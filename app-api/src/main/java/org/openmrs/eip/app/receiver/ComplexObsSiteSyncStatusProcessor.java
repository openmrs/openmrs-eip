package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_IS_FILE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_METADATA;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Sets the last sync date for a site when a message for a complex obs is received
 */
@Component("complexObsSiteSyncStatusProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ComplexObsSiteSyncStatusProcessor implements Processor {
	
	private static final Logger logger = LoggerFactory.getLogger(ComplexObsSiteSyncStatusProcessor.class);
	
	@Override
	public void process(Exchange exchange) {
		SiteInfo siteInfo = null;
		try {
			if (!exchange.getProperty(EX_PROP_IS_FILE, Boolean.class)) {
				if (logger.isTraceEnabled()) {
					logger.trace("Skipping updating site last sync date for a non complex obs message");
				}
				
				return;
			}
			
			SyncMetadata metadata = JsonUtils.unmarshal(exchange.getProperty(EX_PROP_METADATA, String.class),
			    SyncMetadata.class);
			siteInfo = ReceiverContext.getSiteInfo(metadata.getSourceIdentifier());
			
			if (siteInfo == null) {
				logger.error("No site info found with identifier: " + metadata.getSourceIdentifier()
				        + ", please create one in order to track its last sync date");
				return;
			}
			
			ReceiverUtils.saveLastSyncDate(siteInfo, new Date());
		}
		catch (Throwable t) {
			logger.error("Failed to update sync status for: " + siteInfo, t);
		}
	}
}
