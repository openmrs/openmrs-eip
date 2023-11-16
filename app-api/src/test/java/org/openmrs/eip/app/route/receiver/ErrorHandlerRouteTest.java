package org.openmrs.eip.app.route.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ERR_MSG;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ERR_TYPE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_FAILED_ENTITIES;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_RETRY_ITEM;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_RETRY_ITEM_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_ERROR_HANDLER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_ERROR_HANDLER;
import static org.openmrs.eip.app.route.TestUtils.getEntity;

import java.util.HashSet;
import java.util.Set;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientModel;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
@TestPropertySource(properties = "logging.level." + ROUTE_ID_ERROR_HANDLER + "=DEBUG")
public class ErrorHandlerRouteTest extends BaseReceiverRouteTest {
	
	@Override
	public String getTestRouteFilename() {
		return "error-handler-route";
	}
	
	@Before
	public void setup() throws Exception {
		Whitebox.setInternalState(AppUtils.class, "shuttingDown", false);
		
		advise(ROUTE_ID_ERROR_HANDLER, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				onException(EIPException.class).to(URI_ERROR_HANDLER);
			}
			
		});
	}
	
	@Test
	public void shouldSkipIfTheApplicationIsStopping() {
		Whitebox.setInternalState(AppUtils.class, "shuttingDown", true);
		final int errorCount = TestUtils.getEntities(ReceiverRetryQueueItem.class).size();
		
		producerTemplate.send(URI_ERROR_HANDLER, new DefaultExchange(camelContext));
		
		assertMessageLogged(Level.INFO, "Ignoring the error because the application is shutting down");
		assertEquals(errorCount, TestUtils.getEntities(ReceiverRetryQueueItem.class).size());
	}
	
	@Test
	public void shouldAddTheMessageToTheErrorQueue() {
		final String errorMsg = "test error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(errorMsg));
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(EIPException.class.getName(), exchange.getProperty(EX_PROP_ERR_TYPE));
		assertEquals(errorMsg, exchange.getProperty(EX_PROP_ERR_MSG));
	}
	
	@Test
	public void shouldSetExceptionTypeToRootCause() {
		final String rootCauseMsg = "test root error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException("test1", new Exception("test2", new ActiveMQException(rootCauseMsg))));
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(ActiveMQException.class.getName(), exchange.getProperty(EX_PROP_ERR_TYPE));
		assertEquals(rootCauseMsg, exchange.getProperty(EX_PROP_ERR_MSG));
	}
	
	@Test
	public void shouldProcessARetryItem() {
		final Long retryItemId = 1L;
		final String newErrorMsg = "new test error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(newErrorMsg));
		ReceiverRetryQueueItem retryItem = getEntity(ReceiverRetryQueueItem.class, retryItemId);
		assertNull(retryItem.getDateChanged());
		assertEquals(Exception.class.getName(), retryItem.getExceptionType());
		final int errorItemCount = TestUtils.getEntities(ReceiverRetryQueueItem.class).size();
		exchange.setProperty(EX_PROP_RETRY_ITEM_ID, retryItemId);
		exchange.setProperty(EX_PROP_RETRY_ITEM, retryItem);
		exchange.setProperty(EX_PROP_FAILED_ENTITIES, new HashSet());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(errorItemCount, TestUtils.getEntities(ReceiverRetryQueueItem.class).size());
		retryItem = getEntity(ReceiverRetryQueueItem.class, retryItemId);
		assertNotNull(retryItem.getDateChanged());
		assertEquals(EIPException.class.getName(), retryItem.getExceptionType());
		assertEquals(newErrorMsg, retryItem.getMessage());
		Set<String> failedEntities = exchange.getProperty(EX_PROP_FAILED_ENTITIES, Set.class);
		assertEquals(2, failedEntities.size());
		assertTrue(failedEntities.contains(retryItem.getModelClassName() + "#" + retryItem.getIdentifier()));
		assertTrue(failedEntities.contains(PatientModel.class.getName() + "#" + retryItem.getIdentifier()));
	}
	
	@Test
	public void shouldTruncateTheErrorMessageIfItIsLongerThan1024Characters() {
		final String errorMsg = RandomStringUtils.randomAscii(1025);
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(errorMsg));
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(EIPException.class.getName(), exchange.getProperty(EX_PROP_ERR_TYPE));
		assertEquals(errorMsg.substring(0, 1024), exchange.getProperty(EX_PROP_ERR_MSG));
	}
	
	@Test
	public void shouldLoadTheRetryItemIfNotSetOnTheExchangeWhenProcessARetryItem() {
		final Long retryItemId = 1L;
		final String newErrorMsg = "new test error";
		DefaultExchange exchange = new DefaultExchange(camelContext);
		exchange.setException(new EIPException(newErrorMsg));
		ReceiverRetryQueueItem retryItem = getEntity(ReceiverRetryQueueItem.class, retryItemId);
		assertNull(retryItem.getDateChanged());
		assertEquals(Exception.class.getName(), retryItem.getExceptionType());
		final int errorItemCount = TestUtils.getEntities(ReceiverRetryQueueItem.class).size();
		exchange.setProperty(EX_PROP_RETRY_ITEM_ID, retryItemId);
		exchange.setProperty(EX_PROP_RETRY_ITEM, retryItem);
		exchange.setProperty(EX_PROP_FAILED_ENTITIES, new HashSet());
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertEquals(errorItemCount, TestUtils.getEntities(ReceiverRetryQueueItem.class).size());
		retryItem = getEntity(ReceiverRetryQueueItem.class, retryItemId);
		assertNotNull(retryItem.getDateChanged());
		assertEquals(EIPException.class.getName(), retryItem.getExceptionType());
		assertEquals(newErrorMsg, retryItem.getMessage());
	}
	
	@Test
	public void shouldSkipIfTheErrorWasEncounteredWhileSyncingAConflictItem() {
		final int errorCount = TestUtils.getEntities(ReceiverRetryQueueItem.class).size();
		final Exception exception = new EIPException("test");
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(ReceiverConstants.EX_PROP_IS_CONFLICT, true);
		exchange.setException(exception);
		
		producerTemplate.send(URI_ERROR_HANDLER, exchange);
		
		assertMessageLogged(Level.DEBUG, "An error was encountered while syncing a conflict item");
		assertEquals(exception, exchange.getProperty(Constants.EX_PROP_EXCEPTION));
		assertEquals(errorCount, TestUtils.getEntities(ReceiverRetryQueueItem.class).size());
	}
	
}
