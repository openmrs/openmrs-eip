package org.openmrs.eip.app.route.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.management.entity.SenderSyncRequest.SenderRequestStatus.NEW;
import static org.openmrs.eip.app.management.entity.SenderSyncRequest.SenderRequestStatus.PROCESSED;
import static org.openmrs.eip.app.route.sender.SenderTestUtils.getEntities;
import static org.openmrs.eip.app.route.sender.SenderTestUtils.getEntity;
import static org.openmrs.eip.app.sender.SenderConstants.ROUTE_ID_REQUEST_PROCESSOR;
import static org.openmrs.eip.app.sender.SenderConstants.URI_REQUEST_PROCESSOR;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.app.management.entity.SenderSyncRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "logging.level." + ROUTE_ID_REQUEST_PROCESSOR + "=DEBUG")
public class SenderRequestProcessorRouteTest extends BaseSenderRouteTest {
	
	@Override
	public String getTestRouteFilename() {
		return ROUTE_ID_REQUEST_PROCESSOR;
	}
	
	private SenderSyncRequest createSyncRequest(String table, String identifier, String requestUuid) {
		SenderSyncRequest request = new SenderSyncRequest();
        request.setTableName(table);
        request.setIdentifier(identifier);
        request.setRequestUuid(requestUuid);
        request.setDateCreated(new Date());
		SenderTestUtils.saveEntity(request);
		return request;
	}
	
	@Test
	public void shouldDoNothingIfNoSyncRequestsAreFound() {
		producerTemplate.send(URI_REQUEST_PROCESSOR, new DefaultExchange(camelContext));
		
		assertMessageLogged(Level.DEBUG, "No sync requests found");
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_request.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldLoadAndProcessAllNewSyncRequestsSortedByDateCreated() {
		final int messageCount = 3;
		List<SenderSyncRequest> requests = getEntities(SenderSyncRequest.class).stream().filter(m -> m.getStatus() == NEW)
		        .collect(Collectors.toList());
		assertEquals(messageCount, requests.size());
		assertTrue(requests.get(0).getDateCreated().getTime() > (requests.get(2).getDateCreated().getTime()));
		assertTrue(requests.get(1).getDateCreated().getTime() > (requests.get(2).getDateCreated().getTime()));
		DefaultExchange exchange = new DefaultExchange(camelContext);
		
		producerTemplate.send(URI_REQUEST_PROCESSOR, exchange);
		
		assertMessageLogged(Level.INFO, "Fetched " + requests.size() + " sync request(s)");
		assertEquals(messageCount, exchange.getIn().getBody(List.class).size());
		assertEquals(3, ((SenderSyncRequest) exchange.getIn().getBody(List.class).get(0)).getId().intValue());
		assertEquals(1, ((SenderSyncRequest) exchange.getIn().getBody(List.class).get(1)).getId().intValue());
		assertEquals(2, ((SenderSyncRequest) exchange.getIn().getBody(List.class).get(2)).getId().intValue());
		assertEquals(0, getEntities(SenderSyncRequest.class).stream().filter(m -> m.getStatus() == NEW).count());
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void shouldProcessARequestAndTheEntityIsFound() {
		final String table = "patient";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String reqUuid = "request-uuid";
		assertTrue(getEntities(DebeziumEvent.class).isEmpty());
		assertTrue(getEntities(SenderSyncRequest.class).isEmpty());
		SenderSyncRequest request = createSyncRequest(table, uuid, reqUuid);
		assertEquals(1, getEntities(SenderSyncRequest.class).size());
		
		producerTemplate.send(URI_REQUEST_PROCESSOR, new DefaultExchange(camelContext));
		
		assertEquals(1, getEntities(SenderSyncRequest.class).size());
		assertEquals(PROCESSED, getEntity(SenderSyncRequest.class, request.getId()).getStatus());
		List<DebeziumEvent> events = getEntities(DebeziumEvent.class);
		assertEquals(1, events.size());
		DebeziumEvent event = events.get(0);
		assertEquals(table, event.getEvent().getTableName());
		assertEquals("101", event.getEvent().getPrimaryKeyId());
		assertEquals(uuid, event.getEvent().getIdentifier());
		assertEquals(reqUuid, event.getEvent().getRequestUuid());
		assertEquals("r", event.getEvent().getOperation());
		assertFalse(event.getEvent().getSnapshot());
		assertNotNull(event.getDateCreated());
	}
	
	@Test
	public void shouldProcessARequestAndTheEntityIsNotFound() {
		final String table = "patient";
		final String uuid = "patient-uuid";
		final String reqUuid = "request-uuid";
		assertTrue(getEntities(DebeziumEvent.class).isEmpty());
		assertTrue(getEntities(SenderSyncRequest.class).isEmpty());
		SenderSyncRequest request = createSyncRequest(table, uuid, reqUuid);
		assertEquals(1, getEntities(SenderSyncRequest.class).size());
		
		producerTemplate.send(URI_REQUEST_PROCESSOR, new DefaultExchange(camelContext));
		
		assertEquals(1, getEntities(SenderSyncRequest.class).size());
		assertEquals(PROCESSED, getEntity(SenderSyncRequest.class, request.getId()).getStatus());
		List<DebeziumEvent> events = getEntities(DebeziumEvent.class);
		assertEquals(1, events.size());
		DebeziumEvent event = events.get(0);
		assertEquals(table, event.getEvent().getTableName());
		assertEquals("-1", event.getEvent().getPrimaryKeyId());
		assertEquals(uuid, event.getEvent().getIdentifier());
		assertEquals(reqUuid, event.getEvent().getRequestUuid());
		assertEquals("r", event.getEvent().getOperation());
		assertFalse(event.getEvent().getSnapshot());
		assertNotNull(event.getDateCreated());
	}
	
}
