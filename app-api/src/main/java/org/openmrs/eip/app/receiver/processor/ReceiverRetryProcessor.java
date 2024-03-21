package org.openmrs.eip.app.receiver.processor;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.openmrs.eip.app.receiver.SyncHelper;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Re-processes a receiver retry item.
 */
@Component("receiverRetryProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverRetryProcessor extends BaseQueueProcessor<ReceiverRetryQueueItem> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverRetryProcessor.class);
	
	//Used to temporarily store the entities being processed at any point in time across all sites
	private static Set<String> PROCESSING_MSG_QUEUE;
	
	private ReceiverService service;
	
	private SyncHelper syncHelper;
	
	public ReceiverRetryProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, ReceiverService service,
	    SyncHelper syncHelper) {
		super(executor);
		this.service = service;
		this.syncHelper = syncHelper;
		PROCESSING_MSG_QUEUE = Collections.synchronizedSet(new HashSet<>(executor.getMaximumPoolSize()));
	}
	
	@Override
	public String getProcessorName() {
		return "retry";
	}
	
	@Override
	public String getQueueName() {
		return "retry";
	}
	
	@Override
	public String getUniqueId(ReceiverRetryQueueItem item) {
		return item.getIdentifier();
	}
	
	@Override
	public String getThreadName(ReceiverRetryQueueItem msg) {
		return msg.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-"
		        + msg.getIdentifier() + "-" + msg.getMessageUuid();
	}
	
	@Override
	public String getLogicalType(ReceiverRetryQueueItem item) {
		return item.getModelClassName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfModelClassHierarchy(logicalType);
	}
	
	@Override
	public void processItem(ReceiverRetryQueueItem msg) {
		//TODO Move this logic that ensures no threads process events for the same entity to message-processor route
		String modelClass = msg.getModelClassName();
		String uuid = msg.getIdentifier();
		if (ReceiverUtils.isSubclass(modelClass)) {
			modelClass = ReceiverUtils.getParentModelClassName(modelClass);
		}
		
		final String uniqueId = modelClass + "#" + uuid;
		boolean removeId = false;
		try {
			//TODO Use the same PROCESSING_MSG_QUEUE as the SyncMessageProcessor
			if (!PROCESSING_MSG_QUEUE.add(uniqueId)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Postponed sync of {} because a site thread is processing an event for the same entity", msg);
				}
				
				return;
			}
			
			removeId = true;
			LOG.info("Re-processing message");
			
			//TODO Add logic
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Done re-processing message");
			}
		}
		catch (ConflictsFoundException e) {
			service.moveToConflictQueue(msg);
		}
		catch (Throwable t) {
			//TODO Updated retry item
		}
		finally {
			if (removeId) {
				PROCESSING_MSG_QUEUE.remove(uniqueId);
			}
		}
	}
	
}
