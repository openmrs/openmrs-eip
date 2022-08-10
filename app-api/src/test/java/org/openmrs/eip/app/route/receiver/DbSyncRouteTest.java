package org.openmrs.eip.app.route.receiver;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ENTITY_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MODEL_CLASS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_CLEAR_CACHE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_DBSYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_UPDATE_SEARCH_INDEX;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_DBSYNC;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql({ "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
@TestPropertySource(properties = "logging.level." + ROUTE_ID_DBSYNC + "=DEBUG")
public class DbSyncRouteTest extends BaseReceiverRouteTest {
	
	protected static final String ROUTE_ID_DESTINATION = "msg-processor";
	
	@EndpointInject("mock:" + ROUTE_ID_DESTINATION)
	private MockEndpoint mockLoadEndpoint;
	
	@EndpointInject("mock:" + ROUTE_ID_UPDATE_SEARCH_INDEX)
	private MockEndpoint mockUpdateSearchIndexEndpoint;
	
	@EndpointInject("mock:" + ROUTE_ID_CLEAR_CACHE)
	private MockEndpoint mockClearCacheEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return "db-sync-route";
	}
	
	@Before
	public void setup() throws Exception {
		mockLoadEndpoint.reset();
		mockUpdateSearchIndexEndpoint.reset();
		mockClearCacheEndpoint.reset();
		
		advise(ROUTE_ID_DBSYNC, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				interceptSendToEndpoint("direct:" + ROUTE_ID_UPDATE_SEARCH_INDEX).skipSendToOriginalEndpoint()
				        .to(mockUpdateSearchIndexEndpoint);
				interceptSendToEndpoint("direct:" + ROUTE_ID_CLEAR_CACHE).skipSendToOriginalEndpoint()
				        .to(mockClearCacheEndpoint);
				weaveByToUri("openmrs:load").replace().to(mockLoadEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldFailIfTheEntityHasItemsInTheConflictQueue() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "uuid-1";
		assertTrue(ReceiverTestUtils.hasConflict(modelClass, uuid));
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		mockLoadEndpoint.expectedMessageCount(0);
		mockUpdateSearchIndexEndpoint.expectedMessageCount(0);
		mockClearCacheEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		assertEquals("Cannot process the message because the entity has 3 message(s) in the DB sync conflict queue",
		    getErrorMessage(exchange));
		mockLoadEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldPassIfTheEntityHasResolvedItemsInTheConflictQueue() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "uuid-2";
		ConflictQueueItem conflict = TestUtils.getEntity(ConflictQueueItem.class, 4L);
		assertEquals(modelClass.getName(), conflict.getModelClassName());
		assertEquals(uuid, conflict.getIdentifier());
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
		mockClearCacheEndpoint.expectedBodiesReceived("{\"resource\": \"person\", \"uuid\": \"" + uuid + "\"}");
		mockUpdateSearchIndexEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadPersonEntityAndClearDbCacheAndUpdateTheSearchIndex() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
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
		mockClearCacheEndpoint.expectedBodiesReceived("{\"resource\": \"person\", \"uuid\": \"" + uuid + "\"}");
		mockUpdateSearchIndexEndpoint.expectedBodiesReceived(asList(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"1bfd940e-32dc-491f-8038-a8f3afe3e35a\"}",
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"2bfd940e-32dc-491f-8038-a8f3afe3e35a\"}",
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"1cfd940e-32dc-491f-8038-a8f3afe3e35c\"}",
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"2cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
		
	}
	
	@Test
	public void shouldLoadPersonEntityAndUpdateTheSearchIndexForADeleteEvent() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "some-uuid";
		PersonModel model = new PersonModel();
		model.setUuid(uuid);
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(modelClass);
		syncModel.setModel(model);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("d");
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		mockClearCacheEndpoint.expectedBodiesReceived("{\"resource\": \"person\"}");
		mockUpdateSearchIndexEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
}
