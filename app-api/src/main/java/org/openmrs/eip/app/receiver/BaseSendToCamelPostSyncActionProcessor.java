package org.openmrs.eip.app.receiver;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.SendToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;

/**
 * Superclass for post sync action processors that send an item to a camel endpoint uri for
 * processing
 */
public abstract class BaseSendToCamelPostSyncActionProcessor extends BaseQueueProcessor<SyncedMessage> implements SendToCamelEndpointProcessor<SyncedMessage> {
	
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
	public void processItem(SyncedMessage item) {
		send(endpointUri, item, producerTemplate);
		onSuccess(item);
	}
	
	/**
	 * Post-processes the message upon success
	 *
	 * @param item the item that was successfully processed
	 */
	public abstract void onSuccess(SyncedMessage item);
	
}
