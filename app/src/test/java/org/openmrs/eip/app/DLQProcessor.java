package org.openmrs.eip.app;

import java.io.FileInputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.utils.JsonUtils;

public class DLQProcessor {
	
	private static ActiveMQConnectionFactory activeMQConnFactory;
	
	private static final Set<String> UNIQUE_REQUESTS = new HashSet(1000);
	
	private static final String INSERT = "INSERT INTO receiver_sync_request (table_name, identifier, site_id, request_uuid, date_created) "
	        + "VALUES (?, ?, ?, ?, now())";
	
	private static final String GET_REQ_UUID = "SELECT request_uuid FROM receiver_sync_request WHERE LOWER(table_name) = LOWER(?) AND identifier = LOWER(?) AND site_id = ?";
	
	private static final String GET_SITE_ID = "SELECT id FROM site_info WHERE LOWER(identifier) = LOWER(?)";
	
	public static void main(String[] args) throws Exception {
		System.out.println("Loading receiver properties...");
		
		Properties props = new Properties();
		props.load(new FileInputStream("application-receiver.properties"));
		final String ACTIVEMQ_HOST = props.getProperty("spring.artemis.host");
		final String ACTIVEMQ_PORT = props.getProperty("spring.artemis.port");
		final String ACTIVEMQ_USER = props.getProperty("spring.artemis.user");
		final String ACTIVEMQ_PASS = props.getProperty("spring.artemis.password");
		final String dbHost = props.getProperty("openmrs.db.host");
		final String dbPort = props.getProperty("openmrs.db.port");
		final String dbName = props.getProperty("openmrs.db.name") + "_dbsync";
		final String MGT_DB_URL = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
		final String MGT_DB_USER = props.getProperty("spring.mngt-datasource.username");
		final String MGT_DB_PASS = props.getProperty("spring.mngt-datasource.password");
		
		System.out.println("Done loading receiver properties");
		
		activeMQConnFactory = new ActiveMQConnectionFactory("tcp://" + ACTIVEMQ_HOST + ":" + ACTIVEMQ_PORT);
		
		try (Connection conn = activeMQConnFactory.createConnection(ACTIVEMQ_USER, ACTIVEMQ_PASS);
		        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		        java.sql.Connection mgtDbConn = DriverManager.getConnection(MGT_DB_URL, MGT_DB_USER, MGT_DB_PASS);) {
			
			conn.start();
			
			process(session, mgtDbConn);
			
			conn.stop();
		}
	}
	
	private static void process(Session session, java.sql.Connection mgtDbConn) throws Exception {
		Map<String, Long> siteIdentifierIdMap = new HashMap();
		Queue queue = session.createQueue("DLQ");
		int count = 0;
		int skipCount = 0;
		int batchSize = 0;
		
		try (QueueBrowser browser = session.createBrowser(queue);
		        PreparedStatement insertStmt = mgtDbConn.prepareStatement(INSERT)) {
			
			Enumeration msgs = browser.getEnumeration();
			
			while (msgs.hasMoreElements()) {
				count++;
				TextMessage msg = (TextMessage) msgs.nextElement();
				System.out.println();
				System.out.println("Processing message -> " + msg.getText());
				SyncModel model = JsonUtils.unmarshalSyncModel(msg.getText());
				if (model.getMetadata().getOperation().equals("d")) {
					skipCount++;
					System.out.println("Skipping message for delete event");
					continue;
				}
				
				final String siteIdentifier = model.getMetadata().getSourceIdentifier();
				Long siteId = siteIdentifierIdMap.get(siteIdentifier);
				if (siteId == null) {
					try (PreparedStatement getSiteStmt = mgtDbConn.prepareStatement(GET_SITE_ID)) {
						getSiteStmt.setString(1, siteIdentifier);
						try (ResultSet rs = getSiteStmt.executeQuery()) {
							if (rs.next()) {
								siteId = rs.getLong(1);
								siteIdentifierIdMap.put(siteIdentifier, siteId);
							} else {
								skipCount++;
								System.out.println("No site found with identifier: " + siteIdentifier);
								continue;
							}
						}
					}
				}
				
				final String identifier = model.getModel().getUuid();
				final String tableName = TableToSyncEnum.getTableToSyncEnum(model.getTableToSyncModelClass()).name()
				        .toLowerCase();
				String tableUuidAndSite = (tableName + "#" + identifier + "#" + siteIdentifier).toLowerCase();
				
				if (UNIQUE_REQUESTS.contains(tableUuidAndSite)) {
					skipCount++;
					System.out.println(
					    "Skipping message because another message was already encountered for the same entity and remote site");
					continue;
				}
				
				UNIQUE_REQUESTS.add(tableUuidAndSite);
				
				String requestUuid = null;
				try (PreparedStatement getReqStmt = mgtDbConn.prepareStatement(GET_REQ_UUID)) {
					getReqStmt.setString(1, tableName);
					getReqStmt.setString(2, identifier);
					getReqStmt.setLong(3, siteId);
					try (ResultSet rs = getReqStmt.executeQuery()) {
						if (rs.next()) {
							requestUuid = rs.getString(1);
						}
					}
				}
				
				//Skip an entity that already has a request for the same site in the DB
				if (StringUtils.isNotBlank(requestUuid)) {
					skipCount++;
					System.out.println(
					    "Skipping message because there is already an existing request for the same entity and remote site in the DB with uuid: "
					            + requestUuid);
					
					continue;
				}
				
				System.out.println("Preparing request to insert");
				insertStmt.setString(1, tableName);
				insertStmt.setString(2, identifier);
				insertStmt.setLong(3, siteId);
				insertStmt.setString(4, UUID.randomUUID().toString());
				insertStmt.addBatch();
				
				batchSize++;
				
				if (batchSize % 200 == 0) {
					commitBatch(insertStmt, batchSize);
					batchSize = 0;
				}
			}
			
			if (batchSize > 0) {
				commitBatch(insertStmt, batchSize);
			}
			
		}
		
		System.out.println("Total message count found in the dead letter queue: " + count);
		
		System.out.println("Skipped message count: " + skipCount);
	}
	
	private static void commitBatch(PreparedStatement insertStmt, int batchSize) throws SQLException {
		System.out.println("Inserting " + batchSize + " sync request(s) in a batch");
		int[] updateCounts = insertStmt.executeBatch();
		System.out.println("Successfully inserted " + updateCounts.length + " request(s) in the batch");
		for (int updateCount : updateCounts) {
			if (updateCount == Statement.SUCCESS_NO_INFO) {
				System.out.println("Batch insert executed with no success info");
			} else if (updateCount == Statement.EXECUTE_FAILED) {
				System.out.println("Failed to insert some request(s) in the batch");
			}
		}
	}
	
}
