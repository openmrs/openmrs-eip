package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ENTITY_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ERR_MSG;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ERR_TYPE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_FOUND_CONFLICT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_IS_CONFLICT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MODEL_CLASS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_DBSYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_DBSYNC;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.route.TestUtils;
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
		SyncModel syncModel = SyncModel.builder().metadata(new SyncMetadata()).build();
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		assertTrue(exchange.getProperty(EX_PROP_MSG_PROCESSED, Boolean.class));
		assertNull(exchange.getProperty(EX_PROP_FOUND_CONFLICT));
		assertNull(exchange.getProperty(EX_PROP_ERR_TYPE));
		assertNull(exchange.getProperty(EX_PROP_ERR_MSG));
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
		
		assertEquals("Cannot process the message because the entity has 3 message(s) in the conflict queue",
		    getErrorMessage(exchange));
		mockLoadEndpoint.assertIsSatisfied();
		assertNull(exchange.getProperty(EX_PROP_MSG_PROCESSED));
		assertNull(exchange.getProperty(EX_PROP_FOUND_CONFLICT));
		assertNull(exchange.getProperty(EX_PROP_ERR_TYPE));
		assertNull(exchange.getProperty(EX_PROP_ERR_MSG));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldPassIfTheFoundConflictWhileSyncingAConflictItem() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "uuid-1";
		VisitModel model = new VisitModel();
		model.setUuid(uuid);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid("1cfd940e-32dc-491f-8038-a8f3afe3e36d");
		SyncModel syncModel = SyncModel.builder().tableToSyncModelClass(modelClass).model(model).metadata(metadata).build();
		assertTrue(ReceiverTestUtils.hasConflict(modelClass, uuid));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.setProperty(EX_PROP_IS_CONFLICT, true);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		assertTrue(exchange.getProperty(EX_PROP_MSG_PROCESSED, Boolean.class));
		assertNull(exchange.getProperty(EX_PROP_FOUND_CONFLICT));
		assertNull(exchange.getProperty(EX_PROP_ERR_TYPE));
		assertNull(exchange.getProperty(EX_PROP_ERR_MSG));
	}
	
	@Test
	public void shouldGracefullyHandleAConflictException() throws Exception {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, UserModel.class.getName());
		mockLoadEndpoint.whenAnyExchangeReceived(e -> {
			throw new ConflictsFoundException();
		});
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		assertTrue(exchange.getProperty(EX_PROP_FOUND_CONFLICT, Boolean.class));
		assertNull(exchange.getProperty(EX_PROP_MSG_PROCESSED));
	}
	
	@Test
	public void shouldGracefullyHandleAConflictExceptionForARetryItem() throws Exception {
		assertTrue(TestUtils.getEntities(ConflictQueueItem.class).isEmpty());
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, UserModel.class.getName());
		mockLoadEndpoint.whenAnyExchangeReceived(e -> {
			throw new ConflictsFoundException();
		});
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		assertTrue(exchange.getProperty(EX_PROP_FOUND_CONFLICT, Boolean.class));
		assertNull(exchange.getProperty(EX_PROP_MSG_PROCESSED));
	}
	
}
