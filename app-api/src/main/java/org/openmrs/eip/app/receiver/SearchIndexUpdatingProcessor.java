package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.component.Constants.OPENMRS_DATASOURCE_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Reads and processes post sync action items that require updating the OpenMRS search index.
 */
@Component("searchIndexUpdatingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SearchIndexUpdatingProcessor extends BaseSendToCamelPostSyncActionProcessor {
	
	protected static final Logger log = LoggerFactory.getLogger(SearchIndexUpdatingProcessor.class);
	
	public SearchIndexUpdatingProcessor(ProducerTemplate producerTemplate, PostSyncActionRepository repo) {
		super(ReceiverConstants.URI_UPDATE_SEARCH_INDEX, producerTemplate, repo);
	}
	
	@Override
	public String getProcessorName() {
		return "search index update";
	}
	
	@Override
	public String getUniqueId(PostSyncAction item) {
		return item.getMessage().getIdentifier();
	}
	
	@Override
	public String getQueueName() {
		return "search-index-update";
	}
	
	@Override
	public String getThreadName(PostSyncAction item) {
		SyncedMessage m = item.getMessage();
		return m.getSite().getIdentifier() + "-" + m.getMessageUuid() + "-" + AppUtils.getSimpleName(m.getModelClassName())
		        + "-" + m.getIdentifier() + "-" + item.getId();
	}
	
	@Override
	public String getLogicalType(PostSyncAction item) {
		return item.getMessage().getModelClassName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfModelClassHierarchy(logicalType);
	}
	
	@Override
	public void processItem(PostSyncAction item) {
		//Postpone processing until the cache eviction action has been completed if required.
		if (item.getMessage().requiresCacheEviction()) {
			if (log.isDebugEnabled()) {
				log.debug("Waiting for cache eviction action to complete before updating search index for -> "
				        + item.getMessage());
			}
			
			return;
		}
		
		super.processItem(item);
	}
	
	@Override
	public Object convertBody(PostSyncAction item) {
		SyncedMessage msg = item.getMessage();
		String modelClass = msg.getModelClassName();
		String uuid = null;
		if (SyncOperation.d != msg.getOperation() || PersonModel.class.getName().equals(modelClass)
		        || PatientModel.class.getName().equals(modelClass)) {
			
			uuid = msg.getIdentifier();
		}
		
		Object payload;
		if (PersonNameModel.class.getName().equals(modelClass)) {
			payload = new OpenmrsPayload("person", "name", uuid);
		} else if (PatientIdentifierModel.class.getName().equals(modelClass)) {
			payload = new OpenmrsPayload("patient", "identifier", uuid);
		} else if (PersonAttributeModel.class.getName().equals(modelClass)) {
			payload = new OpenmrsPayload("person", "attribute", uuid);
		} else if (PersonModel.class.getName().equals(modelClass) || PatientModel.class.getName().equals(modelClass)) {
			List<String> nameUuids = getPersonNameUuids(uuid);
			List<String> idUuids = getPatientIdentifierUuids(uuid);
			List<OpenmrsPayload> payloadList = new ArrayList(nameUuids.size() + idUuids.size());
			nameUuids.forEach(nameUuid -> payloadList.add(new OpenmrsPayload("person", "name", nameUuid)));
			idUuids.forEach(idUuid -> payloadList.add(new OpenmrsPayload("patient", "identifier", idUuid)));
			payload = payloadList;
		} else {
			throw new EIPException("Don't know how to handle search index update for entity of type: " + modelClass);
		}
		
		try {
			if (!Collection.class.isAssignableFrom(payload.getClass())) {
				return ReceiverConstants.MAPPER.writeValueAsString(payload);
			}
			
			Collection payLoadColl = (Collection) payload;
			List<String> payloads = new ArrayList(payLoadColl.size());
			for (Object pl : payLoadColl) {
				payloads.add(ReceiverConstants.MAPPER.writeValueAsString(pl));
			}
			
			return payloads;
		}
		catch (JsonProcessingException e) {
			throw new EIPException("Failed to generate search index update payload", e);
		}
	}
	
	protected List<String> getPersonNameUuids(String personUuid) {
		String q = "SELECT n.uuid FROM person p, person_name n WHERE p.person_id = n.person_id AND p.uuid = '" + personUuid
		        + "'";
		
		return executeQuery(q);
	}
	
	protected List<String> getPatientIdentifierUuids(String patientUuid) {
		String q = "SELECT i.uuid FROM person p, patient_identifier i WHERE p.person_id = i.patient_id AND " + "p.uuid = '"
		        + patientUuid + "'";
		
		return executeQuery(q);
	}
	
	private List<String> executeQuery(String query) {
		Exchange exchange = ExchangeBuilder.anExchange(producerTemplate.getCamelContext()).build();
		CamelUtils.send("sql:" + query + "?dataSource=" + OPENMRS_DATASOURCE_NAME, exchange);
		List<Map<String, String>> rows = exchange.getMessage().getBody(List.class);
		List<String> uuids = new ArrayList(rows.size());
		rows.forEach(r -> uuids.add(r.get("uuid")));
		
		return uuids;
	}
	
}
