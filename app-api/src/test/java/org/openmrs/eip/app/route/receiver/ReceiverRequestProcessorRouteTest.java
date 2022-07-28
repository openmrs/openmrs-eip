package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.management.entity.ReceiverSyncRequest.ReceiverRequestStatus.NEW;
import static org.openmrs.eip.app.management.entity.ReceiverSyncRequest.ReceiverRequestStatus.SENT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_REQUEST_PROCESSOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_REQUEST_PROCESSOR;
import static org.openmrs.eip.app.route.TestUtils.getEntities;
import static org.openmrs.eip.app.route.TestUtils.getEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ToDynamicDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.ReceiverSyncRequest;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.route.TestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_REQUEST_PROCESSOR + "=DEBUG")
public class ReceiverRequestProcessorRouteTest extends BaseReceiverRouteTest {
	
	private static final String TEST_ACTIVEMQ = "mock:openmrs.sync.${exchangeProperty.syncRequest.site.identifier}";
	
	private static final String EX_PROP_SYNC_REQ = "syncRequest";
	
	@EndpointInject(TEST_ACTIVEMQ)
	private MockEndpoint mockActiveMqEndpoint;
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_REQUEST_PROCESSOR;
	}
	
	private ReceiverSyncRequest createSyncRequest(String table, String identifier, String requestUuid) {
		ReceiverSyncRequest request = new ReceiverSyncRequest();
		request.setTableName(table);
		request.setIdentifier(identifier);
		request.setRequestUuid(requestUuid);
		request.setSite(getEntity(SiteInfo.class, 1L));
		request.setDateCreated(new Date());
		TestUtils.saveEntity(request);
		return request;
	}
	
	@Before
	public void setup() throws Exception {
		mockActiveMqEndpoint.reset();
		advise(ROUTE_ID_REQUEST_PROCESSOR, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ToDynamicDefinition.class).selectLast().replace().to(mockActiveMqEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldDoNothingIfNoSyncRequestsAreFound() {
		producerTemplate.send(URI_REQUEST_PROCESSOR, new DefaultExchange(camelContext));
		
		assertMessageLogged(Level.DEBUG, "No sync requests found");
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_request.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldLoadAndProcessAllNewSyncRequestsSortedByDateCreated() throws Exception {
		final int messageCount = 3;
		List<ReceiverSyncRequest> requests = getEntities(ReceiverSyncRequest.class).stream()
		        .filter(m -> m.getStatus() == NEW).collect(Collectors.toList());
		assertEquals(messageCount, requests.size());
		assertTrue(requests.get(0).getDateCreated().getTime() > (requests.get(2).getDateCreated().getTime()));
		assertTrue(requests.get(1).getDateCreated().getTime() > (requests.get(2).getDateCreated().getTime()));
		List<ReceiverSyncRequest> processedRequests = new ArrayList();
		mockActiveMqEndpoint.expectedMessageCount(messageCount);
		mockActiveMqEndpoint.whenAnyExchangeReceived(e -> {
			processedRequests.add(e.getProperty(EX_PROP_SYNC_REQ, ReceiverSyncRequest.class));
		});
		DefaultExchange exchange = new DefaultExchange(camelContext);
		
		producerTemplate.send(URI_REQUEST_PROCESSOR, exchange);
		
		mockActiveMqEndpoint.assertIsSatisfied();
		assertMessageLogged(Level.INFO, "Fetched " + requests.size() + " sync request(s)");
		assertEquals(messageCount, processedRequests.size());
		assertEquals(3, processedRequests.get(0).getId().intValue());
		assertEquals(1, processedRequests.get(1).getId().intValue());
		assertEquals(2, processedRequests.get(2).getId().intValue());
		assertEquals(0, getEntities(ReceiverSyncRequest.class).stream().filter(m -> m.getStatus() == NEW).count());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldProcessARequestAndMarkItAsSent() {
		final String table = "patient";
		final String uuid = "person-uuid";
		final String reqUuid = "request-uuid";
		assertTrue(getEntities(ReceiverSyncRequest.class).isEmpty());
		ReceiverSyncRequest request = createSyncRequest(table, uuid, reqUuid);
		assertEquals(1, getEntities(ReceiverSyncRequest.class).size());
		
		producerTemplate.send(URI_REQUEST_PROCESSOR, new DefaultExchange(camelContext));
		
		assertEquals(1, getEntities(ReceiverSyncRequest.class).size());
		request = getEntity(ReceiverSyncRequest.class, request.getId());
		assertEquals(SENT, request.getStatus());
		assertNotNull(request.getDateSent());
		assertNull(request.getDateReceived());
		assertFalse(request.getFound());
	}
	
}
