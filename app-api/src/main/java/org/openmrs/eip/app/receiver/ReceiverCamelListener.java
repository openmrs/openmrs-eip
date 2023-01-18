package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.DEFAULT_SITE_PARALLEL_SIZE;
import static org.openmrs.eip.app.SyncConstants.DEFAULT_THREAD_NUMBER;
import static org.openmrs.eip.app.SyncConstants.PROP_SITE_PARALLEL_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_THREAD_NUMBER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.DEFAULT_DELAY_IN_SECONDS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_DELAY_IN_SECONDS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_MSG_PROCESSOR;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStartedEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStoppingEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.AppUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.RECEIVER)
public class ReceiverCamelListener extends EventNotifierSupport {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverCamelListener.class);
	
	private ScheduledExecutorService siteExecutor;
	
	private ExecutorService msgExecutor;
	
	@Value("${" + PROP_SITE_PARALLEL_SIZE + ":" + DEFAULT_SITE_PARALLEL_SIZE + "}")
	private int parallelSiteSize;
	
	@Value("${" + PROP_THREAD_NUMBER + ":" + DEFAULT_THREAD_NUMBER + "}")
	private int threads;
	
	@Value("${" + PROP_DELAY_IN_SECONDS + ":" + DEFAULT_DELAY_IN_SECONDS + "}")
	private long delay;
	
	@Override
	public void notify(CamelEvent event) {
		
		if (event instanceof CamelContextStartedEvent) {
			log.info("Loading OpenMRS user account");
			String username = SyncContext.getBean(Environment.class).getProperty(Constants.PROP_OPENMRS_USER);
			if (StringUtils.isBlank(username)) {
				throw new EIPException("No value set for application property: " + Constants.PROP_OPENMRS_USER);
			}
			
			UserRepository userRepo = SyncContext.getBean(UserRepository.class);
			User exampleUser = new User();
			exampleUser.setUsername(username);
			Example<User> example = Example.of(exampleUser, ExampleMatcher.matching().withIgnoreCase());
			Optional<User> optional = userRepo.findOne(example);
			if (!optional.isPresent()) {
				log.error("No user found with username: " + username);
				AppUtils.shutdown();
			}
			
			UserLightRepository userLightRepo = SyncContext.getBean(UserLightRepository.class);
			SyncContext.setAppUser(userLightRepo.findById(optional.get().getId()).get());
			
			log.info("Loading OpenMRS admin user account");
			exampleUser = new User();
			exampleUser.setUsername("admin");
			example = Example.of(exampleUser, ExampleMatcher.matching().withIgnoreCase());
			optional = userRepo.findOne(example);
			if (!optional.isPresent()) {
				log.error("No admin user found");
				AppUtils.shutdown();
			}
			
			SyncContext.setAdminUser(userLightRepo.findById(optional.get().getId()).get());
			
			log.info("Starting sync message consumer threads, one per site");
			
			siteExecutor = Executors.newScheduledThreadPool(parallelSiteSize);
			msgExecutor = Executors.newFixedThreadPool(threads);
			
			Collection<SiteInfo> sites = ReceiverContext.getSites().stream().filter(s -> !s.getDisabled())
			        .collect(Collectors.toList());
			
			sites.stream().forEach(site -> {
				SiteMessageConsumer consumer = new SiteMessageConsumer(URI_MSG_PROCESSOR, site, threads, msgExecutor);
				siteExecutor.scheduleWithFixedDelay(consumer, 2, delay, TimeUnit.SECONDS);
			});
			
		} else if (event instanceof CamelContextStoppingEvent) {
			int timeout = 15;
			if (msgExecutor != null) {
				log.info("Shutting down executor for message sync threads");
				
				msgExecutor.shutdown();
				
				try {
					log.info("Waiting for " + timeout + " seconds for message sync threads to terminate");
					
					msgExecutor.awaitTermination(timeout, TimeUnit.SECONDS);
					
					log.info("Done shutting down executor for message sync threads");
				}
				catch (Exception e) {
					log.error("An error occurred while waiting for message sync threads to terminate");
				}
			}
			
			if (siteExecutor != null) {
				log.info("Shutting down executor for site message consumer threads");
				
				siteExecutor.shutdown();
				
				try {
					log.info("Waiting for " + timeout + " seconds for site message consumer threads to terminate");
					
					siteExecutor.awaitTermination(timeout, TimeUnit.SECONDS);
					
					log.info("Done shutting down executor for site message consumer threads");
				}
				catch (Exception e) {
					log.error("An error occurred while waiting for site message consumer threads to terminate");
				}
			}
		}
		
	}
	
}
