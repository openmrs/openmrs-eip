package org.openmrs.eip.app;

import static org.openmrs.eip.app.SyncConstants.DEFAULT_BATCH_SIZE;
import static org.openmrs.eip.app.SyncConstants.ROUTE_URI_CHANGE_EVNT_PROCESSOR;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.kafka.connect.data.Struct;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("changeEventProcessor")
@Profile(SyncProfiles.SENDER)
public class ChangeEventProcessor extends BaseEventProcessor {
	
	protected static final Logger log = LoggerFactory.getLogger(ChangeEventProcessor.class);
	
	private List<CompletableFuture<Void>> futures;
	
	//private Map<String, Integer> tableAndMaxRowIdsMap;
	
	//private SnapshotSavePointStore savepointStore;
	
	private static Integer batchSize;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		if (producerTemplate == null) {
			producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		}
		
		Message message = exchange.getMessage();
		Struct primaryKeyStruct = message.getHeader(DebeziumConstants.HEADER_KEY, Struct.class);
		//TODO Take care of situation where a table has a composite PK because fields length will be > 1
		String id = primaryKeyStruct.get(primaryKeyStruct.schema().fields().get(0)).toString();
		Map<String, Object> sourceMetadata = message.getHeader(DebeziumConstants.HEADER_SOURCE_METADATA, Map.class);
		String table = sourceMetadata.get("table").toString();
		final String snapshotStr = sourceMetadata.getOrDefault("snapshot", "").toString();
		final boolean snapshot = !"false".equalsIgnoreCase(snapshotStr);
		
		if (snapshot) {
			if (executor == null) {
				executor = Executors.newFixedThreadPool(threadCount);
			}
			
			if (futures == null) {
				futures = new Vector(DEFAULT_BATCH_SIZE);
			}
			
			if (batchSize == null) {
				batchSize = DEFAULT_BATCH_SIZE;
			}
			
			/*if (tableAndMaxRowIdsMap == null) {
				tableAndMaxRowIdsMap = new ConcurrentHashMap(DEFAULT_BATCH_SIZE);
			}
			
			if (savepointStore == null) {
				savepointStore = new SnapshotSavePointStore();
				savepointStore.init();
			}
			
			Integer currentRowId = Integer.valueOf(id);
			Integer saveRowId = savepointStore.getSavedRowId(table);
			if (saveRowId != null && saveRowId >= currentRowId) {
				if (log.isDebugEnabled()) {
					log.debug("Skipping previously processed row with id: " + currentRowId + " in " + table + " table");
				}
				
				return;
			}*/
			
			futures.add(CompletableFuture.runAsync(() -> {
				final String originalThreadName = Thread.currentThread().getName();
				//Block saving offsets
				if (!CustomFileOffsetBackingStore.isPaused()) {
					CustomFileOffsetBackingStore.pause();
				}
				
				try {
					setThreadName(table, id);
					//producerTemplate.send(ROUTE_URI_CHANGE_EVNT_PROCESSOR, exchange);
					CamelUtils.send(ROUTE_URI_CHANGE_EVNT_PROCESSOR, exchange, producerTemplate);
					//TODO Add support for PKs that are not integers
					/*Integer maxRowId = tableAndMaxRowIdsMap.get(table);
					if (maxRowId == null || currentRowId > maxRowId) {
						tableAndMaxRowIdsMap.put(table, currentRowId);
					}*/
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
			}, executor));
			
			boolean isLast = snapshotStr.equalsIgnoreCase("last");
			if (isLast || futures.size() == batchSize) {
				waitForFutures(futures);
				futures.clear();
				
				if (isLast) {
					//Only save offsets if it is the last snapshot item
					log.info("Processed final snapshot change event");
					
					CustomFileOffsetBackingStore.unpause();
					
					//savepointStore.discard();
					//savepointStore = null;
				} /* else {
				  savepointStore.update(tableAndMaxRowIdsMap);
				  }*/
			}
			
		} else {
			final String originalThreadName = Thread.currentThread().getName();
			try {
				setThreadName(table, id);
				CamelUtils.send(ROUTE_URI_CHANGE_EVNT_PROCESSOR, exchange, producerTemplate);
			}
			finally {
				Thread.currentThread().setName(originalThreadName);
			}
		}
	}
	
	@Override
	public String getProcessorName() {
		return "change event";
	}
	
	private void setThreadName(String table, String id) {
		Thread.currentThread().setName(Thread.currentThread().getName() + ":change-event:" + getThreadName(table, id));
	}
	
	protected String getThreadName(String table, String id) {
		return table + "-" + id;
	}
	
}
