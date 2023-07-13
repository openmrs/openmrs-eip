package org.openmrs.eip.app;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Watches for specific camel events and responds to them accordingly
 */
@Component
public class AppCamelListener extends EventNotifierSupport {
	
	protected static final Logger log = LoggerFactory.getLogger(AppCamelListener.class);
	
	private ThreadPoolExecutor syncExecutor;
	
	public AppCamelListener(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor syncExecutor) {
		this.syncExecutor = syncExecutor;
	}
	
	@Override
	public void notify(CamelEvent event) throws Exception {
		if (event instanceof CamelEvent.CamelContextStoppingEvent) {
			AppUtils.handleAppContextStopping();
			AppUtils.shutdownExecutor(syncExecutor, "sync", false);
		}
	}
	
}
