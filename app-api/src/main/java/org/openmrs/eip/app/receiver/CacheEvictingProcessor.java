package org.openmrs.eip.app.receiver;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.UserModel;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Processes synced messages that require eviction from the OpenMRS cache.
 */
@Component("cacheEvictingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class CacheEvictingProcessor extends BaseSendToCamelPostSyncActionProcessor {
	
	protected static final Logger log = LoggerFactory.getLogger(CacheEvictingProcessor.class);
	
	public CacheEvictingProcessor(ProducerTemplate producerTemplate, SyncedMessageRepository repo) {
		super(ReceiverConstants.URI_CLEAR_CACHE, producerTemplate, repo);
	}
	
	@Override
	public String getProcessorName() {
		return "cache evict";
	}
	
	@Override
	public String getUniqueId(SyncedMessage item) {
		return item.getIdentifier();
	}
	
	@Override
	public String getQueueName() {
		return "cache-evict";
	}
	
	@Override
	public String getThreadName(SyncedMessage item) {
		return item.getSite().getIdentifier() + "-" + item.getMessageUuid() + "-"
		        + AppUtils.getSimpleName(item.getModelClassName()) + "-" + item.getIdentifier() + "-" + item.getId();
	}
	
	@Override
	public String getLogicalType(SyncedMessage item) {
		return item.getModelClassName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfModelClassHierarchy(logicalType);
	}
	
	@Override
	public Object convertBody(SyncedMessage item) {
		String modelClass = item.getModelClassName();
		String uuid = null;
		//Users are not deleted, so no need to clear the cache for all users as we do for other cached entities
		if (SyncOperation.d != item.getOperation() || UserModel.class.getName().equals(modelClass)) {
			uuid = item.getIdentifier();
		}
		
		String resource;
		String subResource = null;
		if (PersonNameModel.class.getName().equals(modelClass)) {
			resource = "person";
			subResource = "name";
		} else if (PersonAttributeModel.class.getName().equals(modelClass)) {
			resource = "person";
			subResource = "attribute";
		} else if (PersonModel.class.getName().equals(modelClass) || PatientModel.class.getName().equals(modelClass)) {
			resource = "person";
		} else if (PersonAddressModel.class.getName().equals(modelClass)) {
			resource = "person";
			subResource = "address";
		} else if (UserModel.class.getName().equals(modelClass)) {
			//TODO Remove this clause when user and provider sync is stopped
			resource = "user";
		} else {
			throw new EIPException("Don't know how to handle cache eviction for entity of type: " + modelClass);
		}
		
		try {
			return ReceiverConstants.MAPPER.writeValueAsString(new OpenmrsPayload(resource, subResource, uuid));
		}
		catch (JsonProcessingException e) {
			throw new EIPException("Failed to generate cache evict payload", e);
		}
	}
	
	@Override
	public void onSuccess(SyncedMessage item) {
		item.setEvictedFromCache(true);
		repo.save(item);
	}
	
}
