package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Superclass for {@link PostSyncAction} processor tasks
 */
public abstract class BasePostSyncActionRunnable extends BaseSiteRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(BasePostSyncActionRunnable.class);
	
	protected PostSyncActionType actionType;
	
	protected Pageable pageable;
	
	protected PostSyncActionRepository repo;
	
	public BasePostSyncActionRunnable(SiteInfo site, PostSyncActionType actionType, int batchSize) {
		super(site);
		this.actionType = actionType;
		//TODO Configure batch size
		this.pageable = PageRequest.of(0, batchSize);
		repo = SyncContext.getBean(PostSyncActionRepository.class);
	}
	
	@Override
	public boolean doRun() throws Exception {
		try {
			List<PostSyncAction> actions = getNextBatch();
			if (actions.isEmpty()) {
				if (log.isTraceEnabled()) {
					log.trace("No post sync actions of type " + actionType + " found for site: " + getSite());
				}
				
				return true;
			}
			
			process(actions);
		}
		catch (Throwable t) {
			log.error(
			    "An error occurred while processing post sync actions of type " + actionType + " for site: " + getSite(), t);
		}
		
		return false;
	}
	
	/**
	 * Gets the next batch of PostSyncActions items to process
	 *
	 * @return List of post sync actions
	 */
	public List<PostSyncAction> getNextBatch() {
		return repo.getOrderedBatchOfPendingActions(getSite(), actionType, pageable);
	}
	
	/**
	 * Processes the specified list of post sync action items
	 * 
	 * @param actions list of post sync actions
	 * @throws Exception
	 */
	public abstract void process(List<PostSyncAction> actions) throws Exception;
	
}
