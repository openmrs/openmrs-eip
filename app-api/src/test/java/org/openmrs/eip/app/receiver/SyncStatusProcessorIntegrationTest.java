package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncStatus;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.SiteSyncStatusRepository;
import org.openmrs.eip.component.model.SyncMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SyncStatusProcessorIntegrationTest extends BaseReceiverTest {
	
	@Before
	public void tearDown() {
		setInternalState(SyncStatusProcessor.class, "siteIdAndStatusMap", (Object) null);
	}
	
	@Autowired
	private SyncStatusProcessor processor;
	
	@Autowired
	private SiteSyncStatusRepository statusRepo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Test
	public void shouldSkipUpdatingSyncStatusIfNoSiteIsFoundMatchingTheSiteIdentifier() {
		final String siteIdentifier = "bad-identifier";
		assertEquals(0, statusRepo.count());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteIdentifier);
		
		processor.process(metadata);
		
		assertEquals(0, statusRepo.count());
		assertMessageLogged(Level.ERROR, "No site info found with identifier: " + siteIdentifier
		        + ", please create one in order to track its last sync date");
	}
	
	@Test
	public void shouldInsertASyncStatusRowForTheSiteIfItDoesNotExist() {
		assertEquals(0, statusRepo.count());
		SyncMetadata metadata = new SyncMetadata();
		SiteInfo siteInfo = siteRepo.findById(1L).get();
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		Date timestamp = new Date();
		
		processor.process(metadata);
		
		List<ReceiverSyncStatus> statuses = statusRepo.findAll();
		assertEquals(1, statuses.size());
		assertEquals(siteInfo, statuses.get(0).getSiteInfo());
		assertTrue(statuses.get(0).getLastSyncDate().getTime() >= timestamp.getTime());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_status.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldUpdateASyncStatusRowForTheSiteIfItExists() {
		assertEquals(2, statusRepo.count());
		SiteInfo siteInfo = siteRepo.findById(1L).get();
		ReceiverSyncStatus syncStatus = statusRepo.findById(1L).get();
		Date existingLastSyncDate = syncStatus.getLastSyncDate();
		Date dateCreated = syncStatus.getDateCreated();
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		
		processor.process(metadata);
		
		assertEquals(2, statusRepo.findAll().size());
		syncStatus = statusRepo.findById(syncStatus.getId()).get();
		assertTrue(syncStatus.getLastSyncDate().getTime() > existingLastSyncDate.getTime());
		assertEquals(siteInfo, syncStatus.getSiteInfo());
		assertEquals(dateCreated, syncStatus.getDateCreated());
	}
	
	@Test
	public void shouldInsertTheSyncStatusForAFileSyncMessage() {
		assertEquals(0, statusRepo.count());
		SyncMetadata metadata = new SyncMetadata();
		SiteInfo siteInfo = siteRepo.findById(1L).get();
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		Date timestamp = new Date();
		
		processor.process(metadata);
		
		List<ReceiverSyncStatus> statuses = statusRepo.findAll();
		assertEquals(1, statuses.size());
		assertEquals(siteInfo, statuses.get(0).getSiteInfo());
		assertTrue(statuses.get(0).getLastSyncDate().getTime() >= timestamp.getTime());
	}
	
}
