package org.openmrs.eip.app;

import java.io.FileWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;

import liquibase.util.csv.CSVWriter;

public class ActiveMQBrowser {
	
	private static final String ACTIVEMQ_HOST = "localhost";
	
	private static final String ACTIVEMQ_PORT = "61616";
	
	private static final String ACTIVEMQ_USER = "admin";
	
	private static final String ACTIVEMQ_PASS = "admin";
	
	private static final String FILE_EVENTS = "events.csv";
	
	private static final String FILE_STATS = "stats.csv";
	
	static ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://" + ACTIVEMQ_HOST + ":" + ACTIVEMQ_PORT);
	
	public static void main(String[] args) throws Exception {
		try (Connection conn = cf.createConnection(ACTIVEMQ_USER, ACTIVEMQ_PASS);
		        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
			
			conn.start();
			
			browse(session);
			
			conn.stop();
		}
	}
	
	private static void browse(Session session) throws Exception {
		Queue queue = session.createQueue("DLQ");
		Map<String, Integer> siteCountMap = new HashMap();
		int count = 0;
		
		try (QueueBrowser browser = session.createBrowser(queue)) {
			CSVWriter writer = new CSVWriter(new FileWriter(FILE_EVENTS));
			writer.writeNext(new String[] { "Type", "UUID", "Site", "OP", "Date Sent" });
			Enumeration msgs = browser.getEnumeration();
			
			while (msgs.hasMoreElements()) {
				TextMessage msg = (TextMessage) msgs.nextElement();
				SyncModel model = JsonUtils.unmarshalSyncModel(msg.getText());
				final String siteId = model.getMetadata().getSourceIdentifier();
				siteCountMap.put(siteId, siteCountMap.getOrDefault(siteId, 0) + 1);
				
				writer.writeNext(new String[] { model.getTableToSyncModelClass().getSimpleName(), model.getModel().getUuid(),
				        siteId, model.getMetadata().getOperation(), model.getMetadata().getDateSent().toString() });
				
				count++;
			}
			
			writer.flush();
		}
		
		System.out.println("Total message Count browsed in the Dead letter queue: " + count);
		
		CSVWriter writer = new CSVWriter(new FileWriter(FILE_STATS));
		writer.writeNext(new String[] { "Site", "Event Count" });
		
		for (Map.Entry<String, Integer> e : siteCountMap.entrySet()) {
			writer.writeNext(new String[] { e.getKey(), e.getValue().toString() });
		}
		
		writer.flush();
	}
	
}
