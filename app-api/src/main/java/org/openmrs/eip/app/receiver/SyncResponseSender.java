package org.openmrs.eip.app.receiver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncResponseModel;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.DateUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Reads a batch of post sync items of type PostSyncActionType.SEND_RESPONSE in the synced queue
 * that are not yet successful and sends sync responses for their associated sync messages.
 */
public class SyncResponseSender extends BaseSiteRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncResponseSender.class);
	
	private ConnectionFactory activeMqConnFactory;
	
	private PostSyncActionRepository repo;
	
	private String queueName;
	
	private Pageable page;
	
	public SyncResponseSender(SiteInfo site) {
		super(site);
		activeMqConnFactory = SyncContext.getBean(ConnectionFactory.class);
		repo = SyncContext.getBean(PostSyncActionRepository.class);
		//TODO Configure batch size
		page = PageRequest.of(0, 100000);
		String endpoint = SyncContext.getBean(ReceiverActiveMqMessagePublisher.class)
		        .getCamelOutputEndpoint(getSite().getIdentifier());
		if (!endpoint.startsWith("activemq:")) {
			throw new EIPException(endpoint + " is an invalid message broker endpoint value for outbound messages");
		}
		
		queueName = endpoint.substring(endpoint.indexOf(":") + 1);
	}
	
	@Override
	public String getProcessorName() {
		return "Synced message sender";
	}
	
	@Override
	public boolean doRun() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("Fetching next batch of sync response actions for site: " + getSite());
		}
		
		List<PostSyncAction> respActions = repo.getBatchOfPendingResponseActions(getSite(), page);
		
		if (respActions.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("No sync response actions found for site: " + getSite());
			}
			
			return true;
		}
		
		try {
			sendResponsesInBatch(respActions);
			ReceiverUtils.updatePostSyncActionStatuses(respActions, true, null);
		}
		catch (Throwable t) {
			log.warn("An error was encountered while sending sync responses for site: " + getSite(), t);
			Throwable rootCause = ExceptionUtils.getRootCause(t);
			if (rootCause != null) {
				t = rootCause;
			}
			
			String errorMsg = t.toString().trim();
			if (errorMsg.length() > 1024) {
				errorMsg = errorMsg.substring(0, 1024).trim();
			}
			
			ReceiverUtils.updatePostSyncActionStatuses(respActions, false, errorMsg);
		}
		
		return false;
	}
	
	/**
	 * Sends sync responses to a single remote site's response queue in the message broker for all the
	 * specified post sync actions in a batch
	 * 
	 * @param actions post sync actions to process
	 * @throws JMSException
	 */
	protected void sendResponsesInBatch(List<PostSyncAction> actions) throws JMSException {
		log.info("Sending " + actions.size() + " sync response(s) for site: " + getSite());
		
		List<String> responses = generateResponses(actions);
		if (responses.isEmpty()) {
			log.info("No sync responses to send");
			
			return;
		}
		
		try (Connection conn = activeMqConnFactory.createConnection();
		        Session session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE)) {
			
			Queue queue = session.createQueue(queueName);
			try (MessageProducer p = session.createProducer(queue)) {
				for (String responsePayload : responses) {
					p.send(session.createTextMessage(responsePayload));
				}
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Sending a batch of " + responses.size() + " response(s) to site: " + getSite());
			}
			
			session.commit();
			
			if (log.isDebugEnabled()) {
				log.debug("Successfully sent " + responses.size() + " response(s)");
			}
		}
	}
	
	/**
	 * Converts the specified list of post sync actions to a list of json payloads
	 * 
	 * @param actions post sync actions to process
	 * @return list of json payloads
	 */
	protected List<String> generateResponses(List<PostSyncAction> actions) {
		return actions.stream().map(a -> {
			SyncResponseModel syncResponse = new SyncResponseModel();
			syncResponse.setDateSentByReceiver(LocalDateTime.now());
			syncResponse.setMessageUuid(a.getMessage().getMessageUuid());
			syncResponse.setDateReceived(DateUtils.dateToLocalDateTime(a.getMessage().getDateReceived()));
			
			return JsonUtils.marshall(syncResponse);
		}).collect(Collectors.toList());
	}
	
}
