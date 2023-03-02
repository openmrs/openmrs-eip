package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for {@link Runnable} instances that process tasks for a single site
 */
public abstract class BaseSiteRunnable implements Runnable {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseSiteRunnable.class);
	
	protected SiteInfo site;
	
	private boolean errorEncountered = false;
	
	public BaseSiteRunnable(SiteInfo site) {
		this.site = site;
	}
	
	@Override
	public void run() {
		if (AppUtils.isStopping()) {
			if (log.isDebugEnabled()) {
				log.debug("Skipping " + getTaskName() + " for site: " + site + " because the application is stopping");
			}
			
			return;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Starting " + getTaskName() + " for site -> " + site);
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
					String msg = getTaskName() + " for site: " + site + " encountered an error";
					if (log.isDebugEnabled()) {
						log.error(msg, t);
					} else {
						log.warn(msg);
					}
					
					break;
				}
			}
		} while (!AppUtils.isStopping() && !errorEncountered);
		
		if (!errorEncountered) {
			if (log.isDebugEnabled()) {
				log.debug(getTaskName() + " for site: " + site + " has completed");
			}
		}
	}
	
	/**
	 * Gets the logical task name
	 *
	 * @return the task name
	 */
	public abstract String getTaskName();
	
	/**
	 * Subclasses should add their implementation logic in this method
	 * 
	 * @return true if this runnable should stop otherwise false
	 * @throws Exception
	 */
	public abstract boolean doRun() throws Exception;
	
}
