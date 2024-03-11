package org.openmrs.eip.app.sender;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.DEFAULT_DELAY_PRUNER;
import static org.openmrs.eip.app.SyncConstants.PROP_ARCHIVES_MAX_AGE_DAYS;
import static org.openmrs.eip.app.SyncConstants.PROP_DELAY_PRUNER;
import static org.openmrs.eip.app.SyncConstants.PROP_DELAY_RECONCILER;
import static org.openmrs.eip.app.SyncConstants.PROP_DELAY_SYNC;
import static org.openmrs.eip.app.SyncConstants.PROP_DELAY_TABLE_RECONCILER;
import static org.openmrs.eip.app.SyncConstants.PROP_INITIAL_DELAY_PRUNER;
import static org.openmrs.eip.app.SyncConstants.PROP_INITIAL_DELAY_RECONCILER;
import static org.openmrs.eip.app.SyncConstants.PROP_INITIAL_DELAY_SYNC;
import static org.openmrs.eip.app.SyncConstants.PROP_INITIAL_DELAY_TABLE_RECONCILER;
import static org.openmrs.eip.app.SyncConstants.PROP_PRUNER_ENABLED;
import static org.openmrs.eip.app.sender.SenderConstants.BEAN_NAME_SCHEDULED_EXECUTOR;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_BINLOG_MAX_KEEP_COUNT;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_BINLOG_PURGER_ENABLED;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_OFFSET_FILENAME;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DELAY_BINLOG_PURGER;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_INITIAL_DELAY_BINLOG_PURGER;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseCamelListener;
import org.openmrs.eip.app.sender.reconcile.SenderReconcileTask;
import org.openmrs.eip.app.sender.reconcile.SenderTableReconcileTask;
import org.openmrs.eip.app.sender.task.SenderSyncMessageTask;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Watches for specific camel events and responds to them accordingly
 */
@Component
@Profile(SyncProfiles.SENDER)
public class SenderCamelListener extends BaseCamelListener {
	
	protected static final Logger log = LoggerFactory.getLogger(SenderCamelListener.class);
	
	private static final int DEFAULT_INITIAL_DELAY = 5000;
	
	private static final int DEFAULT_DELAY = 300000;
	
	private static final int DEFAULT_BINLOG_MAX_KEEP_COUNT = 100;
	
	private static final int DEFAULT_INITIAL_DELAY_BINLOG_PURGER = 60000;
	
	private static final int DEFAULT_DELAY_BINLOG_PURGER = 86400000;
	
	private ScheduledExecutorService scheduledExecutor;
	
	@Value("${" + PROP_BINLOG_PURGER_ENABLED + ":false}")
	private boolean binlogPurgerEnabled;
	
	@Value("${" + PROP_BINLOG_MAX_KEEP_COUNT + ":" + DEFAULT_BINLOG_MAX_KEEP_COUNT + "}")
	private int binlogMaxKeepCount;
	
	@Value("${" + PROP_INITIAL_DELAY_BINLOG_PURGER + ":" + DEFAULT_INITIAL_DELAY_BINLOG_PURGER + "}")
	private long initialDelayBinlogPurger;
	
	@Value("${" + PROP_DELAY_BINLOG_PURGER + ":" + DEFAULT_DELAY_BINLOG_PURGER + "}")
	private long delayBinlogPurger;
	
	@Value("${" + PROP_INITIAL_DELAY_PRUNER + ":" + DEFAULT_INITIAL_DELAY + "}")
	private long initialDelayPruner;
	
	@Value("${" + PROP_DELAY_PRUNER + ":" + DEFAULT_DELAY_PRUNER + "}")
	private long delayPruner;
	
	@Value("${" + PROP_PRUNER_ENABLED + ":false}")
	private boolean prunerEnabled;
	
	@Value("${" + PROP_ARCHIVES_MAX_AGE_DAYS + ":}")
	private Integer archivesMaxAgeInDays;
	
	@Value("${" + PROP_INITIAL_DELAY_RECONCILER + ":" + DEFAULT_INITIAL_DELAY + "}")
	private long initDelayReconciler;
	
	@Value("${" + PROP_DELAY_RECONCILER + ":" + DEFAULT_DELAY + "}")
	private long delayReconciler;
	
	@Value("${" + PROP_INITIAL_DELAY_TABLE_RECONCILER + ":" + DEFAULT_INITIAL_DELAY + "}")
	private long initDelayTblReconciler;
	
	@Value("${" + PROP_DELAY_TABLE_RECONCILER + ":" + DEFAULT_DELAY + "}")
	private long delayTblReconciler;
	
	@Value("${" + PROP_INITIAL_DELAY_SYNC + ":" + DEFAULT_INITIAL_DELAY + "}")
	private long initialDelaySync;
	
	@Value("${" + PROP_DELAY_SYNC + ":" + DEFAULT_DELAY + "}")
	private long delaySync;
	
	public SenderCamelListener(@Qualifier(BEAN_NAME_SCHEDULED_EXECUTOR) ScheduledExecutorService scheduledExecutor,
	    @Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor syncExecutor) {
		super(syncExecutor);
		this.scheduledExecutor = scheduledExecutor;
	}
	
	@Override
	public void applicationStarted() {
		startTasks();
	}
	
	@Override
	public void applicationStopped() {
		AppUtils.shutdownExecutor(scheduledExecutor, "scheduled", false);
	}
	
	private void startTasks() {
		log.info("Starting tasks");
		SenderSyncMessageTask syncTask = new SenderSyncMessageTask();
		scheduledExecutor.scheduleWithFixedDelay(syncTask, initialDelaySync, delaySync, MILLISECONDS);
		SenderReconcileTask recTask = new SenderReconcileTask();
		scheduledExecutor.scheduleWithFixedDelay(recTask, initDelayReconciler, delayReconciler, MILLISECONDS);
		SenderTableReconcileTask tblRecTask = new SenderTableReconcileTask();
		scheduledExecutor.scheduleWithFixedDelay(tblRecTask, initDelayTblReconciler, delayTblReconciler, MILLISECONDS);
		
		if (binlogPurgerEnabled) {
			String file = SyncContext.getBean(Environment.class).getProperty(PROP_DBZM_OFFSET_FILENAME);
			BinlogPurgingTask task = new BinlogPurgingTask(FileUtils.instantiateFile(file), binlogMaxKeepCount);
			scheduledExecutor.scheduleWithFixedDelay(task, initialDelayBinlogPurger, delayBinlogPurger, MILLISECONDS);
		}
		
		if (prunerEnabled) {
			if (archivesMaxAgeInDays == null) {
				log.error(PROP_ARCHIVES_MAX_AGE_DAYS + " is required when " + PROP_PRUNER_ENABLED + " is set to true");
				AppUtils.shutdown();
			}
			
			log.info("Pruning sync archives older than " + archivesMaxAgeInDays + " days");
			
			SenderArchivePruningTask pruner = new SenderArchivePruningTask(archivesMaxAgeInDays);
			scheduledExecutor.scheduleWithFixedDelay(pruner, initialDelayPruner, delayPruner, MILLISECONDS);
		}
	}
	
}
