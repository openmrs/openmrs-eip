package org.openmrs.eip.app;

import static org.openmrs.eip.app.SyncConstants.DEFAULT_SYNC_THREAD_SIZE;
import static org.openmrs.eip.app.SyncConstants.MAX_COUNT;
import static org.openmrs.eip.app.SyncConstants.PROP_SYNC_THREAD_SIZE;
import static org.openmrs.eip.app.SyncConstants.WAIT_IN_SECONDS;

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
public class CamelListener extends EventNotifierSupport {
	
	protected static final Logger log = LoggerFactory.getLogger(CamelListener.class);
	
	private static ExecutorService siteExecutor;
	
	private static ExecutorService syncExecutor;
	
	@Value("${" + PROP_SYNC_THREAD_SIZE + ":" + DEFAULT_SYNC_THREAD_SIZE + "}")
	private Integer parallelSyncMsgSize;
	
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
				SyncApplication.shutdown();
			}
			
			SyncContext.setAppUser(userListRepo.findById(optional.get().getId()).get());
			
			log.info("Loading OpenMRS admin user account");
			exampleUser = new User();
			exampleUser.setUsername("admin");
			example = Example.of(exampleUser, ExampleMatcher.matching().withIgnoreCase());
			optional = userRepo.findOne(example);
			if (!optional.isPresent()) {
				log.error("No admin user found");
				SyncApplication.shutdown();
			}
			
			SyncContext.setAdminUser(userListRepo.findById(optional.get().getId()).get());
			
			log.info("Starting sync message consumer threads, one per site");
			
			Collection<SiteInfo> sites = ReceiverContext.getSites();
			siteExecutor = Executors.newFixedThreadPool(sites.size());
			syncExecutor = Executors.newFixedThreadPool(parallelSyncMsgSize);
			
			sites.parallelStream().forEach((site) -> {
				log.info("Starting sync message consumer for site: " + site + ", batch size: " + MAX_COUNT);
				
				siteExecutor.execute(new SiteMessageConsumer(site, syncExecutor, parallelSyncMsgSize));
				
				if (log.isDebugEnabled()) {
					log.debug("Started sync message consumer for site: " + site);
				}
			});
			
		} else if (event instanceof CamelContextStoppingEvent) {
			ReceiverContext.setStopSignal();
			log.info("Shutting down executor for sync message threads");
			
			if (syncExecutor != null) {
				syncExecutor.shutdown();
				
				try {
					int wait = WAIT_IN_SECONDS + 10;
					log.info("Waiting for " + wait + " seconds for sync threads to terminate");
					
					syncExecutor.awaitTermination(wait, TimeUnit.SECONDS);
					
					log.info("The sync threads have successfully terminated, done shutting down the "
					        + "executor for sync threads");
				}
				catch (Exception e) {
					log.error("An error occurred while waiting for sync threads to terminate");
				}
			}
			
			log.info("Shutting down executor for site message consumer threads");
			
			if (siteExecutor != null) {
				siteExecutor.shutdown();
				
				try {
					int wait = WAIT_IN_SECONDS + 10;
					log.info("Waiting for " + wait + " seconds for message consumer threads to terminate");
					
					siteExecutor.awaitTermination(wait, TimeUnit.SECONDS);
					
					log.info("The message consumer threads have successfully terminated, done shutting down the "
					        + "executor for message consumer threads");
				}
				catch (Exception e) {
					log.error("An error occurred while waiting for message consumer threads to terminate");
				}
			}
		}
		
	}
	
}
