package org.openmrs.eip.app;

import static java.util.Collections.synchronizedList;
import static org.openmrs.eip.app.SyncConstants.DEFAULT_MSG_PARALLEL_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_MSG_PARALLEL_SIZE;
import static org.openmrs.eip.app.SyncConstants.ROUTE_URI_DBZM_EVNT_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("debeziumEventProcessor")
@Profile(SyncProfiles.SENDER)
public class DebeziumEventProcessor extends EventNotifierSupport implements Processor {
	
	private static final Logger log = LoggerFactory.getLogger(DebeziumEventProcessor.class);
	
	public static final int WAIT_IN_SECONDS = 30;
	
	@Value("${" + PROP_MSG_PARALLEL_SIZE + ":" + DEFAULT_MSG_PARALLEL_SIZE + "}")
	private int threadCount;
	
	private ProducerTemplate producerTemplate;
	
	private ExecutorService executor;
	
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
				
				syncThreadFutures.add(CompletableFuture.runAsync(() -> {
					final String originalThreadName = Thread.currentThread().getName();
					try {
						setThreadName(debeziumEvent);
						producerTemplate.sendBody(ROUTE_URI_DBZM_EVNT_PROCESSOR, debeziumEvent);
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
					
					producerTemplate.sendBody(ROUTE_URI_DBZM_EVNT_PROCESSOR, debeziumEvent);
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
	public void notify(CamelEvent event) {
		if (event instanceof CamelEvent.CamelContextStoppingEvent) {
			if (executor != null) {
				log.info("Shutting down executor for db event processor threads");
				
				executor.shutdown();
				
				try {
					log.info("Waiting for " + WAIT_IN_SECONDS + " seconds for db event processor threads to terminate");
					
					executor.awaitTermination(WAIT_IN_SECONDS, TimeUnit.SECONDS);
					
					log.info("Done shutting down executor for db event processor threads");
				}
				catch (Exception e) {
					log.error("An error occurred while waiting for db event processor threads to terminate");
				}
			}
		}
	}
	
	/**
	 * Wait for all the Future instances in the specified list to terminate
	 *
	 * @param futures the list of Futures instance to wait for
	 * @throws Exception
	 */
	public void waitForFutures(List<CompletableFuture<Void>> futures) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Waiting for " + futures.size() + " db event processor thread(s) to terminate");
		}
		
		CompletableFuture<Void> allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		
		allFuture.get();
		
		if (log.isDebugEnabled()) {
			log.debug(futures.size() + " db event processor thread(s) have terminated");
		}
	}
	
	private void setThreadName(DebeziumEvent event) {
		Thread.currentThread().setName(Thread.currentThread().getName() + ":" + getThreadName(event));
	}
	
	protected String getThreadName(DebeziumEvent event) {
		String name = event.getEvent().getTableName() + "-" + event.getEvent().getPrimaryKeyId() + "-" + event.getId();
		if (StringUtils.isNotBlank(event.getEvent().getIdentifier())) {
			name += ("-" + event.getEvent().getIdentifier());
		}
		
		return name;
	}
	
}
