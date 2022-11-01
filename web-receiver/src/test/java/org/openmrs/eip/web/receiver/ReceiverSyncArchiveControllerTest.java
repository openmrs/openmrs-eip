package org.openmrs.eip.web.receiver;

import static org.junit.Assert.assertEquals;
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
	public void shouldGetAllArchives() {
		Map result = controller.getAll();
		assertEquals(2, result.size());
		assertEquals(3, result.get("count"));
		assertEquals(3, ((List) result.get("items")).size());
		
	}
	
	@Test
	public void shouldReturnArchivesMatchingTheSpecifiedStartAndEndDates() throws Exception {
		Map response = controller.searchByDateReceived("2022-10-25", "2022-10-30");
		assertEquals(2, response.size());
		assertEquals(1, response.get("count"));
		assertEquals(1, ((List) response.get("items")).size());
		assertEquals(3, ((List<ReceiverSyncArchive>) response.get("items")).get(0).getId().longValue());
	}
	
	@Test
	public void shouldReturnArchivesMatchingTheSpecifiedEndDate() throws Exception {
		Map response = controller.searchByDateReceived(null, "2022-10-23");
		assertEquals(2, response.size());
		assertEquals(2, response.get("count"));
		assertEquals(2, ((List) response.get("items")).size());
		assertEquals(1, ((List<ReceiverSyncArchive>) response.get("items")).get(0).getId().longValue());
		assertEquals(2, ((List<ReceiverSyncArchive>) response.get("items")).get(1).getId().longValue());
	}
	
	@Test
	public void shouldReturnArchivesMatchingTheSpecifiedStartDate() throws Exception {
		Map response = controller.searchByDateReceived("2022-10-23", null);
		assertEquals(2, response.size());
		assertEquals(2, response.get("count"));
		assertEquals(2, ((List) response.get("items")).size());
		assertEquals(2, ((List<ReceiverSyncArchive>) response.get("items")).get(0).getId().longValue());
		assertEquals(3, ((List<ReceiverSyncArchive>) response.get("items")).get(1).getId().longValue());
	}
	
}
