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

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncResponseModel;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.DateUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a batch of messages in the synced queue for which responses have not yet ben sent and sends
 * them to their respective queues in the message broker.
 */
public class SyncResponseSender extends BasePostSyncActionRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncResponseSender.class);
	
	private ConnectionFactory activeMqConnFactory;
	
	private SyncResponseSenderProcessor processor;
	
	private String queueName;
	
	public SyncResponseSender(SiteInfo site) {
		super(site);
		activeMqConnFactory = SyncContext.getBean(ConnectionFactory.class);
		processor = SyncContext.getBean(SyncResponseSenderProcessor.class);
		String endpoint = SyncContext.getBean(ReceiverActiveMqMessagePublisher.class)
		        .getCamelOutputEndpoint(site.getIdentifier());
		if (!endpoint.startsWith("activemq:")) {
			throw new EIPException(endpoint + " is an invalid message broker endpoint value for outbound messages");
		}
		
		queueName = endpoint.substring(endpoint.indexOf(":") + 1);
	}
	
	@Override
	public String getTaskName() {
		return "response sender task";
	}
	
	@Override
	public List<SyncedMessage> getNextBatch() {
		return repo.getBatchOfMessagesForResponse(site, page);
	}
	
	@Override
	public void process(List<SyncedMessage> messages) throws Exception {
		try {
			sendResponsesInBatch(messages);
			processor.processWork(messages);
		}
		catch (Throwable t) {
			log.warn("An error occurred while sending sync responses to site: " + site, t);
		}
	}
	
	/**
	 * Sends sync responses to a single remote site's response queue in the message broker for all the
	 * specified messages in a batch
	 * 
	 * @param messages messages to process
	 * @throws JMSException
	 */
	protected void sendResponsesInBatch(List<SyncedMessage> messages) throws JMSException {
		if (log.isDebugEnabled()) {
			log.debug("Sending " + messages.size() + " sync response(s) to site: " + site);
		}
		
		List<String> responses = generateResponses(messages);
		
		try (Connection conn = activeMqConnFactory.createConnection();
		        Session session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE)) {
			
			Queue queue = session.createQueue(queueName);
			try (MessageProducer p = session.createProducer(queue)) {
				for (String responsePayload : responses) {
					p.send(session.createTextMessage(responsePayload));
				}
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Committing " + responses.size() + " response(s) to site: " + site);
			}
			
			session.commit();
			
			if (log.isDebugEnabled()) {
				log.debug("Successfully committed " + responses.size() + " response(s) to site: " + site);
			}
		}
		
		log.info("Successfully sent " + messages.size() + " sync responses to site: " + site);
	}
	
	/**
	 * Converts the specified list of messages to a list of json payloads
	 * 
	 * @param messages messages to process
	 * @return list of json payloads
	 */
	protected List<String> generateResponses(List<SyncedMessage> messages) {
		return messages.stream().map(m -> {
			SyncResponseModel syncResponse = new SyncResponseModel();
			syncResponse.setDateSentByReceiver(LocalDateTime.now());
			syncResponse.setMessageUuid(m.getMessageUuid());
			syncResponse.setDateReceived(DateUtils.dateToLocalDateTime(m.getDateReceived()));
			
			return JsonUtils.marshall(syncResponse);
		}).collect(Collectors.toList());
	}
	
}
