package org.openmrs.eip.app;

import static java.util.Collections.synchronizedList;
import static org.openmrs.eip.app.SyncConstants.ROUTE_URI_CHANGE_EVNT_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.kafka.connect.data.Struct;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("changeEventProcessor")
@Profile(SyncProfiles.SENDER)
public class ChangeEventProcessor extends BaseEventProcessor {
	
	protected static final Logger log = LoggerFactory.getLogger(ChangeEventProcessor.class);
	
	private List<CompletableFuture<Void>> futures;
	
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
				futures = synchronizedList(new ArrayList(threadCount));
			}
			
			futures.add(CompletableFuture.runAsync(() -> {
				final String originalThreadName = Thread.currentThread().getName();
				//Block saving offsets
				if (!CustomFileOffsetBackingStore.isPaused()) {
					CustomFileOffsetBackingStore.pause();
				}
				
				try {
					setThreadName(table, id);
					producerTemplate.send(ROUTE_URI_CHANGE_EVNT_PROCESSOR, exchange);
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
			}, executor));
			
			//Only save offsets if it is the last snapshot item
			if (snapshotStr.equalsIgnoreCase("last")) {
				waitForFutures(futures);
				futures.clear();
				if (snapshotStr.equalsIgnoreCase("last")) {
					log.info("Processed final snapshot change event");
				}
				
				CustomFileOffsetBackingStore.unpause();
			}
			
		} else {
			final String originalThreadName = Thread.currentThread().getName();
			try {
				setThreadName(table, id);
				if (futures.size() > 0) {
					waitForFutures(futures);
					futures.clear();
				}
				
				producerTemplate.send(ROUTE_URI_CHANGE_EVNT_PROCESSOR, exchange);
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
		Thread.currentThread().setName(Thread.currentThread().getName() + ":change-event:" + table + "-" + id);
	}
	
}
