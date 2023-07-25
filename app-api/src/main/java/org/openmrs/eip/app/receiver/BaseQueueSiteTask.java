package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

/**
 * Base class for tasks that read items from a database queue table and forward them to a processor
 * for processing
 * 
 * @see BaseQueueProcessor
 * @param <T> the queue entity type
 * @param <P> the processor type
 */
public abstract class BaseQueueSiteTask<T extends AbstractEntity, P extends BaseQueueProcessor<T>> extends BaseSiteRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseQueueSiteTask.class);
	
	private final P processor;
	
	private Pageable page;
	
	public BaseQueueSiteTask(SiteInfo site, P processor, Pageable page) {
		super(site);
		this.processor = processor;
		this.page = page;
	}
	
	@Override
	public boolean doRun() throws Exception {
		List<T> items = getNextBatch(page);
		if (items.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("No items found");
			}
			
			return true;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Submitting to processor " + items.size() + " items(s)");
		}
		
		processor.processWork(items);
		
		return false;
	}
	
	/**
	 * Gets the next batch of items to process
	 * 
	 * @param page {@link Pageable} object
	 * @return List of items
	 */
	public abstract List<T> getNextBatch(Pageable page);
	
}
