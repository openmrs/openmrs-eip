package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.SendToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;

/**
 * Superclass for PostSyncAction processors that send an item to a camel endpoint uri for processing
 */
public abstract class BaseSendToCamelPostSyncActionProcessor extends BaseQueueProcessor<PostSyncAction> implements SendToCamelEndpointProcessor<PostSyncAction> {
	
	private String endpointUri;
	
	protected ProducerTemplate producerTemplate;
	
	private PostSyncActionRepository repo;
	
	public BaseSendToCamelPostSyncActionProcessor(String endpointUri, ProducerTemplate producerTemplate,
	    PostSyncActionRepository repo) {
		this.endpointUri = endpointUri;
		this.producerTemplate = producerTemplate;
		this.repo = repo;
	}
	
	@Override
	public void processItem(PostSyncAction item) {
		send(endpointUri, item, producerTemplate);
	}
	
	@Override
	public void onSuccess(PostSyncAction item) {
		item.markAsCompleted();
		repo.save(item);
	}
	
	@Override
	public void onFailure(PostSyncAction item, Throwable throwable) {
		item.markAsProcessedWithError(ReceiverUtils.getErrorMessage(throwable));
		repo.save(item);
		SendToCamelEndpointProcessor.super.onFailure(item, throwable);
	}
	
}
