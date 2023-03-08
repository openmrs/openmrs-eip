package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes an un-itemized synced message to generate post sync items.
 */
@Component("syncedMsgItemizingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SyncedMessageItemizingProcessor extends BaseQueueProcessor<SyncedMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncedMessageItemizingProcessor.class);
	
	public SyncedMessageItemizingProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor) {
		super(executor);
	}
	
	@Override
	public String getProcessorName() {
		return "msg itemizer";
	}
	
	@Override
	public String getUniqueId(SyncedMessage item) {
		return item.getIdentifier();
	}
	
	@Override
	public String getQueueName() {
		return "msg-itemizer";
	}
	
	@Override
	public String getThreadName(SyncedMessage item) {
		return item.getSite().getIdentifier() + "-" + item.getMessageUuid() + "-"
		        + AppUtils.getSimpleName(item.getModelClassName()) + "-" + item.getIdentifier();
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
	public void processItem(SyncedMessage item) {
		ReceiverUtils.itemize(item);
	}
	
}
