package org.openmrs.eip.component.camel;

import static org.openmrs.eip.component.Constants.DAEMON_USER_UUID;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_CLASS;
import static org.openmrs.eip.component.Constants.QUERY_SAVE_HASH;
import static org.openmrs.eip.component.Constants.VALUE_SITE_SEPARATOR;
import static org.openmrs.eip.component.service.light.AbstractLightService.DEFAULT_VOID_REASON;
import static org.springframework.data.domain.ExampleMatcher.matching;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.User;
import org.openmrs.eip.component.entity.light.LightEntity;
import org.openmrs.eip.component.entity.light.PersonAttributeTypeLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.model.BaseDataModel;
import org.openmrs.eip.component.model.BaseMetadataModel;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.ProviderModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.model.UserModel;
import org.openmrs.eip.component.repository.UserRepository;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;

public class OpenmrsLoadProducer extends AbstractOpenmrsProducer {
	
	private static final Logger log = LoggerFactory.getLogger(OpenmrsLoadProducer.class);
	
	public OpenmrsLoadProducer(final OpenmrsEndpoint endpoint, final ApplicationContext applicationContext,
	    final ProducerParams params) {
		super(endpoint, applicationContext, params);
	}
	
	@Override
	public void process(Exchange exchange) {
		doProcess(exchange);
	}
	
	/**
	 * Processes the sync message specified on the exchange, this method is marked as synchronized
	 * because we want to avoid the following from happening,
	 * 
	 * <pre>
	 * 1. Duplicating a record when the same new record is being synced by 2 different sites in parallel
	 * 2. Duplicating records because they are referenced by multiple entities being processed in parallel
	 * </pre>
	 */
	private synchronized static void doProcess(Exchange exchange) {
		EntityServiceFacade serviceFacade = (EntityServiceFacade) appContext.getBean("entityServiceFacade");
		SyncModel syncModel = exchange.getIn().getBody(SyncModel.class);
		TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnum(syncModel.getTableToSyncModelClass());
		
		boolean isUser = syncModel.getModel() instanceof UserModel;
		if (isUser && DAEMON_USER_UUID.equals(syncModel.getModel().getUuid())) {
			log.info("Skipping syncing of daemon user");
			return;
		}
		
		if (isUser && SyncContext.getAdminUser().getUuid().equals(syncModel.getModel().getUuid())) {
			log.info("Skipping syncing of a user with a uuid matching the local admin account");
			return;
		}
		
		boolean isProvider = syncModel.getModel() instanceof ProviderModel;
		boolean isDeleteOperation = "d".equals(syncModel.getMetadata().getOperation());
		boolean delete = isDeleteOperation && !isUser && !isProvider;
		Class<? extends BaseHashEntity> hashClass = TableToSyncEnum.getHashClass(syncModel.getModel());
		ProducerTemplate producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		BaseModel dbModel = serviceFacade.getModel(tableToSyncEnum, syncModel.getModel().getUuid());
		BaseHashEntity storedHash = HashUtils.getStoredHash(syncModel.getModel().getUuid(), hashClass, producerTemplate);
		//Delete any deleted entity type BUT for deleted users or providers we only proceed processing this as a delete
		//if they do not exist in the receiver to avoid creating them at all otherwise we retire the existing one.
		if (delete || (isDeleteOperation && (isUser || isProvider) && dbModel == null)) {
			delete(syncModel, storedHash, hashClass, serviceFacade, dbModel, tableToSyncEnum, producerTemplate);
		} else {
			save(dbModel, storedHash, hashClass, serviceFacade, syncModel, tableToSyncEnum, producerTemplate, isUser,
			    isDeleteOperation);
		}
	}
	
	private static void save(BaseModel dbModel, BaseHashEntity storedHash, Class<? extends BaseHashEntity> hashClass,
	                         EntityServiceFacade serviceFacade, SyncModel syncModel, TableToSyncEnum tableToSyncEnum,
	                         ProducerTemplate producerTemplate, boolean isUser, boolean isDeleteOperation) {
		
		BaseModel modelToSave = syncModel.getModel();
		String siteId = syncModel.getMetadata().getSourceIdentifier();
		if (!isDeleteOperation) {
			if (isUser) {
				UserModel userModel = (UserModel) modelToSave;
				UserRepository userRepo = SyncContext.getBean(UserRepository.class);
				if (userModel.getUsername() != null) {
					User exampleUser = new User();
					exampleUser.setUsername(userModel.getUsername());
					Example<User> example = Example.of(exampleUser, matching().withIgnoreCase());
					List<User> duplicates = userRepo.findAll(example);
					if (duplicates.size() > 0) {
						boolean duplicateUsername = false;
						if (duplicates.size() > 1) {
							duplicateUsername = true;
						} else if (!userModel.getUuid().equalsIgnoreCase(duplicates.get(0).getUuid())) {
							duplicateUsername = true;
						}
						
						if (duplicateUsername) {
							log.info(
							    "Found another user in the receiver DB with a duplicate username: " + userModel.getUsername()
							            + ", appending " + "site id to this user's username to make it unique");
							userModel.setUsername(userModel.getUsername() + VALUE_SITE_SEPARATOR + siteId);
						}
					}
				}
				
				if (userModel.getSystemId() != null) {
					User exampleUser = new User();
					exampleUser.setSystemId(userModel.getSystemId());
					Example<User> example = Example.of(exampleUser, matching().withIgnoreCase());
					List<User> duplicates = userRepo.findAll(example);
					if (duplicates.size() > 0) {
						boolean duplicateSystemId = false;
						if (duplicates.size() > 1) {
							duplicateSystemId = true;
						} else if (!userModel.getUuid().equalsIgnoreCase(duplicates.get(0).getUuid())) {
							duplicateSystemId = true;
						}
						
						if (duplicateSystemId) {
							log.info(
							    "Found another user in the receiver DB with a duplicate systemId: " + userModel.getSystemId()
							            + ", appending site id " + "to this user's systemId to make it unique");
							userModel.setSystemId(userModel.getSystemId() + VALUE_SITE_SEPARATOR + siteId);
						}
					}
				}
			} else if (modelToSave instanceof PersonAttributeModel) {
				PersonAttributeModel model = (PersonAttributeModel) syncModel.getModel();
				PersonAttributeTypeLight type = getLightEntity(model.getPersonAttributeTypeUuid());
				if (type.getFormat() != null && type.getFormat().startsWith(OPENMRS_ROOT_PGK)) {
					if (log.isDebugEnabled()) {
						log.debug("Converting uuid " + model.getValue() + " for " + type.getFormat() + " to id");
					}
					
					model.setValue(getId(type.getFormat(), model.getValue()).toString());
				}
			}
		} else {
			//This is a user or provider entity that was deleted
			log.info("Entity was deleted in remote site, marking it as retired");
			BaseMetadataModel existing = serviceFacade.getModel(tableToSyncEnum, syncModel.getModel().getUuid());
			existing.setRetired(true);
			existing.setRetiredByUuid(UserLight.class.getName() + "(" + SyncContext.getAppUser().getUuid() + ")");
			existing.setDateRetired(LocalDateTime.now());
			existing.setRetireReason(Constants.DEFAULT_RETIRE_REASON);
			modelToSave = existing;
		}
		
		if (dbModel == null) {
			insert(modelToSave, storedHash, hashClass, serviceFacade, tableToSyncEnum, producerTemplate);
		} else {
			update(modelToSave, storedHash, hashClass, serviceFacade, tableToSyncEnum, producerTemplate, dbModel);
		}
	}
	
	private static void delete(SyncModel syncModel, BaseHashEntity storedHash, Class<? extends BaseHashEntity> hashClass,
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
			
			producerTemplate.sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, hashClass.getSimpleName()), storedHash);
			
			if (log.isDebugEnabled()) {
				if (isNewHash) {
					log.debug("Successfully saved the new hash for the deleted entity");
				} else {
					log.debug("Successfully updated the hash for the deleted entity");
				}
			}
		}
	}
	
	private static void insert(BaseModel modelToSave, BaseHashEntity storedHash, Class<? extends BaseHashEntity> hashClass,
	                           EntityServiceFacade serviceFacade, TableToSyncEnum tableToSyncEnum,
	                           ProducerTemplate producerTemplate) {
		
		if (storedHash == null) {
			if (log.isDebugEnabled()) {
				log.debug("Inserting new hash for the incoming entity state");
			}
			
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
		} else {
			//This will typically happen if we inserted the hash but something went wrong before or during
			//insert of the entity and the event comes back as a retry item
			log.info("Found existing hash for a new entity, this could be a retry item to insert a new entity "
			        + "where the hash was created but the insert previously failed or a previously deleted entity at another site");
			storedHash.setDateChanged(LocalDateTime.now());
			
			if (log.isDebugEnabled()) {
				log.debug("Updating hash for the incoming entity state");
			}
		}
		
		storedHash.setHash(HashUtils.computeHash(modelToSave));
		
		producerTemplate.sendBody(Constants.QUERY_SAVE_HASH.replace(Constants.PLACEHOLDER_CLASS, hashClass.getSimpleName()),
		    storedHash);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully saved the hash for the incoming entity state");
		}
		
		serviceFacade.saveModel(tableToSyncEnum, modelToSave);
	}
	
	private static void update(BaseModel modelToSave, BaseHashEntity storedHash, Class<? extends BaseHashEntity> hashClass,
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
		
		producerTemplate.sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, hashClass.getSimpleName()), storedHash);
		
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
	private static Long getId(String openmrsClassName, String uuid) {
		LightEntity entity = getEntityLightRepository(openmrsClassName).findByUuid(uuid);
		if (entity == null) {
			throw new EIPException("No entity of type " + openmrsClassName + " found with uuid " + uuid);
		}
		
		return entity.getId();
	}
	
}
