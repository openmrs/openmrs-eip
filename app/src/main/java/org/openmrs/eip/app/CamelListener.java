package org.openmrs.eip.app;

import static org.openmrs.eip.app.SyncConstants.MAX_COUNT;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStartedEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStoppingEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.RECEIVER)
public class CamelListener extends EventNotifierSupport {
	
	protected static final Logger log = LoggerFactory.getLogger(CamelListener.class);
	
	private static ExecutorService executor;
	
	@Override
	public void notify(CamelEvent event) {
		
		if (event instanceof CamelContextStartedEvent) {
			log.info("Starting sync message consumer threads, one per site");
			
			Collection<SiteInfo> sites = ReceiverContext.getSites();
			executor = Executors.newFixedThreadPool(sites.size());
			ProducerTemplate producerTemplate = SyncContext.getBean(ProducerTemplate.class);
			sites.parallelStream().forEach((site) -> {
				log.info("Starting sync message consumer for site: " + site + ", batch size: " + MAX_COUNT);
				
				executor.execute(new SiteMessageConsumer(site, producerTemplate));
				
				if (log.isDebugEnabled()) {
					log.debug("Started sync message consumer for site: " + site);
				}
			});
			
		} else if (event instanceof CamelContextStoppingEvent) {
			ReceiverContext.setStopSignal();
			//executor.shutdownNow();
		}
		
	}
	
}
