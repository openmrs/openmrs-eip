package org.openmrs.eip.app.receiver;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseTask;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetches all the existing conflicts and forwards them to the {@link ConflictVerifyingProcessor}.
 */
public class ConflictVerifyingTask extends BaseTask {
	
	protected static final Logger log = LoggerFactory.getLogger(ConflictVerifyingTask.class);
	
	private static class ConflictVerifyingTaskHolder {
		
		private static ConflictVerifyingTask INSTANCE = new ConflictVerifyingTask();
		
	}
	
	public static ConflictVerifyingTask getInstance() {
		return ConflictVerifyingTaskHolder.INSTANCE;
	}
	
	public static boolean isExecuting = false;
	
	private ConflictVerifyingProcessor processor;
	
	private ConflictRepository repo;
	
	private ConflictVerifyingTask() {
		processor = SyncContext.getBean(ConflictVerifyingProcessor.class);
		repo = SyncContext.getBean(ConflictRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "conflict verifier task";
	}
	
	@Override
	public boolean doRun() throws Exception {
		if (!isExecuting) {
			try {
				isExecuting = true;
				List<Long> ids = repo.getConflictIds();
				if (ids.isEmpty()) {
					log.info("No conflicts found to verify");
				} else {
					log.info("Verifying " + ids.size() + " conflict(s)");
					
					StopWatch stopWatch = new StopWatch();
					stopWatch.start();
					
					int pageSize = AppUtils.getTaskPage().getPageSize();
					List<Long> idsBatch = new ArrayList(pageSize);
					Long lastId = ids.get(ids.size() - 1);
					for (Long id : ids) {
						idsBatch.add(id);
						if (idsBatch.size() == pageSize || id.equals(lastId)) {
							try {
								processor.processWork(repo.findAllById(idsBatch));
							}
							finally {
								idsBatch.clear();
							}
						}
					}
					
					stopWatch.stop();
					String timeTaken = DurationFormatUtils.formatDuration(stopWatch.getTime(), "HH:mm:ss", true);
					
					log.info("Conflict verification task took: " + timeTaken);
				}
			}
			finally {
				isExecuting = false;
			}
		} else {
			log.info(getTaskName() + " is already running");
		}
		
		return true;
	}
	
}
