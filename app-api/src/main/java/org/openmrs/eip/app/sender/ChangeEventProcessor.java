package org.openmrs.eip.app.sender;

import static org.apache.camel.component.debezium.DebeziumConstants.HEADER_SOURCE_METADATA;
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
import org.openmrs.eip.component.exception.EIPException;
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
		try {
			if (CustomFileOffsetBackingStore.isDisabled()) {
				if (log.isDebugEnabled()) {
					log.debug("Deferring DB event because an error was encountered while processing a previous one");
				}
				
				return;
			}
			
			//In case if initial loading, block saving offsets until all rows in the snapshot are processed
			//In case of incremental, block saving until we have successfully save the event to the event queue in the DB
			if (!CustomFileOffsetBackingStore.isPaused()) {
				CustomFileOffsetBackingStore.pause();
			}
			
			final Message message = exchange.getMessage();
			final Struct primaryKeyStruct = message.getHeader(DebeziumConstants.HEADER_KEY, Struct.class);
			//TODO Take care of situation where a table has a composite PK because fields length will be > 1
			final String id = primaryKeyStruct.get(primaryKeyStruct.schema().fields().get(0)).toString();
			final Map<String, Object> sourceMetadata = message.getHeader(HEADER_SOURCE_METADATA, Map.class);
			final String table = sourceMetadata.get("table").toString();
			final String snapshotStr = sourceMetadata.getOrDefault("snapshot", "").toString();
			final boolean snapshot = !"false".equalsIgnoreCase(snapshotStr);
			
			if (snapshot) {
				processSnapshotEvent(exchange, sourceMetadata, id, table, snapshotStr);
			} else {
				final String originalThreadName = Thread.currentThread().getName();
				try {
					setThreadName(table, id);
					handler.handle(table, id, false, sourceMetadata, exchange);
					CustomFileOffsetBackingStore.unpause();
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
			}
		}
		catch (Throwable t) {
			try {
				CustomFileOffsetBackingStore.disable();
			}
			finally {
				throw new EIPException("Failed to process DB event", t);
			}
		}
	}
	
	@Override
	public String getProcessorName() {
		return "change event";
	}
	
	private void processSnapshotEvent(Exchange exchange, Map<String, Object> sourceMetadata, String id, String table,
	                                  String snapshotStr)
	    throws Exception {
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
