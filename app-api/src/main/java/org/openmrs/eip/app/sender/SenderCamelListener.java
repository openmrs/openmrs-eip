package org.openmrs.eip.app.sender;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openmrs.eip.app.sender.SenderConstants.BEAN_NAME_SCHEDULED_EXECUTOR;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_BINLOG_MAX_KEEP_COUNT;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_BINLOG_PURGER_ENABLED;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_OFFSET_FILENAME;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DELAY_BINLOG_PURGER;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_INITIAL_DELAY_BINLOG_PURGER;

import java.util.concurrent.ScheduledExecutorService;

import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.openmrs.eip.app.AppUtils;
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
public class SenderCamelListener extends EventNotifierSupport {
	
	protected static final Logger log = LoggerFactory.getLogger(SenderCamelListener.class);
	
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
	
	public SenderCamelListener(@Qualifier(BEAN_NAME_SCHEDULED_EXECUTOR) ScheduledExecutorService scheduledExecutor) {
		this.scheduledExecutor = scheduledExecutor;
	}
	
	@Override
	public void notify(CamelEvent event) throws Exception {
		if (event instanceof CamelEvent.CamelContextStartedEvent) {
			if (binlogPurgerEnabled) {
				log.info("Starting tasks");
				
				String file = SyncContext.getBean(Environment.class).getProperty(PROP_DBZM_OFFSET_FILENAME);
				BinlogPurgingTask task = new BinlogPurgingTask(FileUtils.instantiateFile(file), binlogMaxKeepCount);
				scheduledExecutor.scheduleWithFixedDelay(task, initialDelayBinlogPurger, delayBinlogPurger, MILLISECONDS);
			}
		} else if (event instanceof CamelEvent.CamelContextStoppingEvent) {
			AppUtils.shutdownExecutor(scheduledExecutor, "scheduled", false);
		}
	}
	
}
