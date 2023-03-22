package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Updates the OpenMRS search index for entities associated to synced messages.
 */
@Component("searchIndexUpdatingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SearchIndexUpdatingProcessor extends BaseSendToCamelPostSyncActionProcessor {
	
	protected static final Logger log = LoggerFactory.getLogger(SearchIndexUpdatingProcessor.class);
	
	public SearchIndexUpdatingProcessor(ProducerTemplate producerTemplate,
	    @Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, SyncedMessageRepository repo) {
		super(ReceiverConstants.URI_UPDATE_SEARCH_INDEX, producerTemplate, executor, repo);
	}
	
	@Override
	public String getProcessorName() {
		return "search index update";
	}
	
	@Override
	public String getUniqueId(SyncedMessage item) {
		return item.getId().toString();
	}
	
	@Override
	public String getQueueName() {
		return "search-index-update";
	}
	
	@Override
	public String getThreadName(SyncedMessage item) {
		return item.getSite().getIdentifier() + "-" + item.getMessageUuid() + "-"
		        + AppUtils.getSimpleName(item.getModelClassName()) + "-" + item.getIdentifier();
	}
	
	@Override
	public String getLogicalType(SyncedMessage item) {
		return item.getClass().getName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return null;
	}
	
	@Override
	public void processWork(List<SyncedMessage> items) throws Exception {
		Map<String, SyncedMessage> keyAndLatestMsgMap = new LinkedHashMap(items.size());
		items.stream().forEach(msg -> {
			//TODO Take care of class hierarchy
			keyAndLatestMsgMap.put(msg.getModelClassName() + "#" + msg.getIdentifier(), msg);
		});
		
		Collection<SyncedMessage> latest = keyAndLatestMsgMap.values();
		
		//If an entity has multiple events, update the search index once i.e. only for the latest event
		doProcessWork(new ArrayList(latest));
		
		Collection<SyncedMessage> skipped = CollectionUtils.subtract(items, latest);
		skipped.stream().forEach(msg -> msg.setSearchIndexUpdated(true));
		
		//The skipped events will only be marked as processed in the DB without processing
		doProcessWork(new ArrayList(skipped));
	}
	
	protected void doProcessWork(List<SyncedMessage> items) throws Exception {
		super.processWork(items);
	}
	
	@Override
	public boolean skipSend(SyncedMessage item) {
		return item.isSearchIndexUpdated();
	}
	
	@Override
	public void onSuccess(SyncedMessage item) {
		if (!item.isSearchIndexUpdated()) {
			item.setSearchIndexUpdated(true);
		}
		
		repo.save(item);
	}
	
	@Override
	public Object convertBody(SyncedMessage item) {
		return ReceiverUtils.generateSearchIndexUpdatePayload(item.getModelClassName(), item.getIdentifier(),
		    item.getOperation());
	}
	
}
