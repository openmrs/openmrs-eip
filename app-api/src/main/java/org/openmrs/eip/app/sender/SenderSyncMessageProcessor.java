package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.repository.SenderSyncMessageRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component("senderSyncMsgProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderSyncMessageProcessor extends BaseQueueProcessor<SenderSyncMessage> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SenderSyncMessageProcessor.class);
	
	@Value("${" + SenderConstants.PROP_SENDER_ID + "}")
	private String senderId;
	
	private JmsTemplate jmsTemplate;
	
	private SenderSyncMessageRepository repo;
	
	public SenderSyncMessageProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    JmsTemplate jmsTemplate, SenderSyncMessageRepository repo) {
		super(executor);
		this.jmsTemplate = jmsTemplate;
		this.repo = repo;
	}
	
	@Override
	public String getProcessorName() {
		return "sync msg";
	}
	
	@Override
	public String getThreadName(SenderSyncMessage msg) {
		return msg.getTableName() + "-" + msg.getIdentifier() + "-" + msg.getMessageUuid();
	}
	
	@Override
	public String getUniqueId(SenderSyncMessage item) {
		return item.getIdentifier();
	}
	
	@Override
	public String getQueueName() {
		return "sync-msg";
	}
	
	@Override
	public String getLogicalType(SenderSyncMessage item) {
		return item.getTableName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfTablesInHierarchy(logicalType);
	}
	
	@Override
	public void processItem(SenderSyncMessage syncMsg) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Preparing sync payload to send");
		}
		
		SyncModel syncModel = JsonUtils.unmarshalSyncModel(syncMsg.getData());
		syncModel.getMetadata().setDateSent(LocalDateTime.now());
		syncModel.getMetadata().setSourceIdentifier(senderId);
		String syncData = JsonUtils.marshall(syncModel);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sync payload -> " + syncData);
		}
		
		jmsTemplate.convertAndSend(SenderUtils.getQueueName(), syncData);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sync payload sent!");
		}
		
		syncMsg.setData(syncData);
		syncMsg.markAsSent(syncModel.getMetadata().getDateSent());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Updating sender sync message status to " + syncMsg.getStatus());
		}
		
		repo.save(syncMsg);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Successfully sent and updated status for sync message");
		}
	}
	
}
