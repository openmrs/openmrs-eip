package org.openmrs.eip.app.receiver;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.eip.app.management.entity.ReceiverSyncStatus;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.repository.SiteSyncStatusRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("syncStatusProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SyncStatusProcessor implements Processor {
	
	private static final Logger logger = LoggerFactory.getLogger(SyncStatusProcessor.class);
	
	private static Map<String, ReceiverSyncStatus> siteIdAndStatusMap;
	
	private SiteSyncStatusRepository statusRepo;
	
	public SyncStatusProcessor(SiteSyncStatusRepository statusRepo) {
		this.statusRepo = statusRepo;
	}
	
	@Override
	public void process(Exchange exchange) {
		SiteInfo site = null;
		try {
			SyncMetadata metadata;
			if (exchange.getProperty("is-file", Boolean.class)) {
				metadata = JsonUtils.unmarshal(exchange.getProperty("sync-metadata", String.class), SyncMetadata.class);
			} else {
				metadata = exchange.getIn().getBody(SyncModel.class).getMetadata();
			}
			
			site = ReceiverContext.getSiteInfo(metadata.getSourceIdentifier());
			
			if (site == null) {
				logger.error("No site info found with identifier: " + metadata.getSourceIdentifier()
				        + ", please create one in order to track its last sync date");
				return;
			}
			
			if (siteIdAndStatusMap == null) {
				siteIdAndStatusMap = new HashMap<>(ReceiverContext.getSites().size());
			}
			
			ReceiverSyncStatus status = siteIdAndStatusMap.get(metadata.getSourceIdentifier());
			boolean cacheStatus = false;
			if (status == null) {
				cacheStatus = true;
				status = statusRepo.findBySiteInfo(site);
			}
			
			//TODO Use exchange.getCreated
			Date lastSyncDate = new Date();
			if (status == null) {
				status = new ReceiverSyncStatus(site, lastSyncDate);
				status.setDateCreated(lastSyncDate);
				if (logger.isTraceEnabled()) {
					logger.trace("Inserting initial sync status for " + site + " as " + status.getLastSyncDate());
				}
			} else {
				status.setLastSyncDate(lastSyncDate);
				if (logger.isTraceEnabled()) {
					logger.trace("Updating last sync date for " + site + " to " + status.getLastSyncDate());
				}
			}
			
			statusRepo.save(status);
			if (cacheStatus) {
				siteIdAndStatusMap.put(metadata.getSourceIdentifier(), status);
			}
			
			if (logger.isTraceEnabled()) {
				logger.trace("Successfully saved sync status for: " + site + " -> " + status);
			}
		}
		catch (Throwable t) {
			logger.error("Failed to update sync status for: " + site, t);
		}
	}
}
