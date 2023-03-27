package org.openmrs.eip.app.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.SendToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Superclass for post sync action processors that send an item to a camel endpoint uri for
 * processing
 */
public abstract class BaseSendToCamelPostSyncActionProcessor extends BaseQueueProcessor<SyncedMessage> implements SendToCamelEndpointProcessor<SyncedMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseSendToCamelPostSyncActionProcessor.class);
	
	private String endpointUri;
	
	protected ProducerTemplate producerTemplate;
	
	protected SyncedMessageRepository repo;
	
	public BaseSendToCamelPostSyncActionProcessor(String endpointUri, ProducerTemplate producerTemplate,
	    ThreadPoolExecutor executor, SyncedMessageRepository repo) {
		super(executor);
		this.endpointUri = endpointUri;
		this.producerTemplate = producerTemplate;
		this.repo = repo;
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
		return item.getSite().getIdentifier() + "-" + item.getMessageUuid() + "-"
		        + AppUtils.getSimpleName(item.getModelClassName()) + "-" + item.getIdentifier();
	}
	
	@Override
	public void processWork(List<SyncedMessage> items) throws Exception {
		//Squash events for the same entity so that exactly one message is processed in case of multiple in this run in 
		//an effort to reduce calls to OpenMRS endpoints. Delete being a terminal event, squash for a single entity will
		//stop at the last event before a delete event to ensure we don't re-process a non-existent entity
		Map<String, SyncedMessage> keyAndEarliestMsgMap = new HashMap(items.size());
		List<SyncedMessage> squashedMsgs = new ArrayList();
		items.stream().forEach(msg -> {
			String modelClass = msg.getModelClassName();
			if (ReceiverUtils.isSubclass(modelClass)) {
				String parentClass = ReceiverUtils.getParentModelClassName(modelClass);
				
				if (log.isTraceEnabled()) {
					log.trace("Parent model class name for " + modelClass + " is " + parentClass);
				}
				
				modelClass = parentClass;
			}
			
			String key = modelClass + "#" + msg.getIdentifier();
			if (!keyAndEarliestMsgMap.containsKey(key)) {
				keyAndEarliestMsgMap.put(key, msg);
			} else {
				if (msg.getOperation() != SyncOperation.d) {
					squashedMsgs.add(msg);
					
					if (log.isTraceEnabled()) {
						log.trace("Squashing entity msg -> " + msg);
					}
				} else {
					if (log.isTraceEnabled()) {
						log.trace("Entity msg squash stopping, skipping delete msg -> " + msg);
					}
				}
			}
		});
		
		//Call the endpoint only for the earliest events for each entity
		doProcessWork(new ArrayList(keyAndEarliestMsgMap.values()));
		
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
			send(endpointUri, item, producerTemplate);
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
