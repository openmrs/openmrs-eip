package org.openmrs.eip.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.component.SyncOperation.c;
import static org.openmrs.eip.component.SyncOperation.u;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.openmrs.eip.web.controller.DashboardHelper;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Import(TestWebConfig.class)
@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SenderBaseDashboardHelperTest extends BaseSenderTest {
	
	private static final String ENTITY_TYPE = DebeziumEvent.class.getSimpleName();
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	@Autowired
	private DelegatingDashboardHelper helper;
	
	private TestDashboardHelper delegate;
	
	@Before
	public void setup() {
		delegate = new TestDashboardHelper(producerTemplate, false);
		Whitebox.setInternalState(helper, DashboardHelper.class, delegate);
	}
	
	@Test
	public void getCategories_shouldGetTheUniqueTableNamesInTheQueue() {
		List<String> tables = helper.getCategories(ENTITY_TYPE);
		assertEquals(3, tables.size());
		assertTrue(tables.contains("visit"));
		assertTrue(tables.contains("encounter"));
		assertTrue(tables.contains("patient"));
	}
	
	@Test
	public void getCount_shouldGetTheTotalCountOfItemsTheQueue() {
		assertEquals(3, helper.getCount(ENTITY_TYPE, null, null).intValue());
	}
	
	@Test
	public void getCount_shouldGetTheEventCountMatchingTheCategoryAndOperation() {
		assertEquals(1, helper.getCount(ENTITY_TYPE, "visit", c).intValue());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getCount_shouldGetTheMessageCountMatchingTheCategoryAndOperation() {
		assertEquals(3, helper.getCount(SenderSyncMessage.class.getSimpleName(), "person", u).intValue());
	}
	
}
