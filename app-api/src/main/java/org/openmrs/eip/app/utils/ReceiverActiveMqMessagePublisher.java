package org.openmrs.eip.app.utils;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.ReceiverSyncRequest;
import org.openmrs.eip.app.management.entity.SenderSyncResponseModel;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.RECEIVER)
public class ReceiverActiveMqMessagePublisher {
	
	private static final Logger log = LoggerFactory.getLogger(ReceiverActiveMqMessagePublisher.class);
	
	@Value("${" + Constants.PROP_CAMEL_OUTPUT_ENDPOINT + "}")
	private String endpointConfig;
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	/**
	 * Sends a <code>SenderSyncResponseModel</code> back to the Sender.
	 * 
	 * @param message The <code>SenderSyncMessage</code> to acknowledge.
	 */
	public void sendSyncResponse(SyncMessage message) {
		sendSyncResponse(message, message.getSite().getIdentifier(), message.getMessageUuid());
	}
	
	/**
	 * Sends a <code>SenderSyncResponseModel</code> back to the Sender.
	 * 
	 * @param syncRequest The <code>ReceiverSyncRequest</code> to acknowledge.
	 * @param messageUuid The Message UUID
	 */
	public void sendSyncResponse(ReceiverSyncRequest syncRequest, String messageUuid) {
		sendSyncResponse(syncRequest, syncRequest.getSite().getIdentifier(), messageUuid);
	}
	
	private <E extends AbstractEntity> void sendSyncResponse(E sourceEntity, String siteIdentifier, String messageUuid) {
		if (StringUtils.isBlank(messageUuid)) {
			if (log.isDebugEnabled()) {
				log.debug("No correspondent messageUuid was found. Skipping sending sync response for: {}", sourceEntity);
			}
			return;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Preparing sync response for: {}", sourceEntity);
		}
		
		SenderSyncResponseModel syncResponse = new SenderSyncResponseModel();
		syncResponse.setDateSent(LocalDateTime.now());
		syncResponse.setMessageUuid(messageUuid);
		
		String endpoint = getCamelOutputEndpoint(siteIdentifier);
		String payload = JsonUtils.marshall(syncResponse);
		
		log.info("Sending Sync Response. Payload: {}; Endpoint: {}", payload, endpoint);
		
		producerTemplate.sendBody(endpoint, payload);
		
		if (log.isDebugEnabled()) {
			log.debug("Sync Response sent.");
		}
	}
	
	/**
	 * Builds the endpoint based on <code>Constants.PROP_CAMEL_OUTPUT_ENDPOINT</code>
	 * 
	 * @param siteIdentifier The sender site identifier
	 * @return The camel output endpoint
	 */
	public String getCamelOutputEndpoint(String siteIdentifier) {
		return MessageFormat.format(endpointConfig, siteIdentifier);
	}
	
}
