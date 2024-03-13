package org.openmrs.eip.app.receiver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Superclass for post sync action processors.
 */
public abstract class BasePostSyncActionProcessor extends BaseQueueProcessor<SyncedMessage> implements HttpRequestProcessor<SyncedMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(BasePostSyncActionProcessor.class);
	
	private CustomHttpClient client;
	
	private String resource;
	
	public BasePostSyncActionProcessor(ThreadPoolExecutor executor, CustomHttpClient client, String resource) {
		super(executor);
		this.client = client;
		this.resource = resource;
	}
	
	@Override
	public String getUniqueId(SyncedMessage item) {
		return item.getId().toString();
	}
	
	@Override
	public String getLogicalType(SyncedMessage item) {
		//Since we squash msgs for the same entity so need to worry about parallel msg processing for the same entity
		return item.getClass().getName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		//Since we squash events for the same entity so need to worry about parallel msg processing for the same entity
		return null;
	}
	
	@Override
	public String getThreadName(SyncedMessage item) {
		return item.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(item.getModelClassName()) + "-"
		        + item.getIdentifier() + "-" + item.getMessageUuid();
	}
	
	@Override
	public void processWork(List<SyncedMessage> items) throws Exception {
		//Squash events for the same entity so that exactly one message is processed in case of multiple in this run in 
		//an effort to reduce calls to OpenMRS endpoints. Delete being a terminal event, squash for a single entity will
		//stop at the last event before a delete event to ensure we don't re-process a non-existent entity
		Map<String, SyncedMessage> entityKeyAndEarliestMsgMap = new LinkedHashMap(items.size());
		List<SyncedMessage> squashedMsgs = new ArrayList();
		items.stream().forEach(msg -> {
			String modelClass = msg.getModelClassName();
			if (ReceiverUtils.isSubclass(modelClass)) {
				modelClass = ReceiverUtils.getParentModelClassName(modelClass);
			}
			
			String key = modelClass + "#" + msg.getIdentifier();
			if (!entityKeyAndEarliestMsgMap.containsKey(key)) {
				entityKeyAndEarliestMsgMap.put(key, msg);
			} else {
				if (msg.getOperation() != SyncOperation.d) {
					squashedMsgs.add(msg);
					
					if (log.isTraceEnabled()) {
						log.trace("Squashing entity msg -> " + msg);
					}
				} else {
					if (log.isTraceEnabled()) {
						log.trace("Squashing stopped for " + key + ", postponing processing of delete msg -> " + msg);
					}
				}
			}
		});
		
		//Also squash delete events for entities of the same type since for delete messages we clear the cache or update 
		//the search index for all entities of that type, to avoid multiple calls that have the same result.
		Set<String> entityTypesWithDeletes = new HashSet(entityKeyAndEarliestMsgMap.size());
		List<SyncedMessage> msgsToProcess = new ArrayList(entityKeyAndEarliestMsgMap.size());
		entityKeyAndEarliestMsgMap.values().stream().forEach(msg -> {
			if (msg.getOperation() != SyncOperation.d) {
				msgsToProcess.add(msg);
				if (log.isTraceEnabled()) {
					log.trace("Queuing for processing -> " + msg);
				}
			} else {
				String modelClass = msg.getModelClassName();
				if (ReceiverUtils.isSubclass(modelClass)) {
					modelClass = ReceiverUtils.getParentModelClassName(modelClass);
				}
				
				if (!entityTypesWithDeletes.contains(modelClass)) {
					msgsToProcess.add(msg);
					entityTypesWithDeletes.add(modelClass);
				} else {
					squashedMsgs.add(msg);
					
					if (log.isTraceEnabled()) {
						log.trace("Squashing delete msg -> " + msg);
					}
				}
			}
		});
		
		doProcessWork(msgsToProcess);
		
		//We are not worried about the index and cache tasks updating the state for the same entity at the same time
		//because they never process the same item in parallel since indexer only processes evicted sync messages
		
		//TODO Delegate to a separate processor for squashed messages to avoid the hacky logic below
		//Squashed events get marked as processed without calling OpenMRS endpoints
		squashedMsgs.stream().forEach(msg -> updateSquashedMessage(msg));
		
		doProcessWork(squashedMsgs);
	}
	
	protected void doProcessWork(List<SyncedMessage> items) throws Exception {
		super.processWork(items);
	}
	
	@Override
	public void processItem(SyncedMessage item) {
		if (!isSquashed(item)) {
			sendRequest(resource, item, client);
		}
		
		onSuccess(item);
	}
	
	/**
	 * Post-processes the message upon success
	 *
	 * @param item the item that was successfully processed
	 */
	public abstract void onSuccess(SyncedMessage item);
	
	/**
	 * Checks if the specified item is squashed
	 *
	 * @param item the item to check
	 * @return true if the item is squashed otherwise false
	 */
	public abstract boolean isSquashed(SyncedMessage item);
	
	/**
	 * Processes the specified squashed {@link SyncedMessage}
	 *
	 * @param item the squashed {@link SyncedMessage} to process
	 */
	public abstract void updateSquashedMessage(SyncedMessage item);
	
}
