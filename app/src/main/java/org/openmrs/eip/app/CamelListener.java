package org.openmrs.eip.app;

import static org.openmrs.eip.app.SyncConstants.MAX_COUNT;
import static org.openmrs.eip.app.SyncConstants.WAIT_IN_SECONDS;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStartedEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStoppingEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.entity.User;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.repository.UserRepository;
import org.openmrs.eip.component.repository.light.UserLightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.RECEIVER)
public class CamelListener extends EventNotifierSupport {
	
	protected static final Logger log = LoggerFactory.getLogger(CamelListener.class);
	
	private static ExecutorService executor;
	
	@Override
	public void notify(CamelEvent event) {
		
		if (event instanceof CamelContextStartedEvent) {
			log.info("Loading OpenMRS user account");
			String username = SyncContext.getBean(Environment.class).getProperty(Constants.PROP_OPENMRS_USER);
			if (StringUtils.isBlank(username)) {
				throw new EIPException("No value set for application property: " + Constants.PROP_OPENMRS_USER);
			}
			
			User exampleUser = new User();
			exampleUser.setUsername(username);
			Example<User> example = Example.of(exampleUser, ExampleMatcher.matchingAll().withIgnoreCase());
			Optional<User> optional = SyncContext.getBean(UserRepository.class).findOne(example);
			User user = optional.orElseThrow(() -> new EIPException("No user found with username: " + username));
			SyncContext.setUser(SyncContext.getBean(UserLightRepository.class).findById(user.getId()).get());
			
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
			log.info("Shutting down executor for message consumer threads");
			
			if (executor != null) {
				executor.shutdown();
				
				try {
					int wait = WAIT_IN_SECONDS + 10;
					log.info("Waiting for " + wait + " seconds for message consumer threads to terminate");
					
					executor.awaitTermination(wait, TimeUnit.SECONDS);
					
					log.info("The message consumer threads have successfully terminated, done shutting down the "
					        + "executor for message consumer threads");
				}
				catch (InterruptedException e) {
					log.error("An error occurred while waiting for message consumer threads to terminate");
				}
			}
		}
		
	}
	
}