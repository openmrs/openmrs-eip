package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.app.route.TestUtils;
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
	public void getBatchOfUnItemizedMessages_shouldReturnAOrderedBatchOfUnItemizedMessages() {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		Pageable page = PageRequest.of(0, 10);
		
		List<SyncedMessage> msgs = repo.getBatchOfUnItemizedMessages(site, page);
		
		assertEquals(3, msgs.size());
		assertEquals(3l, msgs.get(0).getId().longValue());
		assertEquals(4l, msgs.get(1).getId().longValue());
		assertEquals(2l, msgs.get(2).getId().longValue());
		
		site = TestUtils.getEntity(SiteInfo.class, 2L);
		
		msgs = repo.getBatchOfUnItemizedMessages(site, page);
		
		assertEquals(1, msgs.size());
		assertEquals(5l, msgs.get(0).getId().longValue());
	}
	
	@Test
	public void getBatchOfUnItemizedMessages_shouldReturnResultsBasedOnTheBatchSize() {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		assertEquals(3, repo.getBatchOfUnItemizedMessages(site, Pageable.unpaged()).size());
		
		List<SyncedMessage> msgs = repo.getBatchOfUnItemizedMessages(site, PageRequest.of(0, 2));
		
		assertEquals(2, msgs.size());
		assertEquals(3l, msgs.get(0).getId().longValue());
		assertEquals(4l, msgs.get(1).getId().longValue());
	}
	
}
