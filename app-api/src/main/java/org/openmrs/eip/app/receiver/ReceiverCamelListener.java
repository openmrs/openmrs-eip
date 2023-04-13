package org.openmrs.eip.app.receiver;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.BEAN_NAME_SITE_EXECUTOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_ARCHIVES_MAX_AGE_DAYS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_DELAY_PRUNER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_INITIAL_DELAY_PRUNER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRUNER_ENABLED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SITE_DISABLED_TASKS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SITE_TASK_DELAY;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SITE_TASK_INITIAL_DELAY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
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
	
	private static final int DEFAULT_DELAY_PRUNER = 86400000;
	
	private ScheduledThreadPoolExecutor siteExecutor;
	
	private ThreadPoolExecutor syncExecutor;
	
	@Value("${" + PROP_SITE_TASK_INITIAL_DELAY + ":" + DEFAULT_INITIAL_DELAY_SYNC + "}")
	private long siteTaskInitialDelay;
	
	@Value("${" + PROP_SITE_TASK_DELAY + ":" + DEFAULT_DELAY + "}")
	private long siteTaskDelay;
	
	@Value("${" + PROP_SITE_DISABLED_TASKS + ":}")
	private List<SiteChildTaskType> disabledTaskTypes;
	
	@Value("${" + PROP_INITIAL_DELAY_PRUNER + ":" + (DEFAULT_INITIAL_DELAY_SYNC + 55000) + "}")
	private long initialDelayPruner;
	
	@Value("${" + PROP_DELAY_PRUNER + ":" + DEFAULT_DELAY_PRUNER + "}")
	private long delayPruner;
	
	@Value("${" + PROP_PRUNER_ENABLED + ":false}")
	private boolean prunerEnabled;
	
	@Value("${" + PROP_ARCHIVES_MAX_AGE_DAYS + ":}")
	private Integer archivesMaxAgeInDays;
	
	private static List<SiteParentTask> siteTasks;
	
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
			
			startSiteParentTasks(sites);
			
			if (prunerEnabled) {
				if (archivesMaxAgeInDays == null) {
					log.error(PROP_ARCHIVES_MAX_AGE_DAYS + " is required when " + PROP_PRUNER_ENABLED + " is set to true");
					AppUtils.shutdown();
				}
				
				log.info("Pruning sync archives older than " + archivesMaxAgeInDays + " days");
				
				startPrunerTask();
			}
			
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
			
			if (siteTasks != null) {
				siteTasks.forEach(task -> {
					AppUtils.shutdownExecutor(task.getChildExecutor(),
					    task.getSiteInfo().getName() + " " + ReceiverConstants.CHILD_TASK_NAME, true);
				});
			}
			
			AppUtils.shutdownExecutor(siteExecutor, ReceiverConstants.PARENT_TASK_NAME, false);
		}
		
	}
	
	private void startSiteParentTasks(Collection<SiteInfo> sites) {
		List<Class<? extends Runnable>> disabledTaskClasses = disabledTaskTypes.stream().map(t -> t.getTaskClass())
		        .collect(Collectors.toList());
		
		siteTasks = new ArrayList(sites.size());
		
		sites.stream().forEach(site -> {
			SiteParentTask task = new SiteParentTask(site, syncExecutor, disabledTaskClasses);
			siteExecutor.scheduleWithFixedDelay(task, siteTaskInitialDelay, siteTaskDelay, MILLISECONDS);
			siteTasks.add(task);
		});
	}
	
	private void startPrunerTask() {
		ReceiverArchivePruningTask pruner = new ReceiverArchivePruningTask(archivesMaxAgeInDays);
		siteExecutor.scheduleWithFixedDelay(pruner, initialDelayPruner, delayPruner, MILLISECONDS);
	}
	
}
