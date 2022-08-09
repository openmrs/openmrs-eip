package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ENTITY_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MODEL_CLASS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_PAYLOAD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_RETRY_ITEM;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_SITE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_CLEAR_CACHE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_DBSYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_UPDATE_SEARCH_INDEX;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_DBSYNC;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.model.UserModel;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql({ "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
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
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
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
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = SyncConstants.MGT_DATASOURCE_NAME, transactionManager = SyncConstants.MGT_TX_MGR))
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
		List<String> bodies = new ArrayList();
		mockUpdateSearchIndexEndpoint.whenAnyExchangeReceived(e -> bodies.add(e.getIn().getBody(String.class)));
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		assertEquals(4, bodies.size());
		assertTrue(bodies.contains(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"1bfd940e-32dc-491f-8038-a8f3afe3e35a\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"2bfd940e-32dc-491f-8038-a8f3afe3e35a\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"1cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"2cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadPersonEntityAndUpdateTheSearchIndexForADeleteMessage() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonModel.class;
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
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
		List<String> bodies = new ArrayList();
		mockUpdateSearchIndexEndpoint.whenAnyExchangeReceived(e -> bodies.add(e.getIn().getBody(String.class)));
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		assertEquals(4, bodies.size());
		assertTrue(bodies.contains(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"1bfd940e-32dc-491f-8038-a8f3afe3e35a\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"2bfd940e-32dc-491f-8038-a8f3afe3e35a\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"1cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"2cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadPatientEntityAndClearDbCacheAndUpdateTheSearchIndex() throws Exception {
		final Class<? extends BaseModel> modelClass = PatientModel.class;
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		PatientModel model = new PatientModel();
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
		mockUpdateSearchIndexEndpoint.expectedMessageCount(4);
		List<String> bodies = new ArrayList();
		mockUpdateSearchIndexEndpoint.whenAnyExchangeReceived(e -> bodies.add(e.getIn().getBody(String.class)));
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		assertEquals(4, bodies.size());
		assertTrue(bodies.contains(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"1bfd940e-32dc-491f-8038-a8f3afe3e35a\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"2bfd940e-32dc-491f-8038-a8f3afe3e35a\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"1cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"2cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadPatientEntityAndClearDbCacheAndUpdateTheSearchIndexForADeleteMessage() throws Exception {
		final Class<? extends BaseModel> modelClass = PatientModel.class;
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		PatientModel model = new PatientModel();
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
		mockUpdateSearchIndexEndpoint.expectedMessageCount(4);
		List<String> bodies = new ArrayList();
		mockUpdateSearchIndexEndpoint.whenAnyExchangeReceived(e -> bodies.add(e.getIn().getBody(String.class)));
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		assertEquals(4, bodies.size());
		assertTrue(bodies.contains(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"1bfd940e-32dc-491f-8038-a8f3afe3e35a\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"2bfd940e-32dc-491f-8038-a8f3afe3e35a\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"1cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		assertTrue(bodies.contains(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"2cfd940e-32dc-491f-8038-a8f3afe3e35c\"}"));
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadAPersonNameAndClearDbCacheAndUpdateTheSearchIndex() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonNameModel.class;
		final String uuid = "name-uuid";
		PersonNameModel model = new PersonNameModel();
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
		mockClearCacheEndpoint
		        .expectedBodiesReceived("{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"" + uuid + "\"}");
		mockUpdateSearchIndexEndpoint
		        .expectedBodiesReceived("{\"resource\": \"person\", \"subResource\": \"name\", \"uuid\": \"" + uuid + "\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadAPersonNameAndClearDbCacheAndUpdateTheSearchIndexForADeleteMessage() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonNameModel.class;
		final String uuid = "name-uuid";
		PersonNameModel model = new PersonNameModel();
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
		mockClearCacheEndpoint.expectedBodiesReceived("{\"resource\": \"person\", \"subResource\": \"name\"}");
		mockUpdateSearchIndexEndpoint.expectedBodiesReceived("{\"resource\": \"person\", \"subResource\": \"name\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldALoadPersonAttributeAndClearDbCacheAndUpdateTheSearchIndex() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonAttributeModel.class;
		final String uuid = "attrib-uuid";
		PersonAttributeModel model = new PersonAttributeModel();
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
		mockClearCacheEndpoint.expectedBodiesReceived(
		    "{\"resource\": \"person\", \"subResource\": \"attribute\", \"uuid\": \"" + uuid + "\"}");
		mockUpdateSearchIndexEndpoint.expectedBodiesReceived(
		    "{\"resource\": \"person\", \"subResource\": \"attribute\", \"uuid\": \"" + uuid + "\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadAPersonAttributeAndClearDbCacheAndUpdateTheSearchIndexForADeleteMessage() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonAttributeModel.class;
		final String uuid = "attrib-uuid";
		PersonAttributeModel model = new PersonAttributeModel();
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
		mockClearCacheEndpoint.expectedBodiesReceived("{\"resource\": \"person\", \"subResource\": \"attribute\"}");
		mockUpdateSearchIndexEndpoint.expectedBodiesReceived("{\"resource\": \"person\", \"subResource\": \"attribute\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadAPatientIdentifierAndUpdateTheSearchIndex() throws Exception {
		final Class<? extends BaseModel> modelClass = PatientIdentifierModel.class;
		final String uuid = "id-uuid";
		PatientIdentifierModel model = new PatientIdentifierModel();
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
		mockClearCacheEndpoint.expectedMessageCount(0);
		mockUpdateSearchIndexEndpoint.expectedBodiesReceived(
		    "{\"resource\": \"patient\", \"subResource\": \"identifier\", \"uuid\": \"" + uuid + "\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadAPatientIdentifierAndUpdateTheSearchIndexForADeleteMessage() throws Exception {
		final Class<? extends BaseModel> modelClass = PatientIdentifierModel.class;
		final String uuid = "id-uuid";
		PatientIdentifierModel model = new PatientIdentifierModel();
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
		mockClearCacheEndpoint.expectedMessageCount(0);
		mockUpdateSearchIndexEndpoint.expectedBodiesReceived("{\"resource\": \"patient\", \"subResource\": \"identifier\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadAPersonAddressAndUpdateTheSearchIndex() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonAddressModel.class;
		final String uuid = "address-uuid";
		PersonAddressModel model = new PersonAddressModel();
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
		mockUpdateSearchIndexEndpoint.expectedMessageCount(0);
		mockClearCacheEndpoint.expectedBodiesReceived(
		    "{\"resource\": \"person\", \"subResource\": \"address\", \"uuid\": \"" + uuid + "\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadAPersonAddressAndUpdateTheSearchIndexForADeleteMessage() throws Exception {
		final Class<? extends BaseModel> modelClass = PersonAddressModel.class;
		final String uuid = "address-uuid";
		PersonAddressModel model = new PersonAddressModel();
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
		mockUpdateSearchIndexEndpoint.expectedMessageCount(0);
		mockClearCacheEndpoint.expectedBodiesReceived("{\"resource\": \"person\", \"subResource\": \"address\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldLoadAUserAndClearDbCache() throws Exception {
		final Class<? extends BaseModel> modelClass = UserModel.class;
		final String uuid = "user-uuid";
		UserModel model = new UserModel();
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
		mockUpdateSearchIndexEndpoint.expectedMessageCount(0);
		mockClearCacheEndpoint.expectedBodiesReceived("{\"resource\": \"user\", \"uuid\": \"" + uuid + "\"}");
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void shouldAddTheMessageToTheRetryQueueIfAConflictIsDetected() throws Exception {
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
		SiteInfo siteInfo = TestUtils.getEntity(SiteInfo.class, 1L);
		exchange.setProperty(EX_PROP_SITE, siteInfo);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		mockUpdateSearchIndexEndpoint.expectedMessageCount(0);
		mockClearCacheEndpoint.expectedMessageCount(0);
		mockLoadEndpoint.whenAnyExchangeReceived(e -> {
			throw new ConflictsFoundException();
		});
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
		List<ConflictQueueItem> conflicts = TestUtils.getEntities(ConflictQueueItem.class);
		assertEquals(1, conflicts.size());
		assertEquals(modelClass.getName(), conflicts.get(0).getModelClassName());
		assertEquals(uuid, conflicts.get(0).getIdentifier());
		assertEquals(payLoad, conflicts.get(0).getEntityPayload());
		assertFalse(conflicts.get(0).getResolved());
		assertEquals(siteInfo, conflicts.get(0).getSite());
		assertNotNull(conflicts.get(0).getDateCreated());
	}
	
	@Test
	public void shouldAddTheMessageToTheRetryQueueIfAConflictIsDetectedForARetryItem() throws Exception {
		assertTrue(TestUtils.getEntities(ConflictQueueItem.class).isEmpty());
		final Class<? extends BaseModel> modelClass = UserModel.class;
		SiteInfo siteInfo = TestUtils.getEntity(SiteInfo.class, 1L);
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
		retry.setEntityPayload(payLoad);
		retry.setSite(siteInfo);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_MODEL_CLASS, modelClass.getName());
		exchange.setProperty(EX_PROP_ENTITY_ID, uuid);
		exchange.setProperty(EX_PROP_RETRY_ITEM, retry);
		exchange.getIn().setBody(syncModel);
		mockLoadEndpoint.expectedBodiesReceived(syncModel);
		mockUpdateSearchIndexEndpoint.expectedMessageCount(0);
		mockClearCacheEndpoint.expectedMessageCount(0);
		mockLoadEndpoint.whenAnyExchangeReceived(e -> {
			throw new ConflictsFoundException();
		});
		
		producerTemplate.send(URI_DBSYNC, exchange);
		
		mockLoadEndpoint.assertIsSatisfied();
		mockClearCacheEndpoint.assertIsSatisfied();
		mockUpdateSearchIndexEndpoint.assertIsSatisfied();
		List<ConflictQueueItem> conflicts = TestUtils.getEntities(ConflictQueueItem.class);
		assertEquals(1, conflicts.size());
		assertEquals(modelClass.getName(), conflicts.get(0).getModelClassName());
		assertEquals(uuid, conflicts.get(0).getIdentifier());
		assertEquals(payLoad, conflicts.get(0).getEntityPayload());
		assertFalse(conflicts.get(0).getResolved());
		assertEquals(siteInfo, conflicts.get(0).getSite());
		assertNotNull(conflicts.get(0).getDateCreated());
	}
	
}
