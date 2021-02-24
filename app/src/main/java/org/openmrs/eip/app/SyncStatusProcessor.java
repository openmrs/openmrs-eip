package org.openmrs.eip.app;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.eip.app.management.entity.ReceiverSyncStatus;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("syncStatusProcessor")
public class SyncStatusProcessor implements Processor {
	
	private static final Logger logger = LoggerFactory.getLogger(SyncStatusProcessor.class);
	
	@Override
	public void process(Exchange exchange) {
		SiteInfo siteInfo = null;
		try {
			SyncMetadata metadata = exchange.getIn().getBody(SyncModel.class).getMetadata();
			String siteClass = SiteInfo.class.getSimpleName();
			final String siteQuery = "jpa:" + siteClass + " ?query=SELECT s FROM " + siteClass + " s WHERE s.identifier = '"
			        + metadata.getSourceIdentifier() + "'";
			
			List<SiteInfo> sites = on(exchange.getContext()).to(siteQuery).request(List.class);
			
			if (sites.size() == 1) {
				siteInfo = sites.get(0);
			}
			
			if (siteInfo == null) {
				logger.error("No site info found with identifier: " + metadata.getSourceIdentifier()
				        + ", please create one in order to track its last sync date");
				return;
			}
			
			String statusClass = ReceiverSyncStatus.class.getSimpleName();
			final String statusParamsProp = "status-params";
			exchange.setProperty(statusParamsProp, Collections.singletonMap("siteInfoId", siteInfo.getId()));
			final String statusQuery = "jpa:" + statusClass + " ?query=SELECT s FROM " + statusClass
			        + " s WHERE s.siteInfoId = " + siteInfo.getId();
			
			List<ReceiverSyncStatus> statuses = on(exchange.getContext()).to(statusQuery).request(List.class);
			
			if (statuses.size() > 1) {
				logger.error("Found multiple status rows for site: " + siteInfo
				        + ", please delete one in order to track its last sync date");
				return;
			}
			
			ReceiverSyncStatus status;
			boolean exists = false;
			if (statuses.size() == 0) {
				status = new ReceiverSyncStatus(siteInfo.getId(), new Date());
				if (logger.isDebugEnabled()) {
					logger.debug("Inserting initial sync status for " + siteInfo + " as of " + status.getLastSyncDate());
				}
			} else {
				status = statuses.get(0);
				status.setLastSyncDate(new Date());
				exists = true;
				if (logger.isDebugEnabled()) {
					logger.debug("Updating last sync date for " + siteInfo + " to " + status.getLastSyncDate());
				}
			}
			
			exchange.getIn().setBody(status);
			
			on(exchange.getContext()).to("jpa:" + statusClass + "?usePersist=true");
			
			if (logger.isDebugEnabled()) {
				if (exists) {
					logger.debug("Successfully updated last sync date status for: " + siteInfo);
				} else {
					logger.debug("Successfully inserted sync status for: " + siteInfo);
				}
			}
		}
		catch (Exception e) {
			logger.error("Failed to update sync status for: " + siteInfo, e);
		}
		
	}
	
}
