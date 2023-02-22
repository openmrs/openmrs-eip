package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

/**
 * Base class for {@link Runnable} instances that process tasks for a single site
 */
public abstract class BaseSiteRunnable implements Runnable {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseSiteRunnable.class);
	
	@Getter
	private SiteInfo site;
	
	private boolean errorEncountered = false;
	
	public BaseSiteRunnable(SiteInfo site) {
		this.site = site;
	}
	
	@Override
	public void run() {
		if (AppUtils.isStopping()) {
			if (log.isDebugEnabled()) {
				log.debug(
				    "Skipping " + getProcessorName() + " for site: " + getSite() + " because the application is stopping");
			}
			
			return;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Starting " + getProcessorName() + " for site -> " + getSite());
		}
		
		do {
			try {
				boolean stop = doRun();
				if (stop) {
					break;
				}
			}
			catch (Throwable t) {
				if (!AppUtils.isAppContextStopping()) {
					errorEncountered = true;
					log.error(getProcessorName() + " for site: " + getSite() + " encountered an error", t);
					break;
				}
			}
		} while (!AppUtils.isStopping() && !errorEncountered);
		
		if (!errorEncountered) {
			if (log.isDebugEnabled()) {
				log.debug(getProcessorName() + " for site: " + getSite() + " has completed");
			}
		}
	}
	
	/**
	 * Gets the logical processor name
	 *
	 * @return the processor name
	 */
	public abstract String getProcessorName();
	
	/**
	 * Subclasses should add their implementation logic in this method
	 * 
	 * @return true if this runnable should stop otherwise false
	 * @throws Exception
	 */
	public abstract boolean doRun() throws Exception;
	
}
