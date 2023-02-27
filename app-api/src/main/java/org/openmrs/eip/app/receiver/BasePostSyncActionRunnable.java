package org.openmrs.eip.app.receiver;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

/**
 * Superclass for {@link PostSyncAction} processor tasks
 */
public abstract class BasePostSyncActionRunnable extends BaseSiteRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(BasePostSyncActionRunnable.class);
	
	protected PostSyncActionRepository repo;
	
	public BasePostSyncActionRunnable(SiteInfo site) {
		super(site);
		repo = SyncContext.getBean(PostSyncActionRepository.class);
	}
	
	@Override
	public boolean doRun() throws Exception {
		try {
			List<PostSyncAction> actions = getNextBatch();
			if (actions.isEmpty()) {
				if (log.isTraceEnabled()) {
					log.trace("No post sync actions of type " + getActionType() + " found for site: " + getSite());
				}
				
				return true;
			}
			
			List<PostSyncAction> completed;
			try {
				completed = process(actions);
			}
			catch (Throwable t) {
				log.warn("An error occurred while processing post sync actions of type " + getActionType() + " for site: "
				        + getSite(),
				    t);
				
				Throwable rootCause = ExceptionUtils.getRootCause(t);
				if (rootCause != null) {
					t = rootCause;
				}
				
				String errorMsg = t.toString().trim();
				if (errorMsg.length() > 1024) {
					errorMsg = errorMsg.substring(0, 1024).trim();
				}
				
				ReceiverUtils.updatePostSyncActionStatuses(actions, false, errorMsg);
				
				return false;
			}
			
			if (!completed.isEmpty()) {
				if (log.isTraceEnabled()) {
					log.trace("Successfully processed " + completed.size() + " post sync action(s) of type "
					        + getActionType() + " for site: " + getSite());
				}
				
				ReceiverUtils.updatePostSyncActionStatuses(completed, true, null);
			}
			
			List<PostSyncAction> failed = (List) CollectionUtils.subtract(actions, completed);
			if (!failed.isEmpty()) {
				if (log.isTraceEnabled()) {
					log.trace("Failed to process " + completed.size() + " post sync action(s) of type " + getActionType()
					        + " for site: " + getSite());
				}
				
				ReceiverUtils.updatePostSyncActionStatuses(failed, false, null);
			}
		}
		catch (Throwable t) {
			log.error("An unhandled error occurred while processing post sync actions of type " + getActionType()
			        + " for site: " + getSite(),
			    t);
		}
		
		return false;
	}
	
	/**
	 * Gets the next batch of PostSyncActions items to process
	 *
	 * @return List of post sync actions
	 */
	protected List<PostSyncAction> getNextBatch() {
		return repo.getBatchOfPendingActions(getSite(), getActionType(), getPageable());
	}
	
	/**
	 * The {@link PostSyncActionType} handled by this processor
	 * 
	 * @return PostSyncActionType
	 */
	public abstract PostSyncActionType getActionType();
	
	/**
	 * Gets the {@link Pageable} object to determine the batch size
	 * 
	 * @return Pageable object
	 */
	public abstract Pageable getPageable();
	
	/**
	 * Processes the specified list of post sync action items
	 * 
	 * @param actions list of post sync actions
	 * @return list of post sync actions that successfully completed
	 * @throws Exception
	 */
	public abstract List<PostSyncAction> process(List<PostSyncAction> actions) throws Exception;
	
}
