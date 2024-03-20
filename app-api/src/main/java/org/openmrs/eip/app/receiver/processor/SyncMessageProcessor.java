package org.openmrs.eip.app.receiver.processor;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.openmrs.eip.app.receiver.SyncHelper;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Synchronizes the sync message payload to apply the new state in the receiver database.
 */
@Component("syncMessageProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SyncMessageProcessor extends BaseQueueProcessor<SyncMessage> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SyncMessageProcessor.class);
	
	//Used to temporarily store the entities being processed at any point in time across all sites
	private static Set<String> PROCESSING_MSG_QUEUE;
	
	private ReceiverService service;
	
	private SyncHelper syncHelper;
	
	public SyncMessageProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, ReceiverService service,
	    SyncHelper syncHelper) {
		super(executor);
		this.service = service;
		this.syncHelper = syncHelper;
		PROCESSING_MSG_QUEUE = Collections.synchronizedSet(new HashSet<>(executor.getMaximumPoolSize()));
	}
	
	@Override
	public String getProcessorName() {
		return "sync";
	}
	
	@Override
	public String getQueueName() {
		return "sync";
	}
	
	@Override
	public String getUniqueId(SyncMessage item) {
		return item.getIdentifier();
	}
	
	@Override
	public String getThreadName(SyncMessage msg) {
		return msg.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-"
		        + msg.getIdentifier() + "-" + msg.getMessageUuid();
	}
	
	@Override
	public String getLogicalType(SyncMessage item) {
		return item.getModelClassName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfModelClassHierarchy(logicalType);
	}
	
	@Override
	public void processItem(SyncMessage msg) {
		//TODO Move this logic that ensures no threads process events for the same entity to message-processor route
		String modelClass = msg.getModelClassName();
		String uuid = msg.getIdentifier();
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
					LOG.debug("Postponed sync of {} because another site is processing an event for the same entity", msg);
				}
				
				return;
			}
			
			removeId = true;
			LOG.info("Processing message");
			//Ensure there is no retry items in the queue for this entity so that changes in messages that happened later 
			// don't overwrite those that happened before them.
			if (service.hasRetryItem(uuid, modelClass)) {
				throw new EIPException("Entity still has earlier items in the retry queue");
			}
			
			syncHelper.sync(JsonUtils.unmarshalSyncModel(msg.getEntityPayload()), false);
			LOG.info("Done processing message");
		}
		catch (ConflictsFoundException e) {
			service.processConflictedSyncItem(msg);
		}
		catch (Throwable t) {
			Throwable cause = ExceptionUtils.getRootCause(t);
			if (cause == null) {
				cause = t;
			}
			
			service.processFailedSyncItem(msg, cause.getClass().getName(), cause.getMessage());
		}
		finally {
			if (removeId) {
				PROCESSING_MSG_QUEUE.remove(uniqueId);
			}
		}
	}
	
}
