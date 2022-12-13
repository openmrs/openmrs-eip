package org.openmrs.eip.app.receiver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	protected static Map<String, SiteInfo> getSiteNameAndInfoMap() {
		synchronized (ReceiverContext.class) {
			if (siteNameAndInfoMap == null) {
				log.info("Loading sites...");
				
				ProducerTemplate producerTemplate = SyncContext.getBean(ProducerTemplate.class);
				String siteClass = SiteInfo.class.getSimpleName();
				final String siteQueryUri = "jpa:" + siteClass + " ?query=SELECT s FROM " + siteClass + " s";
				List<SiteInfo> sites = producerTemplate.requestBody(siteQueryUri, null, List.class);
				siteNameAndInfoMap = new HashMap(sites.size());
				sites.stream().forEach((site) -> siteNameAndInfoMap.put(site.getIdentifier().toLowerCase(), site));
				
				if (log.isDebugEnabled()) {
					log.debug("Loaded sites: " + sites);
				}
				
				log.info("Successfully loaded " + sites.size() + " site(s)");
			}
		}
		
		return siteNameAndInfoMap;
	}
	
	/**
	 * Gets {@link SiteInfo} that matches the specified identifier
	 * 
	 * @return {@link SiteInfo} object
	 */
	public static SiteInfo getSiteInfo(String identifier) {
		return getSiteNameAndInfoMap().get(identifier.toLowerCase());
	}
	
	/**
	 * Gets all sites
	 * 
	 * @return a collection of {@link SiteInfo} objects
	 */
	public static Collection<SiteInfo> getSites() {
		return getSiteNameAndInfoMap().values();
	}
	
}
