package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.component.service.light.AbstractLightService.DEFAULT_VOID_REASON;

import java.time.LocalDateTime;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.camel.AbstractOpenmrsProducer;
import org.openmrs.eip.component.entity.light.LightEntity;
import org.openmrs.eip.component.entity.light.PersonAttributeTypeLight;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.model.BaseDataModel;
import org.openmrs.eip.component.model.BaseMetadataModel;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component("entityLoader")
@Profile(SyncProfiles.RECEIVER)
public class EntityLoader {
	
	private static final Logger log = LoggerFactory.getLogger(EntityLoader.class);
	
	private EntityServiceFacade serviceFacade;
	
	public EntityLoader(EntityServiceFacade serviceFacade) {
		this.serviceFacade = serviceFacade;
	}
	
	/**
	 * Loads the entity data
	 */
	public void process(SyncModel syncModel) {
		TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnum(syncModel.getTableToSyncModelClass());
		boolean delete = "d".equals(syncModel.getMetadata().getOperation());
		Class<? extends BaseHashEntity> hashClass = TableToSyncEnum.getHashClass(syncModel.getModel());
		ProducerTemplate producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		BaseModel dbModel = serviceFacade.getModel(tableToSyncEnum, syncModel.getModel().getUuid());
		BaseHashEntity storedHash = null;
		if (dbModel != null || delete) {
			storedHash = HashUtils.getStoredHash(syncModel.getModel().getUuid(), hashClass, producerTemplate);
		}
		
		if (delete) {
			delete(syncModel, storedHash, hashClass, serviceFacade, dbModel, tableToSyncEnum, producerTemplate);
		} else {
			save(dbModel, storedHash, hashClass, serviceFacade, syncModel, tableToSyncEnum, producerTemplate);
		}
	}
	
	private void save(BaseModel dbModel, BaseHashEntity storedHash, Class<? extends BaseHashEntity> hashClass,
	                  EntityServiceFacade serviceFacade, SyncModel syncModel, TableToSyncEnum tableToSyncEnum,
	                  ProducerTemplate producerTemplate) {
		BaseModel modelToSave = syncModel.getModel();
		if (modelToSave instanceof PersonAttributeModel) {
			PersonAttributeModel model = (PersonAttributeModel) syncModel.getModel();
			PersonAttributeTypeLight type = AbstractOpenmrsProducer.getLightEntity(model.getPersonAttributeTypeUuid());
			if (type.getFormat() != null && type.getFormat().startsWith(Constants.OPENMRS_ROOT_PGK)) {
				if (log.isDebugEnabled()) {
					log.debug("Converting uuid " + model.getValue() + " for " + type.getFormat() + " to id");
				}
				
				model.setValue(getId(type.getFormat(), model.getValue()).toString());
			}
		}
		
		if (dbModel == null) {
			insert(modelToSave, hashClass, serviceFacade, tableToSyncEnum, producerTemplate);
		} else {
			update(modelToSave, storedHash, hashClass, serviceFacade, tableToSyncEnum, producerTemplate, dbModel);
		}
	}
	
	private void delete(SyncModel syncModel, BaseHashEntity storedHash, Class<? extends BaseHashEntity> hashClass,
	                    EntityServiceFacade serviceFacade, BaseModel dbModel, TableToSyncEnum tableToSyncEnum,
	                    ProducerTemplate producerTemplate) {
		
		boolean isNewHash = false;
		if (dbModel != null) {
			if (storedHash == null) {
				isNewHash = true;
				
				log.info("Inserting new hash for the deleted entity with no existing hash");
				
				try {
					storedHash = HashUtils.instantiateHashEntity(hashClass);
				}
				catch (Exception e) {
					throw new EIPException("Failed to create an instance of " + hashClass, e);
				}
				
				storedHash.setIdentifier(syncModel.getModel().getUuid());
				storedHash.setDateCreated(LocalDateTime.now());
			}
		}
		
		serviceFacade.delete(tableToSyncEnum, syncModel.getModel().getUuid());
		
		if (dbModel != null || storedHash != null) {
			if (dbModel == null) {
				//This will typically happen if we deleted the entity but something went wrong before or during
				//update of the hash and the event comes back as a retry item
				log.info("Found existing hash for a missing entity, this could be a retry item to delete an entity "
				        + "but the hash was never updated to the terminal value");
			}
			
			storedHash.setHash(Constants.HASH_DELETED);
			if (!isNewHash) {
				storedHash.setDateChanged(LocalDateTime.now());
			}
			
			if (log.isDebugEnabled()) {
				if (isNewHash) {
					log.debug("Saving new hash for the deleted entity");
				} else {
					log.debug("Updating hash for the deleted entity");
				}
			}
			
			HashUtils.saveHash(storedHash, producerTemplate, false);
			
			if (log.isDebugEnabled()) {
				if (isNewHash) {
					log.debug("Successfully saved the new hash for the deleted entity");
				} else {
					log.debug("Successfully updated the hash for the deleted entity");
				}
			}
		}
	}
	
	private void insert(BaseModel modelToSave, Class<? extends BaseHashEntity> hashClass, EntityServiceFacade serviceFacade,
	                    TableToSyncEnum tableToSyncEnum, ProducerTemplate producerTemplate) {
		
		if (log.isDebugEnabled()) {
			log.debug("Inserting new hash for the incoming entity state");
		}
		
		BaseHashEntity storedHash;
		try {
			storedHash = HashUtils.instantiateHashEntity(hashClass);
		}
		catch (Exception e) {
			throw new EIPException("Failed to create an instance of " + hashClass, e);
		}
		
		storedHash.setIdentifier(modelToSave.getUuid());
		storedHash.setDateCreated(LocalDateTime.now());
		
		if (log.isDebugEnabled()) {
			log.debug("Saving hash for the incoming entity state");
		}
		
		storedHash.setHash(HashUtils.computeHash(modelToSave));
		HashUtils.saveHash(storedHash, producerTemplate, true);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved the hash for the incoming entity state");
		}
		
		serviceFacade.saveModel(tableToSyncEnum, modelToSave);
	}
	
	private void update(BaseModel modelToSave, BaseHashEntity storedHash, Class<? extends BaseHashEntity> hashClass,
	                    EntityServiceFacade serviceFacade, TableToSyncEnum tableToSyncEnum,
	                    ProducerTemplate producerTemplate, BaseModel dbModel) {
		
		boolean isEtyInDbPlaceHolder = false;
		if (dbModel instanceof BaseDataModel) {
			BaseDataModel dataModel = (BaseDataModel) dbModel;
			isEtyInDbPlaceHolder = dataModel.isVoided() && DEFAULT_VOID_REASON.equals(dataModel.getVoidReason());
		} else if (dbModel instanceof BaseMetadataModel) {
			BaseMetadataModel metadataModel = (BaseMetadataModel) dbModel;
			isEtyInDbPlaceHolder = metadataModel.isRetired() && DEFAULT_VOID_REASON.equals(metadataModel.getRetireReason());
		}
		
		boolean isNewHashInstance = false;
		boolean isExistingEntityWithNoHash = false;
		if (storedHash == null) {
			isExistingEntityWithNoHash = !isEtyInDbPlaceHolder;
			if (!isEtyInDbPlaceHolder) {
				String ignore = SyncContext.getBean(Environment.class).getProperty(Constants.PROP_IGNORE_MISSING_HASH);
				if (!"true".equals(ignore)) {
					//TODO Don't fail if hashes of the db and incoming payloads match
					throw new EIPException("Failed to find the existing hash for an existing entity");
				}
			}
			
			if (log.isDebugEnabled()) {
				if (isEtyInDbPlaceHolder) {
					log.debug("Inserting new hash for existing placeholder entity");
				} else {
					log.debug("Inserting new hash for existing entity with missing hash");
				}
			}
			
			try {
				storedHash = HashUtils.instantiateHashEntity(hashClass);
				isNewHashInstance = true;
			}
			catch (Exception e) {
				throw new EIPException("Failed to create an instance of " + hashClass, e);
			}
			
			storedHash.setIdentifier(modelToSave.getUuid());
			storedHash.setDateCreated(LocalDateTime.now());
		}
		
		String newHash = HashUtils.computeHash(modelToSave);
		if (!isExistingEntityWithNoHash && !isEtyInDbPlaceHolder) {
			String dbEntityHash = HashUtils.computeHash(dbModel);
			if (!dbEntityHash.equals(storedHash.getHash())) {
				if (dbEntityHash.equals(newHash)) {
					//This will typically happen if we update the entity but something goes wrong before or during
					//update of the hash and the event comes back as a retry item
					log.info("Stored hash differs from that of the state in the DB, ignoring this because the incoming "
					        + "and DB states match");
				} else {
					throw new ConflictsFoundException();
				}
			}
		} else {
			if (log.isDebugEnabled()) {
				if (isEtyInDbPlaceHolder) {
					log.debug("Ignoring placeholder entity when checking for conflicts");
				} else if (isExistingEntityWithNoHash) {
					log.debug("Ignoring existing entity with missing hash when checking for conflicts");
				}
			}
		}
		
		serviceFacade.saveModel(tableToSyncEnum, modelToSave);
		
		storedHash.setHash(newHash);
		if (!isNewHashInstance) {
			storedHash.setDateChanged(LocalDateTime.now());
		}
		
		if (log.isDebugEnabled()) {
			if (isNewHashInstance) {
				log.debug("Saving new hash for the entity");
			} else {
				log.debug("Updating hash for the incoming entity state");
			}
		}
		
		HashUtils.saveHash(storedHash, producerTemplate, false);
		
		if (log.isDebugEnabled()) {
			if (isNewHashInstance) {
				log.debug("Successfully saved new hash for the entity");
			} else {
				log.debug("Successfully updated the hash for the incoming entity state");
			}
		}
	}
	
	/**
	 * Gets the id of the entity matching the specified classname and uuid
	 *
	 * @param openmrsClassName the fully qualified OpenMRS java class name to match
	 * @param uuid the uuid of the entity
	 * @return the id of the entity
	 */
	private Long getId(String openmrsClassName, String uuid) {
		LightEntity entity = AbstractOpenmrsProducer.getEntityLightRepository(openmrsClassName).findByUuid(uuid);
		if (entity == null) {
			throw new EIPException("No entity of type " + openmrsClassName + " found with uuid " + uuid);
		}
		
		return entity.getId();
	}
	
}
