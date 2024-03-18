package org.openmrs.eip.app.receiver.processor;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ERR_MSG;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ERR_TYPE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_FOUND_CONFLICT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
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
	
	private ProducerTemplate producerTemplate;
	
	public SyncMessageProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, ReceiverService service,
	    ProducerTemplate producerTemplate) {
		super(executor);
		this.service = service;
		this.producerTemplate = producerTemplate;
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
		Exchange exchange = ExchangeBuilder.anExchange(producerTemplate.getCamelContext()).withBody(msg).build();
		//TODO Move this logic that ensures no threads process events for the same entity to message-processor route
		String modelClass = msg.getModelClassName();
		if (ReceiverUtils.isSubclass(modelClass)) {
			modelClass = ReceiverUtils.getParentModelClassName(modelClass);
		}
		
		final String uniqueId = modelClass + "#" + msg.getIdentifier();
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
			CamelUtils.send(ReceiverConstants.URI_MSG_PROCESSOR, exchange);
		}
		finally {
			if (removeId) {
				PROCESSING_MSG_QUEUE.remove(uniqueId);
			}
		}
		
		boolean foundConflict = exchange.getProperty(EX_PROP_FOUND_CONFLICT, false, Boolean.class);
		String errorType = exchange.getProperty(EX_PROP_ERR_TYPE, String.class);
		String errorMsg = exchange.getProperty(EX_PROP_ERR_MSG, String.class);
		boolean msgProcessed = exchange.getProperty(EX_PROP_MSG_PROCESSED, false, Boolean.class);
		
		if (msgProcessed) {
			service.moveToSyncedQueue(msg, SyncOutcome.SUCCESS);
		} else if (foundConflict) {
			service.processConflictedSyncItem(msg);
		} else if (errorType != null) {
			service.processFailedSyncItem(msg, errorType, errorMsg);
		} else {
			throw new EIPException("Something went wrong while processing sync message -> " + msg);
		}
		
		LOG.info("Done processing message");
	}
	
}
