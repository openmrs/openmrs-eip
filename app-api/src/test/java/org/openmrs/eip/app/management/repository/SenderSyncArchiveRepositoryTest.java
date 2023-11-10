package org.openmrs.eip.app.management.repository;

import static java.time.ZoneId.systemDefault;
import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.openmrs.eip.component.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_sender_sync_archive.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SenderSyncArchiveRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private SenderSyncArchiveRepository repo;
	
	@Test
	public void findByDateCreatedLessThanEqual_shouldABatchOfMessagesCreateOnOrBeforeThatSpecifiedDate() {
		Date maxDate = Date.from(DateUtils.stringToDate("2022-10-26 21:00:27").atZone(systemDefault()).toInstant());
		Pageable page = PageRequest.of(0, 10);
		
		List<SenderSyncArchive> archives = repo.findByDateCreatedLessThanEqual(maxDate, page);
		
		assertEquals(2, archives.size());
		assertEquals(2l, archives.get(0).getId().longValue());
		assertEquals(3l, archives.get(1).getId().longValue());
	}
	
	@Test
	public void findByDateCreatedLessThanEqual_shouldReturnArchivesBasedOnThePageSize() {
		Date minDate = Date.from(DateUtils.stringToDate("2022-10-26 21:00:27").atZone(systemDefault()).toInstant());
		Pageable page = PageRequest.of(0, 1);
		
		List<SenderSyncArchive> archives = repo.findByDateCreatedLessThanEqual(minDate, page);
		
		assertEquals(1, archives.size());
		assertEquals(2l, archives.get(0).getId().longValue());
	}
}
