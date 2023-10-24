package org.openmrs.eip.app.sender;

import java.io.File;

import org.openmrs.eip.app.BaseTask;

/**
 * Deletes processed binary log files so that only a specific number of processed files is preserved
 * to save disk space.
 */
public class BinlogPurgingTask extends BaseTask {
	
	private File debeziumOffsetFile;
	
	private int maxKeepCount;
	
	public BinlogPurgingTask(File debeziumOffsetFile, int maxKeepCount) {
		this.debeziumOffsetFile = debeziumOffsetFile;
		this.maxKeepCount = maxKeepCount;
	}
	
	@Override
	public String getTaskName() {
		return "binlog task";
	}
	
	@Override
	public boolean doRun() throws Exception {
		BinlogUtils.purgeBinLogs(debeziumOffsetFile, maxKeepCount);
		return true;
	}
	
}
