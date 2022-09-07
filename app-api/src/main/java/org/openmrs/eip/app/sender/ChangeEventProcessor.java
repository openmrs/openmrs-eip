package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.SyncConstants.DEFAULT_BATCH_SIZE;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.kafka.connect.data.Struct;
import org.openmrs.eip.app.BaseParallelProcessor;
import org.openmrs.eip.app.CustomFileOffsetBackingStore;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("changeEventProcessor")
@Profile(SyncProfiles.SENDER)
public class ChangeEventProcessor extends BaseParallelProcessor {
	
	protected static final Logger log = LoggerFactory.getLogger(ChangeEventProcessor.class);
	
	private List<CompletableFuture<Void>> futures;
	
	private Map<String, Integer> tableAndMaxRowIdsMap;
	
	private SnapshotSavePointStore savepointStore;
	
	private static Integer batchSize;
	
	private ChangeEventHandler handler;
	
	public ChangeEventProcessor(@Autowired ChangeEventHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getMessage();
		Struct primaryKeyStruct = message.getHeader(DebeziumConstants.HEADER_KEY, Struct.class);
		//TODO Take care of situation where a table has a composite PK because fields length will be > 1
		String id = primaryKeyStruct.get(primaryKeyStruct.schema().fields().get(0)).toString();
		Map<String, Object> sourceMetadata = message.getHeader(DebeziumConstants.HEADER_SOURCE_METADATA, Map.class);
		String table = sourceMetadata.get("table").toString();
		final String snapshotStr = sourceMetadata.getOrDefault("snapshot", "").toString();
		final boolean snapshot = !"false".equalsIgnoreCase(snapshotStr);
		
		if (snapshot) {
			//Block saving offsets until all rows in the snapshot are processed
			if (!CustomFileOffsetBackingStore.isPaused()) {
				CustomFileOffsetBackingStore.pause();
			}
			
			if (batchSize == null) {
				batchSize = DEFAULT_BATCH_SIZE;
			}
			
			if (futures == null) {
				futures = new Vector(batchSize);
			}
			
			if (tableAndMaxRowIdsMap == null) {
				tableAndMaxRowIdsMap = new ConcurrentHashMap(batchSize);
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
			}
			
			futures.add(CompletableFuture.runAsync(() -> {
				final String originalThreadName = Thread.currentThread().getName();

				try {
					setThreadName(table, id);
					handler.handle(table, id, true, sourceMetadata, exchange);
					updateTableAndMaxRowIdsMap(table, currentRowId);
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
			}, executor));
			
			boolean isLast = snapshotStr.equalsIgnoreCase("last");
			if (isLast || futures.size() == batchSize) {
				waitForFutures(futures);
				//If the executor is already shutdown, there could be tasks for some DB events that were never processed
				//Which leaves unprocessed rows when initial loading is resumed, so we can't persist the savepoint
				if (executor.isShutdown()) {
					log.warn("Executor is already shutdown, can't persist snapshot save point");
					return;
				}
				
				for (CompletableFuture f : futures) {
					boolean stop = false;
					if (!f.isDone()) {
						stop = true;
						log.warn("Detected DB event processor threads that were not yet done");
					} else if (f.isCancelled()) {
						stop = true;
						log.warn("Detected DB event processor threads that were cancelled");
					} else if (f.isCompletedExceptionally()) {
						stop = true;
						log.warn("Detected DB event processor threads that encountered exceptions");
					}
					
					if (stop) {
						log.warn(
						    "Can't persist snapshot save point because some DB event processor threads didn't terminate properly");
						return;
					}
				}
				
				futures.clear();
				
				if (isLast) {
					//Only save offsets if it is the last snapshot item
					log.info("Processed final snapshot change event");
					
					CustomFileOffsetBackingStore.unpause();
					
					savepointStore.discard();
					savepointStore = null;
				} else {
					savepointStore.update(tableAndMaxRowIdsMap);
				}
			}
		} else {
			final String originalThreadName = Thread.currentThread().getName();
			try {
				setThreadName(table, id);
				handler.handle(table, id, false, sourceMetadata, exchange);
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
	
	private synchronized void updateTableAndMaxRowIdsMap(String table, Integer currentRowId) {
		//TODO Add support for PKs that are not integers
		Integer maxRowId = tableAndMaxRowIdsMap.get(table);
		if (maxRowId == null || currentRowId > maxRowId) {
			tableAndMaxRowIdsMap.put(table, currentRowId);
		}
	}
	
}
