package org.openmrs.eip.app.receiver.processor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.openmrs.eip.app.receiver.SyncHelper;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for processors that synchronize the payload and apply the new state in the receiver
 * database.
 */
public abstract class BaseSyncProcessor<T extends AbstractEntity> extends BaseQueueProcessor<T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseSyncProcessor.class);
	
	//Used to temporarily store the entities being processed at any point in time across all sites threads.
	private static Set<String> PROCESSING_MSG_QUEUE = Collections.synchronizedSet(new HashSet<>());
	
	private SyncHelper syncHelper;
	
	public BaseSyncProcessor(ThreadPoolExecutor executor, SyncHelper syncHelper) {
		super(executor);
		this.syncHelper = syncHelper;
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfModelClassHierarchy(logicalType);
	}
	
	@Override
	public void processItem(T item) {
		String modelClass = getLogicalType(item);
		String uuid = getUniqueId(item);
		if (ReceiverUtils.isSubclass(modelClass)) {
			modelClass = ReceiverUtils.getParentModelClassName(modelClass);
		}
		
		final String uniqueId = modelClass + "#" + uuid;
		boolean removeId = false;
		try {
			//We could ignore inserts because we don't expect any events for the entity from other sites yet BUT in a
			//very rare case, this could be a message we previously processed but was never removed from the queue
			//and it is just getting re-processed so the entity could have been already been imported by other sites
			//and then we actually have events for the same entity from other sites
			if (!PROCESSING_MSG_QUEUE.add(uniqueId)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Postponed sync of {} because another item is processing for the same entity", item);
				}
				
				return;
			}
			
			removeId = true;
			beforeSync(item);
			syncHelper.sync(JsonUtils.unmarshalSyncModel(getSyncPayload(item)), false);
			afterSync(item);
		}
		catch (ConflictsFoundException e) {
			onConflict(item);
		}
		catch (Throwable t) {
			if (AppUtils.isShuttingDown()) {
				LOG.info("Ignoring the error because the application is shutting down");
				return;
			}
			
			Throwable cause = ExceptionUtils.getRootCause(t);
			if (cause == null) {
				cause = t;
			}
			
			String errMsg = cause.getMessage();
			if (errMsg.length() > 1024) {
				errMsg = errMsg.substring(0, 1024);
			}
			
			onError(item, cause.getClass().getName(), errMsg);
		}
		finally {
			if (removeId) {
				PROCESSING_MSG_QUEUE.remove(uniqueId);
			}
		}
	}
	
	protected abstract void beforeSync(T item);
	
	protected abstract String getSyncPayload(T item);
	
	protected abstract void afterSync(T item);
	
	protected abstract void onConflict(T item);
	
	protected abstract void onError(T item, String exceptionClass, String errorMsg);
	
}
