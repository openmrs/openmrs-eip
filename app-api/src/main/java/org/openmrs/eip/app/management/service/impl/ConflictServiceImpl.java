package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;

import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.commons.beanutils.BeanUtils;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ConflictCacheEvictingProcessor;
import org.openmrs.eip.app.receiver.ConflictResolution;
import org.openmrs.eip.app.receiver.ConflictSearchIndexUpdatingProcessor;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("conflictService")
public class ConflictServiceImpl extends BaseService implements ConflictService {
	
	private static final Logger log = LoggerFactory.getLogger(ConflictServiceImpl.class);
	
	private ConflictRepository conflictRepo;
	
	private ReceiverRetryRepository retryRepo;
	
	private ReceiverSyncArchiveRepository archiveRepo;
	
	private ReceiverService receiverService;
	
	private EntityServiceFacade serviceFacade;
	
	private CamelContext camelContext;
	
	private ConflictCacheEvictingProcessor cacheEvictProcessor;
	
	private ConflictSearchIndexUpdatingProcessor indexUpdateProcessor;
	
	public ConflictServiceImpl(ConflictRepository conflictRepo, ReceiverRetryRepository retryRepo,
	    ReceiverSyncArchiveRepository archiveRepo, ReceiverService receiverService, CamelContext camelContext,
	    EntityServiceFacade serviceFacade, ConflictCacheEvictingProcessor cacheEvictProcessor,
	    ConflictSearchIndexUpdatingProcessor indexUpdateProcessor) {
		
		this.conflictRepo = conflictRepo;
		this.retryRepo = retryRepo;
		this.archiveRepo = archiveRepo;
		this.receiverService = receiverService;
		this.camelContext = camelContext;
		this.serviceFacade = serviceFacade;
		this.cacheEvictProcessor = cacheEvictProcessor;
		this.indexUpdateProcessor = indexUpdateProcessor;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public ReceiverRetryQueueItem moveToRetryQueue(ConflictQueueItem conflict, String reason) {
		if (log.isDebugEnabled()) {
			log.debug("Moving to retry queue the conflict item with id: " + conflict.getId());
		}
		
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem(conflict);
		retry.setMessage(reason);
		if (log.isDebugEnabled()) {
			log.debug("Saving retry item");
		}
		
		retry = retryRepo.save(retry);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved retry item, removing item from the conflict queue");
		}
		
		conflictRepo.delete(conflict);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the conflict queue");
		}
		
		return retry;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public ReceiverSyncArchive moveToArchiveQueue(ConflictQueueItem conflict) {
		if (log.isDebugEnabled()) {
			log.debug("Moving to archive queue the conflict item with id: " + conflict.getId());
		}
		
		ReceiverSyncArchive archive = new ReceiverSyncArchive(conflict);
		archive.setDateCreated(new Date());
		if (log.isDebugEnabled()) {
			log.debug("Saving archive item");
		}
		
		archive = archiveRepo.save(archive);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved archive item");
		}
		
		receiverService.updateHash(conflict.getModelClassName(), conflict.getIdentifier());
		
		if (log.isDebugEnabled()) {
			log.debug("Removing item from the conflict queue");
		}
		
		conflictRepo.delete(conflict);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully removed item from the conflict queue");
		}
		
		return archive;
	}
	
	@Override
	public void resolve(ConflictResolution resolution) {
		if (resolution.getConflict() == null) {
			throw new EIPException("Conflict is required");
		} else if (resolution.getDecision() == null) {
			throw new EIPException("Resolution is required");
		}
		
		ConflictQueueItem c = resolution.getConflict();
		log.info("Resolving conflict for item with uuid:" + c.getMessageUuid() + " with decision as: "
		        + resolution.getDecision());
		
		//TODO should we track the conflict resolution log?
		
		switch (resolution.getDecision()) {
			case IGNORE_NEW:
				moveToArchiveQueue(c);
				break;
			case SYNC_NEW:
				resolveWithNewState(resolution);
				break;
			case MERGE:
				resolveAsMerge(resolution);
				break;
		}
	}
	
	private void resolveWithNewState(ConflictResolution resolution) {
		ConflictQueueItem conflict = resolution.getConflict();
		receiverService.updateHash(conflict.getModelClassName(), conflict.getIdentifier());
		
		moveToRetryQueue(conflict, "Moved from conflict queue after conflict resolution");
	}
	
	private void resolveAsMerge(ConflictResolution resolution) {
		if (resolution.getSyncedProperties().isEmpty()) {
			throw new EIPException("No properties to sync specified for merge resolution decision");
		}
		
		ConflictQueueItem conflict = resolution.getConflict();
		receiverService.updateHash(conflict.getModelClassName(), conflict.getIdentifier());
		BaseModel newModel = JsonUtils.unmarshalSyncModel(conflict.getEntityPayload()).getModel();
		TableToSyncEnum syncEnum = TableToSyncEnum.getTableToSyncEnumByModelClassName(conflict.getModelClassName());
		BaseModel dbModel = serviceFacade.getModel(syncEnum, conflict.getIdentifier());
		for (String propertyName : resolution.getSyncedProperties()) {
			try {
				BeanUtils.copyProperty(dbModel, propertyName, BeanUtils.getProperty(newModel, propertyName));
			}
			catch (ReflectiveOperationException e) {
				throw new EIPException("Failed to set the value for property: " + propertyName, e);
			}
		}
		
		//TODO changeBy and dateChanged should be based on latest state.
		
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).withBody(dbModel)
		        .withProperty(ReceiverConstants.EX_PROP_MODEL_CLASS, conflict.getModelClassName())
		        .withProperty(ReceiverConstants.EX_PROP_ENTITY_ID, conflict.getIdentifier()).build();
		
		CamelUtils.send(ReceiverConstants.URI_INBOUND_DB_SYNC, exchange);
		
		//TODO What should we do in case a new conflict or error is encountered
		if (exchange.getProperty(EX_PROP_MSG_PROCESSED, false, Boolean.class)) {
			cacheEvictProcessor.process(conflict);
			indexUpdateProcessor.process(conflict);
			moveToArchiveQueue(conflict);
		} else {
			throw new EIPException("Something went wrong while syncing item with uuid: " + conflict.getMessageUuid());
		}
	}
	
}
