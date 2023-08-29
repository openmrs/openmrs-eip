package org.openmrs.eip.app.management.service.impl;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.setProperty;
import static org.openmrs.eip.app.SyncConstants.CHAINED_TX_MGR;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.FIELD_VOIDED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.MERGE_EXCLUDE_FIELDS;
import static org.openmrs.eip.component.utils.DateUtils.isDateAfterOrEqual;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.commons.lang3.StringUtils;
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
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.BaseChangeableDataModel;
import org.openmrs.eip.component.model.BaseChangeableMetadataModel;
import org.openmrs.eip.component.model.BaseDataModel;
import org.openmrs.eip.component.model.BaseMetadataModel;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("conflictService")
@Profile(SyncProfiles.RECEIVER)
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
	@Transactional(transactionManager = MGT_TX_MGR)
	public void resolve(ConflictResolution resolution) {
		if (resolution.getConflict() == null) {
			throw new EIPException("Conflict is required");
		} else if (resolution.getDecision() == null) {
			throw new EIPException("Resolution is required");
		}
		
		ConflictQueueItem c = resolution.getConflict();
		log.info("Resolving conflict for item with uuid: " + c.getMessageUuid() + " with decision as: "
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
				resolveWithMerge(resolution);
				break;
		}
	}
	
	@Override
	@Transactional(transactionManager = CHAINED_TX_MGR)
	public void resolveWithMerge(ConflictQueueItem conflict, Set<String> propertiesToSync) {
		if (propertiesToSync.isEmpty()) {
			throw new EIPException("No properties to sync specified for merge resolution decision");
		} else if (!Collections.disjoint(propertiesToSync, MERGE_EXCLUDE_FIELDS)) {
			throw new EIPException(
			        "Found invalid properties for a merge conflict resolution, please exclude: " + MERGE_EXCLUDE_FIELDS);
		}
		
		receiverService.updateHash(conflict.getModelClassName(), conflict.getIdentifier());
		SyncModel syncModel = JsonUtils.unmarshalSyncModel(conflict.getEntityPayload());
		BaseModel newModel = syncModel.getModel();
		TableToSyncEnum syncEnum = TableToSyncEnum.getTableToSyncEnumByModelClassName(conflict.getModelClassName());
		BaseModel dbModel = serviceFacade.getModel(syncEnum, conflict.getIdentifier());
		for (String propertyName : propertiesToSync) {
			try {
				setProperty(dbModel, propertyName, getProperty(newModel, propertyName));
			}
			catch (Exception e) {
				throw new EIPException("Failed to set the value for property: " + propertyName, e);
			}
		}
		
		mergeVoidOrRetireProperties(dbModel, newModel, propertiesToSync);
		mergeAuditProperties(dbModel, newModel);
		
		syncModel.setModel(dbModel);
		Exchange exchange = ExchangeBuilder.anExchange(camelContext).withBody(syncModel)
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
	
	/**
	 * Merges void or retire fields
	 * 
	 * @param dbModel the database model
	 * @param newModel the new model
	 * @param propsToSync set of properties to sync from the new model
	 */
	protected void mergeVoidOrRetireProperties(BaseModel dbModel, BaseModel newModel, Set<String> propsToSync) {
		if (propsToSync.contains(FIELD_VOIDED) || propsToSync.contains("retired")) {
			if (newModel instanceof BaseDataModel) {
				BaseDataModel dataDbModel = (BaseDataModel) dbModel;
				BaseDataModel dataNewModel = (BaseDataModel) newModel;
				if (dataNewModel.isVoided()) {
					//Since we're bringing in data from remote, if dateVoided matches, remote info is latest
					if (isDateAfterOrEqual(dataNewModel.getDateVoided(), dataDbModel.getDateVoided())) {
						//No need to check for null because at this point the new dateVoided can't be null 
						//otherwise both dates are null
						dataDbModel.setDateVoided(dataNewModel.getDateVoided());
						
						//Do not wipe out any data
						if (StringUtils.isNotBlank(dataNewModel.getVoidedByUuid())) {
							dataDbModel.setVoidedByUuid(dataNewModel.getVoidedByUuid());
						}
						
						if (StringUtils.isNotBlank(dataNewModel.getVoidReason())) {
							dataDbModel.setVoidReason(dataNewModel.getVoidReason());
						}
					}
				} else {
					dataDbModel.setVoidedByUuid(null);
					dataDbModel.setDateVoided(null);
					dataDbModel.setVoidReason(null);
				}
			} else if (newModel instanceof BaseMetadataModel) {
				BaseMetadataModel dataDbModel = (BaseMetadataModel) dbModel;
				BaseMetadataModel dataNewModel = (BaseMetadataModel) newModel;
				if (dataNewModel.isRetired()) {
					//Since we're bringing in data from remote, if dateRetired matches, remote info is latest 
					if (isDateAfterOrEqual(dataNewModel.getDateRetired(), dataDbModel.getDateRetired())) {
						//No need to check for null because at this point the new dateRetired can't be null 
						//otherwise both dates are null
						dataDbModel.setDateRetired(dataNewModel.getDateRetired());
						
						if (StringUtils.isNotBlank(dataNewModel.getRetiredByUuid())) {
							dataDbModel.setRetiredByUuid(dataNewModel.getRetiredByUuid());
						}
						
						if (StringUtils.isNotBlank(dataNewModel.getRetireReason())) {
							dataDbModel.setRetireReason(dataNewModel.getRetireReason());
						}
					}
				} else {
					dataDbModel.setRetiredByUuid(null);
					dataDbModel.setDateRetired(null);
					dataDbModel.setRetireReason(null);
				}
			}
		}
	}
	
	/**
	 * Merges the audit fields i.e. changedByUuid and dateChanged properties based on the state that has
	 * the latest date changed value.
	 * 
	 * @param dbModel the database model
	 * @param newModel the new model
	 */
	protected void mergeAuditProperties(BaseModel dbModel, BaseModel newModel) {
		if (newModel instanceof BaseChangeableDataModel || newModel instanceof BaseChangeableMetadataModel) {
			if (newModel instanceof BaseChangeableDataModel) {
				BaseChangeableDataModel dataDbModel = (BaseChangeableDataModel) dbModel;
				BaseChangeableDataModel dataNewModel = (BaseChangeableDataModel) newModel;
				//Since we're bringing in details from remote, if dateChanged matches, remote changedBy is latest 
				if (isDateAfterOrEqual(dataNewModel.getDateChanged(), dataDbModel.getDateChanged())) {
					//No need to check for null because at this point the new dateChanged can't be null
					//otherwise both dates are null
					dataDbModel.setDateChanged(dataNewModel.getDateChanged());
					
					if (StringUtils.isNotBlank(dataNewModel.getChangedByUuid())) {
						dataDbModel.setChangedByUuid(dataNewModel.getChangedByUuid());
					}
				}
			} else {
				BaseChangeableMetadataModel dataDbModel = (BaseChangeableMetadataModel) dbModel;
				BaseChangeableMetadataModel dataNewModel = (BaseChangeableMetadataModel) newModel;
				//Since we're bringing in details from remote, if dateChanged matches, remote changedBy is latest 
				if (isDateAfterOrEqual(dataNewModel.getDateChanged(), dataDbModel.getDateChanged())) {
					//No need to check for null because at this point the new dateChanged can't be null
					//otherwise both dates are null
					dataDbModel.setDateChanged(dataNewModel.getDateChanged());
					
					if (StringUtils.isNotBlank(dataNewModel.getChangedByUuid())) {
						dataDbModel.setChangedByUuid(dataNewModel.getChangedByUuid());
					}
				}
			}
		}
	}
	
	private void resolveWithNewState(ConflictResolution resolution) {
		ConflictQueueItem conflict = resolution.getConflict();
		receiverService.updateHash(conflict.getModelClassName(), conflict.getIdentifier());
		
		moveToRetryQueue(conflict, "Moved from conflict queue after conflict resolution");
	}
	
	private void resolveWithMerge(ConflictResolution r) {
		//We need to call the method on a proxy for the transaction AOP to work
		SyncContext.getBean(ConflictService.class).resolveWithMerge(r.getConflict(), r.getPropertiesToSync());
	}
	
}
