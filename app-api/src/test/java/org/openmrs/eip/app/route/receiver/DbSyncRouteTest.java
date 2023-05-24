package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ENTITY_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MODEL_CLASS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_CONFLICT_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_ERROR_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_PAYLOAD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_RETRY_ITEM;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_SYNC_MESSAGE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_DBSYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_DBSYNC;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.VisitTypeLight;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.model.UserModel;
import org.openmrs.eip.component.model.VisitModel;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
@TestPropertySource(properties = "logging.level." + ROUTE_ID_DBSYNC + "=DEBUG")
public class DbSyncRouteTest extends BaseReceiverRouteTest {
	
	protected static final String ROUTE_ID_DESTINATION = "msg-processor";
	
	@EndpointInject("mock:" + ROUTE_ID_DESTINATION)
	private MockEndpoint mockLoadEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return "db-sync-route";
	}
	
	@Before
	public void setup() throws Exception {
		mockLoadEndpoint.reset();
		
		advise(ROUTE_ID_DBSYNC, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByToUri("openmrs:load").replace().to(mockLoadEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldCallTheLoadProducer() throws Exception {
		final Class<? extends BaseModel> modelClass = VisitModel.class;
		final String uuid = "visit-uuid";
		VisitModel model = new VisitModel();
		model.setUuid(uuid);
		model.setPatientUuid(PatientLight.class.getName() + "(some-patient-uuid)");
		model.setVisitTypeUuid(VisitTypeLight.class.getName() + "(some-visit-type-uuid)");
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(modelClass);
		syncModel.setModel(model);
		syncModel.setMetadata(new SyncMetadata());
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		assertTrue(exchange.getProperty(EX_PROP_MSG_PROCESSED, Boolean.class));
		assertNull(exchange.getProperty(EX_PROP_MOVED_TO_CONFLICT_QUEUE));
		assertNull(exchange.getProperty(EX_PROP_MOVED_TO_ERROR_QUEUE));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldFailIfTheEntityHasItemsInTheConflictQueue() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "uuid-1";
		assertTrue(ReceiverTestUtils.hasConflict(modelClass, uuid));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		mockLoadEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		assertEquals("Cannot process the message because the entity has 3 message(s) in the DB sync conflict queue",
		    getErrorMessage(exchange));
		mockLoadEndpoint.assertIsSatisfied();
		assertNull(exchange.getProperty(EX_PROP_MSG_PROCESSED));
		assertNull(exchange.getProperty(EX_PROP_MOVED_TO_CONFLICT_QUEUE));
		assertNull(exchange.getProperty(EX_PROP_MOVED_TO_ERROR_QUEUE));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldPassIfTheEntityHasResolvedItemsInTheConflictQueue() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "uuid-2";
		ConflictQueueItem conflict = TestUtils.getEntity(ConflictQueueItem.class, 4L);
		assertEquals(modelClass.getName(), conflict.getModelClassName());
		assertEquals(uuid, conflict.getIdentifier());
		assertTrue(conflict.getResolved());
		PersonModel model = new PersonModel();
		model.setUuid(uuid);
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(modelClass);
		syncModel.setModel(model);
		syncModel.setMetadata(new SyncMetadata());
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldAddTheMessageToTheConflictQueueIfAConflictIsDetected() throws Exception {
		assertTrue(TestUtils.getEntities(ConflictQueueItem.class).isEmpty());
		final Class<? extends BaseModel> modelClass = UserModel.class;
		final String uuid = "user-uuid";
		final String payLoad = "{}";
		UserModel model = new UserModel();
		model.setUuid(uuid);
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(modelClass);
		syncModel.setModel(model);
		syncModel.setMetadata(new SyncMetadata());
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.setProperty(EX_PROP_PAYLOAD, payLoad);
		SyncMessage syncMessage = new SyncMessage();
		syncMessage.setOperation(SyncOperation.c);
		syncMessage.setSnapshot(true);
		syncMessage.setMessageUuid("message-uuid");
		syncMessage.setDateCreated(new Date());
		syncMessage.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		syncMessage.setDateSentBySender(LocalDateTime.now());
		exchange.setProperty(EX_PROP_SYNC_MESSAGE, syncMessage);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		mockLoadEndpoint.whenAnyExchangeReceived(e -> {
			throw new ConflictsFoundException();
		});
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		List<ConflictQueueItem> conflicts = TestUtils.getEntities(ConflictQueueItem.class);
		assertEquals(1, conflicts.size());
		ConflictQueueItem conflict = conflicts.get(0);
		assertEquals(modelClass.getName(), conflict.getModelClassName());
		assertEquals(uuid, conflict.getIdentifier());
		assertEquals(syncMessage.getOperation(), conflict.getOperation());
		assertEquals(payLoad, conflict.getEntityPayload());
		assertEquals(syncMessage.getSite(), conflict.getSite());
		assertEquals(syncMessage.getDateSentBySender(), conflict.getDateSentBySender());
		assertEquals(syncMessage.getMessageUuid(), conflict.getMessageUuid());
		assertEquals(syncMessage.getSnapshot(), conflict.getSnapshot());
		assertEquals(syncMessage.getDateCreated(), conflict.getDateReceived());
		assertFalse(conflict.getResolved());
		assertNotNull(conflict.getDateCreated());
		assertTrue(exchange.getProperty(EX_PROP_MOVED_TO_CONFLICT_QUEUE, Boolean.class));
		assertNull(exchange.getProperty(EX_PROP_MSG_PROCESSED));
	}
	
	@Test
	public void shouldAddTheMessageToTheConflictQueueIfAConflictIsDetectedForARetryItem() throws Exception {
		assertTrue(TestUtils.getEntities(ConflictQueueItem.class).isEmpty());
		final Class<? extends BaseModel> modelClass = UserModel.class;
		final String uuid = "user-uuid";
		final String payLoad = "{}";
		UserModel model = new UserModel();
		model.setUuid(uuid);
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(modelClass);
		syncModel.setModel(model);
		syncModel.setMetadata(new SyncMetadata());
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setModelClassName(modelClass.getName());
		retry.setIdentifier(uuid);
		retry.setOperation(SyncOperation.u);
		retry.setEntityPayload(payLoad);
		retry.setSnapshot(true);
		retry.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		retry.setDateSentBySender(LocalDateTime.now());
		retry.setDateReceived(new Date());
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.setProperty(EX_PROP_RETRY_ITEM, retry);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		mockLoadEndpoint.whenAnyExchangeReceived(e -> {
			throw new ConflictsFoundException();
		});
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		List<ConflictQueueItem> conflicts = TestUtils.getEntities(ConflictQueueItem.class);
		assertEquals(1, conflicts.size());
		ConflictQueueItem conflict = conflicts.get(0);
		assertEquals(modelClass.getName(), conflict.getModelClassName());
		assertEquals(uuid, conflict.getIdentifier());
		assertEquals(retry.getOperation(), conflict.getOperation());
		assertEquals(payLoad, conflict.getEntityPayload());
		assertEquals(retry.getDateSentBySender(), conflict.getDateSentBySender());
		assertEquals(retry.getMessageUuid(), conflict.getMessageUuid());
		assertEquals(retry.getSnapshot(), conflict.getSnapshot());
		assertEquals(retry.getDateReceived(), conflict.getDateReceived());
		assertEquals(retry.getSite(), conflict.getSite());
		assertFalse(conflict.getResolved());
		assertNotNull(conflict.getDateCreated());
		assertTrue(exchange.getProperty(EX_PROP_MOVED_TO_CONFLICT_QUEUE, Boolean.class));
		assertNull(exchange.getProperty(EX_PROP_MSG_PROCESSED));
	}
	
}
