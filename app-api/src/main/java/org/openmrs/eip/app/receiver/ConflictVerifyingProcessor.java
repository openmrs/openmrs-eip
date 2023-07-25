package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a ConflictQueueItem by checking if it has a valid hash on file, if the hash is valid it
 * gets moved to the retry queue for re-syncing otherwise stays in the conflict queue.
 */
@Component("conflictVerifyingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ConflictVerifyingProcessor extends BasePureParallelQueueProcessor<ConflictQueueItem> {
	
	protected static final Logger log = LoggerFactory.getLogger(ConflictVerifyingProcessor.class);
	
	private ConflictService service;
	
	private EntityServiceFacade serviceFacade;
	
	private ProducerTemplate producerTemplate;
	
	public ConflictVerifyingProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    ConflictService service, EntityServiceFacade serviceFacade, ProducerTemplate producerTemplate) {
		super(executor);
		this.service = service;
		this.serviceFacade = serviceFacade;
		this.producerTemplate = producerTemplate;
	}
	
	@Override
	public String getProcessorName() {
		return "conflict verifier";
	}
	
	@Override
	public String getQueueName() {
		return "conflict-verifier";
	}
	
	@Override
	public String getThreadName(ConflictQueueItem item) {
		return item.getMessageUuid();
	}
	
	@Override
	public void processItem(ConflictQueueItem item) {
		TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnumByModelClassName(item.getModelClassName());
		BaseModel dbModel = serviceFacade.getModel(tableToSyncEnum, item.getIdentifier());
		boolean move;
		String reason = null;
		if (dbModel != null) {
			Class<? extends BaseHashEntity> hashClass = TableToSyncEnum.getHashClass(dbModel);
			BaseHashEntity storedHash = HashUtils.getStoredHash(item.getIdentifier(), hashClass, producerTemplate);
			move = storedHash != null && HashUtils.computeHash(dbModel).equals(storedHash.getHash());
			if (move) {
				reason = "Moved from conflict queue because the hash on file is valid";
			}
			
			//TODO If Incoming and existing states match, also move the conflict to retry queue.
		} else {
			move = true;
			reason = "No entity found in the database associated to conflict item with id: " + item.getId();
		}
		
		if (move) {
			if (log.isDebugEnabled()) {
				log.debug(reason);
			}
			
			service.moveToRetryQueue(item, reason);
		}
	}
	
}
