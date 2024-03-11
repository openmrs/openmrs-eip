package org.openmrs.eip.app.sender;

import static java.time.ZoneId.systemDefault;
import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.management.entity.sender.SenderSyncMessage.SenderSyncMessageStatus.SENT;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_SENDER_ID;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.repository.SenderSyncMessageRepository;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.TestPropertySource;

import com.jayway.jsonpath.JsonPath;

@TestPropertySource(properties = PROP_SENDER_ID + "=" + SenderSyncMessageProcessorTest.SENDER_ID)
@TestPropertySource(properties = "camel.output.endpoint=" + SenderSyncMessageProcessorTest.URI_ACTIVEMQ_SYNC)
public class SenderSyncMessageProcessorTest extends BaseSenderTest {
	
	protected static final String SENDER_ID = "test-sender-id";
	
	protected static final String QUEUE_NAME = "openmrs.test.sync";
	
	protected static final String URI_ACTIVEMQ_SYNC = "activemq:" + QUEUE_NAME;
	
	@Autowired
	private SenderSyncMessageProcessor processor;
	
	@Autowired
	private SenderSyncMessageRepository repo;
	
	@Autowired
	private JmsTemplate mockTemplate;
	
	@Test
	public void shouldSubmitTheSyncMessageToActiveMqAndUpdateIt() {
		final String table = "patient";
		final String uuid = "patient-uuid";
		final String msgUuid = "msg-uuid";
		final String op = "u";
		assertTrue(repo.findAll().isEmpty());
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setTableName(table);
		msg.setIdentifier(uuid);
		msg.setMessageUuid(msgUuid);
		msg.setOperation(op);
		msg.setSnapshot(true);
		msg.setDateCreated(new Date());
		msg.setEventDate(new Date());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation(op);
		metadata.setMessageUuid(msgUuid);
		metadata.setSnapshot(true);
		PersonModel model = new PersonModel();
		model.setUuid(uuid);
		msg.setData(JsonUtils.marshall(new SyncModel(PersonModel.class, model, metadata)));
		TestUtils.saveEntity(msg);
		assertEquals(1, repo.findAll().size());
		
		processor.processItem(msg);
		
		List<SenderSyncMessage> syncMessages = repo.findAll();
		assertEquals(1, syncMessages.size());
		assertEquals(SENT, repo.findById(msg.getId()).get().getStatus());
		String syncPayload = syncMessages.get(0).getData();
		assertEquals(PersonModel.class.getName(), JsonPath.read(syncPayload, "tableToSyncModelClass"));
		assertEquals(uuid, JsonPath.read(syncPayload, "model.uuid"));
		assertEquals(SENDER_ID, JsonPath.read(syncPayload, "metadata.sourceIdentifier"));
		assertEquals(op, JsonPath.read(syncPayload, "metadata.operation"));
		assertEquals(msgUuid, JsonPath.read(syncPayload, "metadata.messageUuid"));
		assertTrue(JsonPath.read(syncPayload, "metadata.snapshot"));
		Instant dateSentInstant = parse(JsonPath.read(syncPayload, "metadata.dateSent"), ISO_OFFSET_DATE_TIME)
		        .withZoneSameInstant(systemDefault()).toInstant();
		assertEquals(msg.getDateSent(), Date.from(dateSentInstant));
		assertNull(JsonPath.read(syncPayload, "metadata.requestUuid"));
		Mockito.verify(mockTemplate).convertAndSend(QUEUE_NAME, syncPayload);
	}
	
}
