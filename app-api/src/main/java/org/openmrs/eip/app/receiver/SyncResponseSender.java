package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncResponseModel;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.DateUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a batch of post sync items of type PostSyncActionType.SEND_RESPONSE in the queue that are
 * not yet successfully processed and sends sync responses for their associated sync messages.
 */
public class SyncResponseSender extends BasePostSyncActionRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncResponseSender.class);
	
	private ConnectionFactory activeMqConnFactory;
	
	private String queueName;
	
	public SyncResponseSender(SiteInfo site) {
		super(site, PostSyncActionType.SEND_RESPONSE, 100000);
		activeMqConnFactory = SyncContext.getBean(ConnectionFactory.class);
		String endpoint = SyncContext.getBean(ReceiverActiveMqMessagePublisher.class)
		        .getCamelOutputEndpoint(getSite().getIdentifier());
		if (!endpoint.startsWith("activemq:")) {
			throw new EIPException(endpoint + " is an invalid message broker endpoint value for outbound messages");
		}
		
		queueName = endpoint.substring(endpoint.indexOf(":") + 1);
	}
	
	@Override
	public String getProcessorName() {
		return "response sender";
	}
	
	@Override
	public List<PostSyncAction> getNextBatch() {
		return repo.getBatchOfPendingResponseActions(getSite(), pageable);
	}
	
	@Override
	public void process(List<PostSyncAction> actions) throws Exception {
		try {
			sendResponsesInBatch(actions);
			ReceiverUtils.updatePostSyncActionStatuses(actions, true, null);
		}
		catch (Throwable t) {
			log.warn("An error occurred while sending sync responses to site: " + getSite(), t);
			ReceiverUtils.updatePostSyncActionStatuses(actions, false, ReceiverUtils.getErrorMessage(t));
		}
	}
	
	/**
	 * Sends sync responses to a single remote site's response queue in the message broker for all the
	 * specified post sync actions in a batch
	 * 
	 * @param actions post sync actions to process
	 * @throws JMSException
	 */
	protected void sendResponsesInBatch(List<PostSyncAction> actions) throws JMSException {
		if (log.isDebugEnabled()) {
			log.debug("Sending " + actions.size() + " sync response(s) to site: " + getSite());
		}
		
		List<String> responses = generateResponses(actions);
		
		try (Connection conn = activeMqConnFactory.createConnection();
		        Session session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE)) {
			
			Queue queue = session.createQueue(queueName);
			try (MessageProducer p = session.createProducer(queue)) {
				for (String responsePayload : responses) {
					p.send(session.createTextMessage(responsePayload));
				}
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Committing " + responses.size() + " response(s) to site: " + getSite());
			}
			
			session.commit();
			
			if (log.isDebugEnabled()) {
				log.debug("Successfully committed " + responses.size() + " response(s) to site: " + getSite());
			}
		}
		
		log.info("Successfully sent " + actions.size() + " sync responses to site: " + getSite());
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
