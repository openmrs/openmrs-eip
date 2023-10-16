package org.openmrs.eip.app.receiver;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.BaseTask;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetches all the existing conflicts and forwards them to the processor.
 */
public abstract class BaseConflictTask<P extends BasePureParallelQueueProcessor<ConflictQueueItem>> extends BaseTask {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseConflictTask.class);
	
	private boolean started = false;
	
	private P processor;
	
	private ConflictRepository repo;
	
	public BaseConflictTask(P processor) {
		this.processor = processor;
		repo = SyncContext.getBean(ConflictRepository.class);
	}
	
	/**
	 * Gets the started
	 *
	 * @return true if started otherwise false
	 */
	public boolean isStarted() {
		return started;
	}
	
	@Override
	public boolean doRun() throws Exception {
		if (!started) {
			log.info("Starting " + getTaskName());
			
			try {
				started = true;
				List<Long> ids = repo.getConflictIds();
				if (ids.isEmpty()) {
					log.info("No conflicts found");
				} else {
					log.info("Found " + ids.size() + " conflict(s)");
					
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
					
					log.info(getTaskName() + " took: " + timeTaken);
				}
			}
			finally {
				started = false;
			}
		} else {
			log.info(getTaskName() + " is already started");
		}
		
		return true;
	}
	
}
