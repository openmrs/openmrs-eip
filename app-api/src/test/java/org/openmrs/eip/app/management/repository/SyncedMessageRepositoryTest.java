package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.route.TestUtils.getEntity;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_synced_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SyncedMessageRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private SyncedMessageRepository repo;
	
	@Test
	public void getBatchOfMessagesForItemizing_shouldReturnAOrderedBatchOfUnItemizedMessages() {
		SiteInfo site = getEntity(SiteInfo.class, 1L);
		Pageable page = PageRequest.of(0, 10);
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForItemizing(site, page);
		
		assertEquals(3, msgs.size());
		assertEquals(3l, msgs.get(0).getId().longValue());
		assertEquals(4l, msgs.get(1).getId().longValue());
		assertEquals(2l, msgs.get(2).getId().longValue());
		
		site = getEntity(SiteInfo.class, 2L);
		
		msgs = repo.getBatchOfMessagesForItemizing(site, page);
		
		assertEquals(1, msgs.size());
		assertEquals(5l, msgs.get(0).getId().longValue());
	}
	
	@Test
	public void getBatchOfMessagesForItemizing_shouldReturnResultsBasedOnThePageSize() {
		SiteInfo site = getEntity(SiteInfo.class, 1L);
		assertEquals(3, repo.getBatchOfMessagesForItemizing(site, Pageable.unpaged()).size());
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForItemizing(site, PageRequest.of(0, 2));
		
		assertEquals(2, msgs.size());
		assertEquals(3l, msgs.get(0).getId().longValue());
		assertEquals(4l, msgs.get(1).getId().longValue());
	}
	
	@Test
	public void getBatchOfMessagesForEviction_shouldReturnAOrderedBatchOfMessagesToEvict() {
		SiteInfo site = getEntity(SiteInfo.class, 3L);
		Pageable page = PageRequest.of(0, 10);
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForEviction(site, page);
		
		assertEquals(3, msgs.size());
		assertEquals(10l, msgs.get(0).getId().longValue());
		assertEquals(11l, msgs.get(1).getId().longValue());
		assertEquals(9l, msgs.get(2).getId().longValue());
		assertTrue(repo.getBatchOfMessagesForEviction(getEntity(SiteInfo.class, 2L), page).isEmpty());
	}
	
	@Test
	public void getBatchOfMessagesForEviction_shouldReturnResultsBasedOnThePageSize() {
		SiteInfo site = getEntity(SiteInfo.class, 3L);
		assertEquals(3, repo.getBatchOfMessagesForEviction(site, Pageable.unpaged()).size());
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForEviction(site, PageRequest.of(0, 2));
		
		assertEquals(2, msgs.size());
		assertEquals(10l, msgs.get(0).getId().longValue());
		assertEquals(11l, msgs.get(1).getId().longValue());
	}
	
	@Test
	public void getBatchOfMessagesForIndexing_shouldReturnAOrderedBatchOfMessagesToUpdate() {
		SiteInfo site = getEntity(SiteInfo.class, 4L);
		Pageable page = PageRequest.of(0, 10);
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForIndexing(site, page);
		
		assertEquals(3, msgs.size());
		assertEquals(15l, msgs.get(0).getId().longValue());
		assertEquals(16l, msgs.get(1).getId().longValue());
		assertEquals(14l, msgs.get(2).getId().longValue());
		assertTrue(repo.getBatchOfMessagesForIndexing(getEntity(SiteInfo.class, 2L), page).isEmpty());
	}
	
	@Test
	public void getBatchOfMessagesForIndexing_shouldReturnResultsBasedOnThePageSize() {
		SiteInfo site = getEntity(SiteInfo.class, 4L);
		assertEquals(3, repo.getBatchOfMessagesForIndexing(site, Pageable.unpaged()).size());
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForIndexing(site, PageRequest.of(0, 2));
		
		assertEquals(2, msgs.size());
		assertEquals(15l, msgs.get(0).getId().longValue());
		assertEquals(16l, msgs.get(1).getId().longValue());
	}
	
	@Test
	public void getBatchOfMessagesForResponse_shouldReturnAOrderedBatchOfMessagesToSendResponses() {
		SiteInfo site = getEntity(SiteInfo.class, 1L);
		Pageable page = PageRequest.of(0, 10);
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForResponse(site, page);
		
		assertEquals(3, msgs.size());
		assertEquals(2l, msgs.get(0).getId().longValue());
		assertEquals(3l, msgs.get(1).getId().longValue());
		assertEquals(6l, msgs.get(2).getId().longValue());
		assertTrue(repo.getBatchOfMessagesForResponse(getEntity(SiteInfo.class, 2L), page).isEmpty());
	}
	
	@Test
	public void getBatchOfMessagesForResponse_shouldReturnResultsBasedOnThePageSize() {
		SiteInfo site = getEntity(SiteInfo.class, 1L);
		assertEquals(3, repo.getBatchOfMessagesForResponse(site, Pageable.unpaged()).size());
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForResponse(site, PageRequest.of(0, 2));
		
		assertEquals(2, msgs.size());
		assertEquals(2l, msgs.get(0).getId().longValue());
		assertEquals(3l, msgs.get(1).getId().longValue());
	}
	
	@Test
	public void getBatchOfMessagesForArchiving_shouldReturnAOrderedBatchOfMessagesToArchive() {
		SiteInfo site = getEntity(SiteInfo.class, 5L);
		Pageable page = PageRequest.of(0, 10);
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForArchiving(site, page);
		
		assertEquals(4, msgs.size());
		assertEquals(25l, msgs.get(0).getId().longValue());
		assertEquals(26l, msgs.get(1).getId().longValue());
		assertEquals(27l, msgs.get(2).getId().longValue());
		assertEquals(28l, msgs.get(3).getId().longValue());
		
		msgs = repo.getBatchOfMessagesForArchiving(getEntity(SiteInfo.class, 2L), page);
		assertEquals(1, msgs.size());
		assertEquals(8l, msgs.get(0).getId().longValue());
	}
	
	@Test
	public void getBatchOfMessagesForArchiving_shouldReturnResultsBasedOnTheBatchSize() {
		SiteInfo site = getEntity(SiteInfo.class, 5L);
		assertEquals(4, repo.getBatchOfMessagesForArchiving(site, Pageable.unpaged()).size());
		
		List<SyncedMessage> msgs = repo.getBatchOfMessagesForArchiving(site, PageRequest.of(0, 2));
		
		assertEquals(2, msgs.size());
		assertEquals(25l, msgs.get(0).getId().longValue());
		assertEquals(26l, msgs.get(1).getId().longValue());
	}
	
}
