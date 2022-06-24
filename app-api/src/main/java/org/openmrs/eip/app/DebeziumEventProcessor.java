package org.openmrs.eip.app;

import static java.util.Collections.synchronizedList;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DBZM_EVENT_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("debeziumEventProcessor")
@Profile(SyncProfiles.SENDER)
public class DebeziumEventProcessor extends BaseEventProcessor {
	
	@Override
	public void process(Exchange exchange) throws Exception {
		if (producerTemplate == null) {
			producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		}
		
		List<String> tableAndIdentifier = synchronizedList(new ArrayList(threadCount));
		List<CompletableFuture<Void>> syncThreadFutures = synchronizedList(new ArrayList(threadCount));
		List<DebeziumEvent> events = exchange.getIn().getBody(List.class);
		
		for (DebeziumEvent debeziumEvent : events) {
			final String key = debeziumEvent.getEvent().getTableName() + "#" + debeziumEvent.getEvent().getPrimaryKeyId();
			if (debeziumEvent.getEvent().getSnapshot() && !tableAndIdentifier.contains(key)) {
				tableAndIdentifier.add(key);
				if (executor == null) {
					executor = Executors.newFixedThreadPool(threadCount);
				}
				
				//TODO Periodically wait and reset futures to save memory
				syncThreadFutures.add(CompletableFuture.runAsync(() -> {
					final String originalThreadName = Thread.currentThread().getName();
					try {
						setThreadName(debeziumEvent);
						producerTemplate.sendBody(URI_DBZM_EVENT_PROCESSOR, debeziumEvent);
					}
					finally {
						Thread.currentThread().setName(originalThreadName);
					}
				}, executor));
			} else {
				final String originalThreadName = Thread.currentThread().getName();
				try {
					setThreadName(debeziumEvent);
					if (syncThreadFutures.size() > 0) {
						waitForFutures(syncThreadFutures);
						syncThreadFutures.clear();
					}
					
					producerTemplate.sendBody(URI_DBZM_EVENT_PROCESSOR, debeziumEvent);
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
			}
		}
		
		if (syncThreadFutures.size() > 0) {
			waitForFutures(syncThreadFutures);
		}
	}
	
	@Override
	public String getProcessorName() {
		return "db event";
	}
	
	private void setThreadName(DebeziumEvent event) {
		Thread.currentThread().setName(Thread.currentThread().getName() + ":db-event:" + getThreadName(event));
	}
	
	protected String getThreadName(DebeziumEvent event) {
		String name = event.getEvent().getTableName() + "-" + event.getEvent().getPrimaryKeyId() + "-" + event.getId();
		if (StringUtils.isNotBlank(event.getEvent().getIdentifier())) {
			name += ("-" + event.getEvent().getIdentifier());
		}
		
		return name;
	}
	
}
