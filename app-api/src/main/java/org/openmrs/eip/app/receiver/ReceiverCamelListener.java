package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.DEFAULT_SITE_PARALLEL_SIZE;
import static org.openmrs.eip.app.SyncConstants.DEFAULT_THREAD_NUMBER;
import static org.openmrs.eip.app.SyncConstants.MAX_COUNT;
import static org.openmrs.eip.app.SyncConstants.PROP_SITE_PARALLEL_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_THREAD_NUMBER;
import static org.openmrs.eip.app.SyncConstants.WAIT_IN_SECONDS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_MSG_PROCESSOR;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	
	private static ExecutorService siteExecutor;
	
	private static ExecutorService msgExecutor;
	
	@Value("${" + PROP_SITE_PARALLEL_SIZE + ":" + DEFAULT_SITE_PARALLEL_SIZE + "}")
	private int parallelSiteSize;
	
	@Value("${" + PROP_THREAD_NUMBER + ":" + DEFAULT_THREAD_NUMBER + "}")
	private int threadCount;
	
	@Override
	public void notify(CamelEvent event) {
		
		if (event instanceof CamelContextStartedEvent) {
			log.info("Loading OpenMRS user account");
			String username = SyncContext.getBean(Environment.class).getProperty(Constants.PROP_OPENMRS_USER);
			if (StringUtils.isBlank(username)) {
				throw new EIPException("No value set for application property: " + Constants.PROP_OPENMRS_USER);
			}
			
			UserLightRepository userListRepo = SyncContext.getBean(UserLightRepository.class);
			UserRepository userRepo = SyncContext.getBean(UserRepository.class);
			User exampleUser = new User();
			exampleUser.setUsername(username);
			Example<User> example = Example.of(exampleUser, ExampleMatcher.matching().withIgnoreCase());
			Optional<User> optional = userRepo.findOne(example);
			if (!optional.isPresent()) {
				log.error("No user found with username: " + username);
				AppUtils.shutdown();
			}
			
			SyncContext.setAppUser(userListRepo.findById(optional.get().getId()).get());
			
			log.info("Loading OpenMRS admin user account");
			exampleUser = new User();
			exampleUser.setUsername("admin");
			example = Example.of(exampleUser, ExampleMatcher.matching().withIgnoreCase());
			optional = userRepo.findOne(example);
			if (!optional.isPresent()) {
				log.error("No admin user found");
				AppUtils.shutdown();
			}
			
			SyncContext.setAdminUser(userListRepo.findById(optional.get().getId()).get());
			
			log.info("Starting sync message consumer threads, one per site");
			
			Collection<SiteInfo> sites = ReceiverContext.getSites();
			siteExecutor = Executors.newFixedThreadPool(parallelSiteSize);
			msgExecutor = Executors.newFixedThreadPool(threadCount);
			
			sites.parallelStream().forEach((site) -> {
				log.info("Starting sync message consumer for site: " + site + ", batch size: " + MAX_COUNT);
				
				siteExecutor.execute(new SiteMessageConsumer(URI_MSG_PROCESSOR, site, threadCount, msgExecutor));
				
				if (log.isDebugEnabled()) {
					log.debug("Started sync message consumer for site: " + site);
				}
			});
			
		} else if (event instanceof CamelContextStoppingEvent) {
			int wait = WAIT_IN_SECONDS + 10;
			
			if (msgExecutor != null) {
				log.info("Shutting down executor for sync message threads");
				
				msgExecutor.shutdown();
				
				try {
					log.info("Waiting for " + wait + " seconds for sync threads to terminate");
					
					msgExecutor.awaitTermination(wait, TimeUnit.SECONDS);
					
					log.info("Done shutting down executor for site message consumer threads");
				}
				catch (Exception e) {
					log.error("An error occurred while waiting for sync threads to terminate");
				}
			}
			
			if (siteExecutor != null) {
				log.info("Shutting down executor for site message consumer threads");
				
				siteExecutor.shutdown();
				
				try {
					log.info("Waiting for " + wait + " seconds for site message consumer threads to terminate");
					
					siteExecutor.awaitTermination(wait, TimeUnit.SECONDS);
					
					log.info("Done shutting down executor for site message consumer threads");
				}
				catch (Exception e) {
					log.error("An error occurred while waiting for site message consumer threads to terminate");
				}
			}
		}
		
	}
	
}
