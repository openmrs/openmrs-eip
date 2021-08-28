package org.openmrs.eip.app;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds contextual data for the receiver
 */
public final class ReceiverContext {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverContext.class);
	
	private static Map<String, SiteInfo> siteNameAndInfoMap = null;
	
	/**
	 * Gets {@link SiteInfo} that matches the specified identifier
	 * 
	 * @return {@link SiteInfo} object
	 */
	public static SiteInfo getSiteInfo(String identifier) {
		if (siteNameAndInfoMap == null) {
			synchronized (ReceiverContext.class) {
				if (siteNameAndInfoMap == null) {
					log.info("Loading SiteInfo...");
					
					ProducerTemplate producerTemplate = SyncContext.getBean(ProducerTemplate.class);
					String siteClass = SiteInfo.class.getSimpleName();
					final String siteUri = "jpa:" + siteClass + " ?query=SELECT s FROM " + siteClass + " s";
					List<SiteInfo> sites = producerTemplate.requestBody(siteUri, null, List.class);
					siteNameAndInfoMap = new ConcurrentHashMap(sites.size());
					sites.stream().forEach((site) -> siteNameAndInfoMap.put(site.getIdentifier().toLowerCase(), site));
					
					if (log.isTraceEnabled()) {
						log.trace("Loaded sites: " + sites);
					}
					
					log.info("Successfully loaded SiteInfo");
				}
			}
		}
		
		return siteNameAndInfoMap.get(identifier.toLowerCase());
	}
	
}
