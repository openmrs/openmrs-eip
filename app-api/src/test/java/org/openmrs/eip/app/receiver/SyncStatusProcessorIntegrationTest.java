package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_IS_FILE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_METADATA;

import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.ReceiverSyncStatus;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import ch.qos.logback.classic.Level;

@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SyncStatusProcessorIntegrationTest extends BaseReceiverTest {
	
	@Autowired
	private SyncStatusProcessor processor;
	
	@Test
	public void shouldSkipUpdatingSyncStatusIfNoSiteIsFoundMatchingTheSiteIdentifier() {
		final String siteIdentifier = "bad-identifier";
		assertTrue(TestUtils.getEntities(ReceiverSyncStatus.class).isEmpty());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteIdentifier);
		SyncModel syncModel = new SyncModel();
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(syncModel);
		exchange.setProperty(EX_PROP_IS_FILE, false);
		
		processor.process(exchange);
		
		assertTrue(TestUtils.getEntities(ReceiverSyncStatus.class).isEmpty());
		assertMessageLogged(Level.ERROR, "No site info found with identifier: " + siteIdentifier
		        + ", please create one in order to track its last sync date");
	}
	
	@Test
	public void shouldInsertASyncStatusRowForTheSiteIfItDoesNotExist() {
		assertTrue(TestUtils.getEntities(ReceiverSyncStatus.class).isEmpty());
		SyncMetadata metadata = new SyncMetadata();
		SiteInfo siteInfo = TestUtils.getEntity(SiteInfo.class, 1L);
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		SyncModel syncModel = new SyncModel();
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(syncModel);
		exchange.setProperty(EX_PROP_IS_FILE, false);
		Date timestamp = new Date();
		
		processor.process(exchange);
		
		List<ReceiverSyncStatus> statuses = TestUtils.getEntities(ReceiverSyncStatus.class);
		assertEquals(1, statuses.size());
		assertEquals(siteInfo, statuses.get(0).getSiteInfo());
		assertTrue(statuses.get(0).getLastSyncDate().getTime() >= timestamp.getTime());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_status.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldUpdateASyncStatusRowForTheSiteIfItExists() {
		assertEquals(2, TestUtils.getEntities(ReceiverSyncStatus.class).size());
		SiteInfo siteInfo = TestUtils.getEntity(SiteInfo.class, 1L);
		ReceiverSyncStatus syncStatus = TestUtils.getEntity(ReceiverSyncStatus.class, 1L);
		Date existingLastSyncDate = syncStatus.getLastSyncDate();
		Date dateCreated = syncStatus.getDateCreated();
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		SyncModel syncModel = new SyncModel();
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setBody(syncModel);
		exchange.setProperty(EX_PROP_IS_FILE, false);
		
		processor.process(exchange);
		
		assertEquals(2, TestUtils.getEntities(ReceiverSyncStatus.class).size());
		syncStatus = TestUtils.getEntity(ReceiverSyncStatus.class, syncStatus.getId());
		assertTrue(syncStatus.getLastSyncDate().getTime() > existingLastSyncDate.getTime());
		assertEquals(siteInfo, syncStatus.getSiteInfo());
		assertEquals(dateCreated, syncStatus.getDateCreated());
	}
	
	@Test
	public void shouldUpdateTheSyncStatusForAFileSyncMessage() {
		assertTrue(TestUtils.getEntities(ReceiverSyncStatus.class).isEmpty());
		SyncMetadata metadata = new SyncMetadata();
		SiteInfo siteInfo = TestUtils.getEntity(SiteInfo.class, 1L);
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_IS_FILE, true);
		exchange.setProperty(EX_PROP_METADATA, JsonUtils.marshall(metadata));
		Date timestamp = new Date();
		
		processor.process(exchange);
		
		List<ReceiverSyncStatus> statuses = TestUtils.getEntities(ReceiverSyncStatus.class);
		assertEquals(1, statuses.size());
		assertEquals(siteInfo, statuses.get(0).getSiteInfo());
		assertTrue(statuses.get(0).getLastSyncDate().getTime() >= timestamp.getTime());
	}
	
}
