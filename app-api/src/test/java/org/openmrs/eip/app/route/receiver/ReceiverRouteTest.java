package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_IS_FILE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_METADATA;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_ACTIVEMQ_IN_ENDPOINT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_CAMEL_OUTPUT_ENDPOINT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_COMPLEX_OBS_SYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_RECEIVER_MAIN;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_UPDATE_LAST_SYNC_DATE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_COMPLEX_OBS_SYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_UPDATE_LAST_SYNC_DATE;
import static org.openmrs.eip.app.route.TestUtils.getEntities;
import static org.openmrs.eip.app.route.TestUtils.getEntity;
import static org.openmrs.eip.app.route.receiver.ReceiverRouteTest.ACTIVEMQ_IN_ENDPOINT;
import static org.openmrs.eip.app.route.receiver.ReceiverRouteTest.URI_ACTIVEMQ_RESPONSE_PREFIX;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.CustomMessageListenerContainer;
import org.openmrs.eip.app.management.entity.ReceiverSyncRequest;
import org.openmrs.eip.app.management.entity.ReceiverSyncRequest.ReceiverRequestStatus;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.camel.TypeEnum;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@TestPropertySource(properties = PROP_ACTIVEMQ_IN_ENDPOINT + "=" + ACTIVEMQ_IN_ENDPOINT)
@TestPropertySource(properties = PROP_CAMEL_OUTPUT_ENDPOINT + "=" + URI_ACTIVEMQ_RESPONSE_PREFIX + "{0}")
@TestPropertySource(properties = "logging.level." + ROUTE_ID_RECEIVER_MAIN + "=DEBUG")
@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_request.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverRouteTest extends BaseReceiverRouteTest {
	
	protected static final String ACTIVEMQ_IN_ENDPOINT = "direct:" + ROUTE_ID_RECEIVER_MAIN;
	
	protected static final String URI_ACTIVEMQ = ACTIVEMQ_IN_ENDPOINT + "&asyncStartListener=true";
	
	public static final String URI_ACTIVEMQ_RESPONSE_PREFIX = "mock:response.";
	
	@EndpointInject("mock:" + ROUTE_ID_UPDATE_LAST_SYNC_DATE)
	private MockEndpoint mockUpdateSyncStatusEndpoint;
	
	@EndpointInject("mock:" + ROUTE_ID_COMPLEX_OBS_SYNC)
	private MockEndpoint mockComplexObsSyncEndpoint;
	
	@EndpointInject(URI_ACTIVEMQ_RESPONSE_PREFIX + "remote1")
	private MockEndpoint mockActiveMqResponseEndpoint;
	
	@Before
	public void setup() throws Exception {
		Whitebox.setInternalState(CustomMessageListenerContainer.class, "commit", false);
		mockUpdateSyncStatusEndpoint.reset();
		mockComplexObsSyncEndpoint.reset();
		mockActiveMqResponseEndpoint.reset();
		advise(ROUTE_ID_RECEIVER_MAIN, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint(URI_UPDATE_LAST_SYNC_DATE + "?size=65536&blockWhenFull=true")
				        .skipSendToOriginalEndpoint().to(mockUpdateSyncStatusEndpoint);
				interceptSendToEndpoint(URI_COMPLEX_OBS_SYNC).skipSendToOriginalEndpoint().to(mockComplexObsSyncEndpoint);
			}
			
		});
	}
	
	@Override
	public String getTestRouteFilename() {
		return "receiver-route";
	}
	
	@Test
	public void shouldProcessAndSaveASyncMessage() throws Exception {
		final LocalDateTime dateSent = LocalDateTime.now();
		final String uuid = "person-uuid";
		final String msgUuid = "msg-uuid";
		final SyncOperation op = SyncOperation.u;
		assertTrue(TestUtils.getEntities(SyncMessage.class).isEmpty());
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		PersonModel personModel = new PersonModel();
		personModel.setUuid(uuid);
		syncModel.setModel(personModel);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid(msgUuid);
		metadata.setSnapshot(false);
		metadata.setOperation(op.toString());
		SiteInfo siteInfo = getEntity(SiteInfo.class, 1L);
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		metadata.setDateSent(dateSent);
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(JsonUtils.marshall(syncModel));
		mockUpdateSyncStatusEndpoint.expectedMessageCount(1);
		mockUpdateSyncStatusEndpoint.expectedPropertyReceived(EX_PROP_IS_FILE, false);
		
		producerTemplate.send(URI_ACTIVEMQ, exchange);
		
		mockUpdateSyncStatusEndpoint.assertIsSatisfied();
		List<SyncMessage> msgs = getEntities(SyncMessage.class);
		assertEquals(1, msgs.size());
		SyncMessage msg = msgs.get(0);
		assertEquals(PersonModel.class.getName(), msg.getModelClassName());
		assertEquals(uuid, msg.getIdentifier());
		assertEquals(op, msg.getOperation());
		assertEquals(msgUuid, msg.getMessageUuid());
		assertEquals(JsonUtils.marshall(syncModel), msg.getEntityPayload());
		assertEquals(siteInfo, msg.getSite());
		assertEquals(dateSent, msg.getDateSentBySender());
		assertFalse(msg.getSnapshot());
		assertNotNull(msg.getDateCreated());
		assertTrue(Whitebox.getInternalState(CustomMessageListenerContainer.class, "commit"));
	}
	
	@Test
	public void shouldProcessAndSaveAComplexObs() throws Exception {
		final String fileContents = "test";
		final LocalDateTime dateSent = LocalDateTime.now();
		assertTrue(TestUtils.getEntities(SyncMessage.class).isEmpty());
		SyncMetadata metadata = new SyncMetadata();
		SiteInfo siteInfo = getEntity(SiteInfo.class, 1L);
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		metadata.setDateSent(dateSent);
		final String syncMetadata = JsonUtils.marshall(metadata);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_IS_FILE, true);
		exchange.getIn()
		        .setBody(TypeEnum.FILE.getOpeningTag() + syncMetadata + "#" + fileContents + TypeEnum.FILE.getClosingTag());
		mockUpdateSyncStatusEndpoint.expectedMessageCount(1);
		mockUpdateSyncStatusEndpoint.expectedPropertyReceived(EX_PROP_IS_FILE, true);
		mockComplexObsSyncEndpoint.expectedPropertyReceived(EX_PROP_METADATA, syncMetadata);
		mockComplexObsSyncEndpoint.expectedBodiesReceived(fileContents);
		
		producerTemplate.send(URI_ACTIVEMQ, exchange);
		
		mockUpdateSyncStatusEndpoint.assertIsSatisfied();
		mockComplexObsSyncEndpoint.assertIsSatisfied();
		assertTrue(TestUtils.getEntities(SyncMessage.class).isEmpty());
		assertTrue(Whitebox.getInternalState(CustomMessageListenerContainer.class, "commit"));
	}
	
	@Test
	public void shouldProcessAndSaveASyncMessageLinkedToASyncRequest() throws Exception {
		final LocalDateTime dateSent = LocalDateTime.now();
		final String uuid = "person-uuid";
		final String msgUuid = "msg-uuid";
		final SyncOperation op = SyncOperation.r;
		final String requestUuid = "46beb8bd-287c-47f2-9786-a7b98c933c04";
		ReceiverSyncRequest request = getEntity(ReceiverSyncRequest.class, 4L);
		assertEquals(requestUuid, request.getRequestUuid());
		assertEquals(ReceiverRequestStatus.SENT, request.getStatus());
		assertTrue(TestUtils.getEntities(SyncMessage.class).isEmpty());
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		PersonModel personModel = new PersonModel();
		personModel.setUuid(uuid);
		syncModel.setModel(personModel);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid(msgUuid);
		metadata.setSnapshot(false);
		metadata.setOperation(op.toString());
		metadata.setRequestUuid(requestUuid);
		SiteInfo siteInfo = getEntity(SiteInfo.class, 1L);
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		metadata.setDateSent(dateSent);
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(JsonUtils.marshall(syncModel));
		mockUpdateSyncStatusEndpoint.expectedMessageCount(1);
		mockUpdateSyncStatusEndpoint.expectedPropertyReceived(EX_PROP_IS_FILE, false);
		
		producerTemplate.send(URI_ACTIVEMQ, exchange);
		
		mockUpdateSyncStatusEndpoint.assertIsSatisfied();
		assertEquals(1, getEntities(SyncMessage.class).size());
		assertEquals(ReceiverRequestStatus.RECEIVED, getEntity(ReceiverSyncRequest.class, request.getId()).getStatus());
		assertTrue(Whitebox.getInternalState(CustomMessageListenerContainer.class, "commit"));
	}
	
	@Test
	public void shouldProcessAndSaveASyncMessageLinkedToASyncRequestAndTheEntityWasNotFound() throws Exception {
		final LocalDateTime dateSent = LocalDateTime.now();
		final String msgUuid = "msg-uuid";
		final String op = "r";
		final String requestUuid = "46beb8bd-287c-47f2-9786-a7b98c933c04";
		ReceiverSyncRequest request = getEntity(ReceiverSyncRequest.class, 4L);
		assertEquals(requestUuid, request.getRequestUuid());
		assertEquals(ReceiverRequestStatus.SENT, request.getStatus());
		assertTrue(TestUtils.getEntities(SyncMessage.class).isEmpty());
		SyncModel syncModel = new SyncModel();
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid(msgUuid);
		metadata.setSnapshot(false);
		metadata.setOperation(op);
		metadata.setRequestUuid(requestUuid);
		SiteInfo siteInfo = getEntity(SiteInfo.class, 1L);
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		metadata.setDateSent(dateSent);
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(JsonUtils.marshall(syncModel));
		mockUpdateSyncStatusEndpoint.expectedMessageCount(1);
		mockUpdateSyncStatusEndpoint.expectedPropertyReceived(EX_PROP_IS_FILE, false);
		mockActiveMqResponseEndpoint.expectedMessageCount(1);
		
		producerTemplate.send(URI_ACTIVEMQ, exchange);
		
		mockUpdateSyncStatusEndpoint.assertIsSatisfied();
		mockActiveMqResponseEndpoint.assertIsSatisfied();
		assertTrue(TestUtils.getEntities(SyncMessage.class).isEmpty());
		assertEquals(ReceiverRequestStatus.RECEIVED, getEntity(ReceiverSyncRequest.class, request.getId()).getStatus());
		assertTrue(Whitebox.getInternalState(CustomMessageListenerContainer.class, "commit"));
	}
	
}
