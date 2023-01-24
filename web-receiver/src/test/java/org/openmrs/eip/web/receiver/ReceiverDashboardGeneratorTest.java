package org.openmrs.eip.web.receiver;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.web.Dashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ReceiverDashboardGeneratorTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverDashboardGenerator generator;
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql", "classpath:mgt_receiver_sync_msg.sql",
	        "classpath:mgt_receiver_retry_queue.sql",
	        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldGenerateTheDashboard() {
		Dashboard dashboard = generator.generate();
		Map<String, Object> syncMsgs = (Map) dashboard.getEntries().get(ReceiverDashboardGenerator.KEY_SYNC_MSGS);
		Assert.assertEquals(2, syncMsgs.size());
		Assert.assertEquals(4, ((AtomicInteger) syncMsgs.get(ReceiverDashboardGenerator.KEY_TOTAL_COUNT)).get());
		Map<String, Map> entityStatsMap = (Map) syncMsgs.get(ReceiverDashboardGenerator.KEY_ENTITY_STATS);
		Assert.assertEquals(1, entityStatsMap.size());
		Assert.assertEquals(2, entityStatsMap.get(PersonModel.class.getName()).size());
		Assert.assertEquals(3, entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.c));
		Assert.assertEquals(1, entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.u));
		Assert.assertNull(entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.d));
		
		Map<String, Object> errors = (Map) dashboard.getEntries().get(ReceiverDashboardGenerator.KEY_ERRORS);
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals(5, ((AtomicInteger) errors.get(ReceiverDashboardGenerator.KEY_TOTAL_COUNT)).get());
		entityStatsMap = (Map) errors.get(ReceiverDashboardGenerator.KEY_ENTITY_STATS);
		Assert.assertEquals(3, entityStatsMap.size());
		Assert.assertEquals(2, entityStatsMap.get(PersonModel.class.getName()).size());
		Assert.assertEquals(2, entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.c));
		Assert.assertEquals(1, entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.u));
		Assert.assertNull(entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.d));
		Assert.assertEquals(1, entityStatsMap.get(PatientModel.class.getName()).size());
		Assert.assertEquals(1, entityStatsMap.get(PatientModel.class.getName()).get(SyncOperation.c));
		Assert.assertNull(entityStatsMap.get(PatientModel.class.getName()).get(SyncOperation.u));
		Assert.assertNull(entityStatsMap.get(PatientModel.class.getName()).get(SyncOperation.d));
		Assert.assertEquals(1, entityStatsMap.get(OrderModel.class.getName()).size());
		Assert.assertEquals(1, entityStatsMap.get(OrderModel.class.getName()).get(SyncOperation.c));
		Assert.assertNull(entityStatsMap.get(OrderModel.class.getName()).get(SyncOperation.u));
		Assert.assertNull(entityStatsMap.get(OrderModel.class.getName()).get(SyncOperation.d));
		
		Map<String, Object> conflicts = (Map) dashboard.getEntries().get(ReceiverDashboardGenerator.KEY_CONFLICTS);
		Assert.assertEquals(2, conflicts.size());
		Assert.assertEquals(5, ((AtomicInteger) conflicts.get(ReceiverDashboardGenerator.KEY_TOTAL_COUNT)).get());
		entityStatsMap = (Map) conflicts.get(ReceiverDashboardGenerator.KEY_ENTITY_STATS);
		Assert.assertEquals(3, entityStatsMap.size());
		Assert.assertEquals(2, entityStatsMap.get(PersonModel.class.getName()).size());
		Assert.assertEquals(2, entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.c));
		Assert.assertEquals(1, entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.u));
		Assert.assertNull(entityStatsMap.get(PersonModel.class.getName()).get(SyncOperation.d));
		Assert.assertEquals(1, entityStatsMap.get(PatientModel.class.getName()).size());
		Assert.assertEquals(1, entityStatsMap.get(PatientModel.class.getName()).get(SyncOperation.c));
		Assert.assertNull(entityStatsMap.get(PatientModel.class.getName()).get(SyncOperation.u));
		Assert.assertNull(entityStatsMap.get(PatientModel.class.getName()).get(SyncOperation.d));
		Assert.assertEquals(1, entityStatsMap.get(OrderModel.class.getName()).size());
		Assert.assertEquals(1, entityStatsMap.get(OrderModel.class.getName()).get(SyncOperation.c));
		Assert.assertNull(entityStatsMap.get(OrderModel.class.getName()).get(SyncOperation.u));
		Assert.assertNull(entityStatsMap.get(OrderModel.class.getName()).get(SyncOperation.d));
	}
	
}
