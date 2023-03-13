package org.openmrs.eip.app.receiver;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.EXECUTOR_SHUTDOWN_TIMEOUT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.BEAN_NAME_SITE_EXECUTOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_DELAY_ARCHIVER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_DELAY_CACHE_EVICTOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_DELAY_INDEX_UPDATER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_DELAY_RESPONSE_SENDER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_DELAY_SYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_INITIAL_DELAY_ARCHIVER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_INITIAL_DELAY_CACHE_EVICTOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_INITIAL_DELAY_INDEX_UPDATER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_INITIAL_DELAY_RESPONSE_SENDER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_INITIAL_DELAY_SYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_MSG_PROCESSOR;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
	
	private static final int DEFAULT_INITIAL_DELAY_SYNC = 5000;
	
	private static final int DEFAULT_DELAY = 300000;
	
	private ScheduledThreadPoolExecutor siteExecutor;
	
	private ThreadPoolExecutor syncExecutor;
	
	@Value("${" + PROP_INITIAL_DELAY_SYNC + ":" + DEFAULT_INITIAL_DELAY_SYNC + "}")
	private long initialDelayConsumer;
	
	@Value("${" + PROP_DELAY_SYNC + ":" + DEFAULT_DELAY + "}")
	private long delayConsumer;
	
	@Value("${" + PROP_INITIAL_DELAY_CACHE_EVICTOR + ":" + (DEFAULT_INITIAL_DELAY_SYNC + 10000) + "}")
	private long initialDelayCacheEvictor;
	
	@Value("${" + PROP_DELAY_CACHE_EVICTOR + ":" + DEFAULT_DELAY + "}")
	private long delayCacheEvictor;
	
	@Value("${" + PROP_INITIAL_DELAY_INDEX_UPDATER + ":" + (DEFAULT_INITIAL_DELAY_SYNC + 10000) + "}")
	private long initialDelayIndexUpdater;
	
	@Value("${" + PROP_DELAY_INDEX_UPDATER + ":" + DEFAULT_DELAY + "}")
	private long delayIndexUpdater;
	
	@Value("${" + PROP_INITIAL_DELAY_RESPONSE_SENDER + ":" + (DEFAULT_INITIAL_DELAY_SYNC + 25000) + "}")
	private long initialDelayResponseSender;
	
	@Value("${" + PROP_DELAY_RESPONSE_SENDER + ":" + DEFAULT_DELAY + "}")
	private long delayResponseSender;
	
	@Value("${" + PROP_INITIAL_DELAY_ARCHIVER + ":" + (DEFAULT_INITIAL_DELAY_SYNC + 40000) + "}")
	private long initialDelayArchiver;
	
	@Value("${" + PROP_DELAY_ARCHIVER + ":" + DEFAULT_DELAY + "}")
	private long delayArchiver;
	
	public ReceiverCamelListener(@Qualifier(BEAN_NAME_SITE_EXECUTOR) ScheduledThreadPoolExecutor siteExecutor,
	    @Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor syncExecutor) {
		this.siteExecutor = siteExecutor;
		this.syncExecutor = syncExecutor;
	}
	
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
			
			Collection<SiteInfo> sites = ReceiverContext.getSites().stream().filter(s -> !s.getDisabled())
			        .collect(Collectors.toList());
			
			startMessageConsumers(sites);
			
			startCacheEvictors(sites);
			
			startSearchIndexUpdaters(sites);
			
			startSyncResponseSenders(sites);
			
			startMessageArchivers(sites);
			
		} else if (event instanceof CamelContextStoppingEvent) {
			final int syncExecutorWait = 100;
			while (!syncExecutor.isTerminated()) {
				try {
					Thread.sleep(syncExecutorWait);
					if (log.isTraceEnabled()) {
						log.trace("Waiting for " + syncExecutorWait + "ms for sync executor to terminate");
					}
				}
				catch (InterruptedException e) {
					log.error("An error occurred while waiting for sync executor to terminate", e);
				}
			}
			
			log.info("Shutting down site executor");
			
			siteExecutor.shutdownNow();
			
			try {
				log.info("Waiting for " + EXECUTOR_SHUTDOWN_TIMEOUT + " seconds for site executor to terminate");
				
				siteExecutor.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
				
				log.info("Done shutting down site executor");
			}
			catch (Exception e) {
				log.error("An error occurred while waiting for site executor to terminate");
			}
		}
		
	}
	
	private void startMessageConsumers(Collection<SiteInfo> sites) {
		sites.stream().forEach(site -> {
			SiteMessageConsumer consumer = new SiteMessageConsumer(URI_MSG_PROCESSOR, site, syncExecutor);
			siteExecutor.scheduleWithFixedDelay(consumer, initialDelayConsumer, delayConsumer, MILLISECONDS);
		});
	}
	
	private void startCacheEvictors(Collection<SiteInfo> sites) {
		sites.stream().forEach(site -> {
			CacheEvictor evictor = new CacheEvictor(site);
			siteExecutor.scheduleWithFixedDelay(evictor, initialDelayCacheEvictor, delayCacheEvictor, MILLISECONDS);
		});
	}
	
	private void startSearchIndexUpdaters(Collection<SiteInfo> sites) {
		sites.stream().forEach(site -> {
			SearchIndexUpdater updater = new SearchIndexUpdater(site);
			siteExecutor.scheduleWithFixedDelay(updater, initialDelayIndexUpdater, delayIndexUpdater, MILLISECONDS);
		});
	}
	
	private void startSyncResponseSenders(Collection<SiteInfo> sites) {
		sites.stream().forEach(site -> {
			SyncResponseSender sender = new SyncResponseSender(site);
			siteExecutor.scheduleWithFixedDelay(sender, initialDelayResponseSender, delayResponseSender, MILLISECONDS);
		});
	}
	
	private void startMessageArchivers(Collection<SiteInfo> sites) {
		sites.stream().forEach(site -> {
			SyncedMessageArchiver archiver = new SyncedMessageArchiver(site);
			siteExecutor.scheduleWithFixedDelay(archiver, initialDelayArchiver, delayArchiver, MILLISECONDS);
		});
	}
	
}
