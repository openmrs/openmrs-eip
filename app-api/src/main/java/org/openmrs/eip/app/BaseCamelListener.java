package org.openmrs.eip.app;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.impl.event.CamelContextStartedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.ShutdownStrategy;
import org.apache.camel.support.EventNotifierSupport;

/**
 * Base class for listeners for camel events
 */
public abstract class BaseCamelListener extends EventNotifierSupport {
	
	protected ThreadPoolExecutor syncExecutor;
	
	public BaseCamelListener(ThreadPoolExecutor syncExecutor) {
		this.syncExecutor = syncExecutor;
	}
	
	@Override
	public void notify(CamelEvent event) throws Exception {
		if (event instanceof CamelEvent.CamelContextStartedEvent) {
			ShutdownStrategy shutdownStrategy = ((CamelContextStartedEvent) event).getContext().getShutdownStrategy();
			shutdownStrategy.setTimeout(15);
			shutdownStrategy.setShutdownNowOnTimeout(true);
			applicationStarted();
		} else if (event instanceof CamelEvent.CamelContextStoppingEvent) {
			AppUtils.handleAppContextStopping();
			AppUtils.shutdownExecutor(syncExecutor, "sync", false);
			applicationStopped();
		}
	}
	
	/**
	 * Called after the camel context is started
	 */
	public abstract void applicationStarted();
	
	/**
	 * Called before the camel context is stopped
	 */
	public abstract void applicationStopped();
	
}
