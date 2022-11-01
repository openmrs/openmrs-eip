package org.openmrs.eip.web.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_archive.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverSyncArchiveControllerTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverSyncArchiveController controller;
	
	@Test
	public void shouldGetAllArchiveMessages() {
		Map result = controller.getAll();
		assertEquals(2, result.size());
		assertEquals(3, result.get("count"));
		assertEquals(3, ((List) result.get("items")).size());
	}
	
	@Test
	public void shouldDoSearchByPeriod() {
		// Do Search with startDate and EndDate
		String stardDate = "2022-10-25";
		String endDate = "2022-10-30";
		
		Map results = controller.doSearchByPeriod(stardDate, endDate, ReceiverSyncArchive.DATE_CREATED);
		assertEquals(1, results.get("count"));
		assertEquals(2, results.size());
		assertNotNull(((List<ReceiverSyncArchive>) results.get("items")).get(0).getDateCreated());
		assertNotNull(((List<ReceiverSyncArchive>) results.get("items")).get(0).getDateReceived());
		assertNotNull(((List<ReceiverSyncArchive>) results.get("items")).get(0).getDateSentBySender());
	}
	
	@Test
	public void shouldDoSearchByDateReceivedWithEndDate() {
		// Do Search with EndDate
		String stardDate = "";
		String endDate = "2022-10-23";
		
		Map result = controller.doSearchByPeriod(stardDate, endDate, ReceiverSyncArchive.DATE_CREATED);
		assertEquals(1, result.get("count"));
		assertEquals("8cd540b1-dbcc-4dc0-bb85-d5d2763bbw6e",
		    ((List<ReceiverSyncArchive>) result.get("items")).get(0).getIdentifier());
		assertNotNull(((List<ReceiverSyncArchive>) result.get("items")).get(0).getDateCreated());
		assertNotNull(((List<ReceiverSyncArchive>) result.get("items")).get(0).getDateReceived());
		assertNotNull(((List<ReceiverSyncArchive>) result.get("items")).get(0).getDateSentBySender());
		
	}
	
	@Test
	public void shouldDoSearchByDateReceivedWithStartDate() {
		// Do Search with startDate
		String stardDate = "2022-10-30";
		String endDate = "";
		
		Map res = controller.doSearchByPeriod(stardDate, endDate, ReceiverSyncArchive.DATE_CREATED);
		assertEquals(0, res.get("count"));
		assertEquals(0, ((List) res.get("items")).size());
		assertEquals(2, res.size());
		
	}
	
}
