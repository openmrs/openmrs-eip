package org.openmrs.eip.app.receiver;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType.SEND_RESPONSE;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.DateUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.jayway.jsonpath.JsonPath;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, ReceiverUtils.class })
public class SyncResponseSenderTest {
	
	private static final String SITE_IDENTIFIER = "test-site-id";
	
	private static final String QUEUE_NAME = "test-queue";
	
	@Mock
	private ReceiverActiveMqMessagePublisher mockPublisher;
	
	@Mock
	private SiteInfo mockSite;
	
	@Mock
	private ConnectionFactory mockConnFactory;
	
	@Mock
	private PostSyncActionRepository mockRepo;
	
	private SyncResponseSender sender;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(ReceiverUtils.class);
		when(SyncContext.getBean(ReceiverActiveMqMessagePublisher.class)).thenReturn(mockPublisher);
		when(mockSite.getIdentifier()).thenReturn(SITE_IDENTIFIER);
		when(mockPublisher.getCamelOutputEndpoint(SITE_IDENTIFIER)).thenReturn("activemq:" + QUEUE_NAME);
		sender = new SyncResponseSender(mockSite);
		Whitebox.setInternalState(sender, ConnectionFactory.class, mockConnFactory);
		Whitebox.setInternalState(sender, PostSyncActionRepository.class, mockRepo);
	}
	
	@Test
	public void shouldFailForAnInvalidActiveMqEndpoint() {
		when(mockSite.getIdentifier()).thenReturn(SITE_IDENTIFIER);
		final String endpoint = "jms:" + QUEUE_NAME;
		when(mockPublisher.getCamelOutputEndpoint(SITE_IDENTIFIER)).thenReturn("jms:" + QUEUE_NAME);
		Exception e = assertThrows(EIPException.class, () -> {
			new SyncResponseSender(mockSite);
		});
		
		assertEquals(endpoint + " is an invalid message broker endpoint value for outbound messages", e.getMessage());
	}
	
	@Test
	public void getNextBatch_shouldInvokeTheRepoToFetchTheNextBatchOfUnprocessedSyncResponseActions() {
		sender.getNextBatch();
		
		verify(mockRepo).getBatchOfPendingResponseActions(mockSite, sender.pageable);
	}
	
	@Test
	public void process_shouldProcessTheActionsAndMarkThemAsCompleted() throws Exception {
		sender = Mockito.spy(sender);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		sender = Mockito.spy(sender);
		doNothing().when(sender).sendResponsesInBatch(actions);
		
		sender.process(actions);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, true, null);
		PowerMockito.verifyStatic(ReceiverUtils.class, never());
		ReceiverUtils.updatePostSyncActionStatuses(anyList(), ArgumentMatchers.eq(false), isNull());
	}
	
	@Test
	public void process_shouldMarkActionsAsFailedIfAnErrorOccurs() throws Exception {
		sender = Mockito.spy(sender);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		final String errMsg = "Testing";
		EIPException e = new EIPException(errMsg);
		Mockito.doThrow(e).when(sender).sendResponsesInBatch(actions);
		
		sender.process(actions);
		
		PowerMockito.verifyStatic(ReceiverUtils.class, never());
		ReceiverUtils.updatePostSyncActionStatuses(anyList(), ArgumentMatchers.eq(true), isNull());
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, false, e.toString());
	}
	
	@Test
	public void process_shouldSetStatusMessageToTheRootCauseIfAnErrorOccurs() throws Exception {
		sender = Mockito.spy(sender);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		final String rootCauseMsg = "test root error";
		Exception root = new ActiveMQException(rootCauseMsg);
		Exception e = new EIPException("test1", new Exception("test2", root));
		Mockito.doThrow(e).when(sender).sendResponsesInBatch(actions);
		
		sender.process(actions);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, false, root.toString());
	}
	
	@Test
	public void process_shouldTruncateTheErrorMessageIfLongerThan1024() throws Exception {
		sender = Mockito.spy(sender);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		final String errMsg = RandomStringUtils.randomAscii(1025);
		EIPException e = new EIPException(errMsg);
		Mockito.doThrow(e).when(sender).sendResponsesInBatch(actions);
		
		sender.process(actions);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, false, e.toString().substring(0, 1024));
	}
	
	@Test
	public void sendResponsesInBatch_shouldSendTheGeneratedResponses() throws Exception {
		final Connection mockConn = mock(Connection.class);
		final Session mockSession = mock(Session.class);
		final MessageProducer mockProducer = mock(MessageProducer.class);
		final Queue mockQueue = mock(Queue.class);
		final PostSyncAction action1 = new PostSyncAction(new SyncedMessage(), null);
		final PostSyncAction action2 = new PostSyncAction(new SyncedMessage(), null);
		final List<PostSyncAction> actions = Arrays.asList(action1, action2);
		final String text1 = "response1";
		final String text2 = "response2";
		final TextMessage textMsg1 = mock(TextMessage.class);
		final TextMessage textMsg2 = mock(TextMessage.class);
		when(mockConnFactory.createConnection()).thenReturn(mockConn);
		when(mockConn.createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createQueue(QUEUE_NAME)).thenReturn(mockQueue);
		when(mockSession.createProducer(mockQueue)).thenReturn(mockProducer);
		List<String> responses = Arrays.asList(text1, text2);
		sender = Mockito.spy(sender);
		when(sender.generateResponses(Arrays.asList(action1, action2))).thenReturn(responses);
		when(mockSession.createTextMessage(text1)).thenReturn(textMsg1);
		when(mockSession.createTextMessage(text2)).thenReturn(textMsg2);
		
		sender.sendResponsesInBatch(actions);
		
		verify(mockProducer).close();
		verify(mockSession).close();
		verify(mockConn).close();
		verify(mockProducer).send(textMsg1);
		verify(mockProducer).send(textMsg2);
		verify(mockSession).commit();
	}
	
	@Test
	public void generateResponses_shouldGenerateResponses() throws Exception {
		Date date = new Date();
		final String uuid1 = "uuid-1";
		final String uuid2 = "uuid-2";
		final SyncedMessage msg1 = new SyncedMessage();
		msg1.setIdentifier(uuid1);
		msg1.setDateReceived(new Date());
		final SyncedMessage msg2 = new SyncedMessage();
		msg2.setIdentifier(uuid2);
		msg2.setDateReceived(new Date());
		final PostSyncAction action1 = new PostSyncAction(msg1, SEND_RESPONSE);
		final PostSyncAction action2 = new PostSyncAction(msg2, SEND_RESPONSE);
		final List<PostSyncAction> actions = Arrays.asList(action1, action2);
		
		List<String> responses = sender.generateResponses(actions);
		
		for (int i = 0; i < responses.size(); i++) {
			PostSyncAction a = actions.get(i);
			assertEquals(a.getMessage().getMessageUuid(), JsonPath.read(responses.get(i), "messageUuid"));
			LocalDateTime dateReceived = ZonedDateTime
			        .parse(JsonPath.read(responses.get(i), "dateReceived"), ISO_OFFSET_DATE_TIME)
			        .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
			assertEquals(DateUtils.dateToLocalDateTime(a.getMessage().getDateReceived()), dateReceived);
			Instant dateSentByReceiver = ZonedDateTime
			        .parse(JsonPath.read(responses.get(i), "dateSentByReceiver"), ISO_OFFSET_DATE_TIME)
			        .withZoneSameInstant(ZoneId.systemDefault()).toInstant();
			Assert.assertTrue(dateSentByReceiver.equals(date.toInstant()) || dateSentByReceiver.isAfter(date.toInstant()));
		}
	}
	
}
