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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("debeziumEventProcessor")
public class DebeziumEventProcessor implements Processor {
	
	private static final Logger log = LoggerFactory.getLogger(DebeziumEventProcessor.class);
	
	@Value("${" + PROP_MSG_PARALLEL_SIZE + ":" + DEFAULT_MSG_PARALLEL_SIZE + "}")
	private int threadCount;
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	private ExecutorService msgExecutor;
	
	public void process(Exchange exchange) throws Exception {

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
