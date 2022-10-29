package org.openmrs.eip.web.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.openmrs.eip.component.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_sender_sync_archive.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SenderSyncArchiveControllerTest extends BaseSenderTest {
	
	@Autowired
	private SenderSyncArchiveController controller;
	
	@Test
	public void shouldGetAllArchivesMessages() {
		Map result = controller.getAll();
		assertEquals(2, result.size());
		assertEquals(3, result.get("count"));
		assertEquals(3, ((List) result.get("items")).size());
		
	}
	
	@Test
	public void shouldDosearchByEventDate() {
		
		String stardDate = "2022-10-25";
		String endDate = "2022-10-30";
		
		Map results = controller.doSearchByPeriod(stardDate, endDate, SenderSyncArchive.EVENT_DATE);
		assertEquals(1, results.get("count"));
		assertEquals(2, results.size());
	}
	
	@Test
	public void shouldDosearchByEventDateWithEndDate() {
		
		String stardDate = "";
		String endDate = "2022-10-23";
		
		Map results = controller.doSearchByPeriod(stardDate, endDate, SenderSyncArchive.EVENT_DATE);
		assertEquals(1, results.get("count"));
		assertEquals(2, results.size());
		assertEquals("4316548b-8803-43b7-bd10-49f26bc26dde",
		    ((List<SenderSyncArchive>) results.get("items")).get(0).getMessageUuid());
	}
	
	@Test
	public void shouldDosearchByEventDateWithStartDate() {
		
		// Do Search with startDate
		String stardDate = "2022-10-30";
		String endDate = "";
		
		Map results = controller.doSearchByPeriod(stardDate, endDate, SenderSyncArchive.EVENT_DATE);
		assertEquals(0, results.get("count"));
		assertEquals(0, ((List) results.get("items")).size());
		assertEquals(2, results.size());
		
	}
	
}
