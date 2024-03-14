package org.openmrs.eip.app.receiver;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncStatus;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.repository.SiteSyncStatusRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("syncStatusProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SyncStatusProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(SyncStatusProcessor.class);
	
	private static Map<String, ReceiverSyncStatus> siteIdAndStatusMap;
	
	protected static final long FLUSH_INTERVAL = 60000;
	
	private SiteSyncStatusRepository statusRepo;
	
	public SyncStatusProcessor(SiteSyncStatusRepository statusRepo) {
		this.statusRepo = statusRepo;
	}
	
	public void process(SyncMetadata metadata) {
		Date dateReceived = new Date();
		SiteInfo site = null;
		try {
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
			boolean isStatusCached = false;
			boolean flushIntervalElapsed = false;
			if (status == null) {
				status = statusRepo.findBySiteInfo(site);
			} else {
				isStatusCached = true;
				flushIntervalElapsed = Utils.getMillisElapsed(status.getLastSyncDate(), dateReceived) > FLUSH_INTERVAL;
			}
			
			boolean saveChanges = false;
			if (status == null) {
				status = new ReceiverSyncStatus(site, dateReceived);
				status.setDateCreated(dateReceived);
				saveChanges = true;
				if (logger.isTraceEnabled()) {
					logger.trace("Inserting site sync status -> " + status);
				}
			} else if (!isStatusCached || flushIntervalElapsed) {
				//A status that was read from the DB, or if the date we have in the cache is older than a minute, this 
				//ensures that if we get multiple messages from the same site within a short negligible time interval, 
				//this acts as a throttle to avoid multiple database updatessaveChanges = true;
				status.setLastSyncDate(dateReceived);
				saveChanges = true;
				if (logger.isTraceEnabled()) {
					logger.trace("Updating site last sync date to -> " + status);
				}
			}
			
			if (saveChanges) {
				statusRepo.save(status);
				if (logger.isTraceEnabled()) {
					logger.trace("Successfully saved site sync status -> " + status);
				}
			}
			
			if (!isStatusCached) {
				siteIdAndStatusMap.put(metadata.getSourceIdentifier(), status);
				if (logger.isTraceEnabled()) {
					logger.trace("Added to cache site sync status -> " + status);
				}
			}
		}
		catch (Throwable t) {
			logger.error("Failed to update site sync status for: " + site, t);
		}
	}
}
