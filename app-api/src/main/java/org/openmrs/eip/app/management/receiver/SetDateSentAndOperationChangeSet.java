package org.openmrs.eip.app.management.receiver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

/**
 * {@link CustomTaskChange} that sets column values for existing messages in a specific table
 */
public class SetDateSentAndOperationChangeSet implements CustomTaskChange {
	
	private static final Logger log = LoggerFactory.getLogger(SetDateSentAndOperationChangeSet.class);
	
	private static final String QUERY = "SELECT id, entity_payload FROM TABLE WHERE date_sent_by_sender IS NULL LIMIT 1000";
	
	private static final String UPDATE_SQL = "UPDATE TABLE SET date_sent_by_sender = ?, operation = ? WHERE id = ?";
	
	private String tableName;
	
	/**
	 * Sets the tableName
	 *
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public void execute(Database database) throws CustomChangeException {
		log.info("Setting date_sent_by_sender for existing messages in " + tableName + " table");
		
		try {
			JdbcConnection conn = (JdbcConnection) database.getConnection();
			setDateSentBySender(tableName, conn);
		}
		catch (SQLException | DatabaseException e) {
			throw new CustomChangeException(
			        "An error occurred while setting date_sent_by_sender for existing messages in " + tableName + " table",
			        e);
		}
	}
	
	private void setDateSentBySender(String tableName, JdbcConnection conn) throws SQLException, DatabaseException {
		Map<Long, String> idAndPayloadMap = fetchBatchOfMessageDetails(tableName, conn);
		while (idAndPayloadMap.size() > 0) {
			Map<Long, RowData> idAndDataMap = new HashMap(idAndPayloadMap.size());
			idAndPayloadMap.entrySet().stream().forEach(e -> {
				SyncMetadata metadata = JsonUtils.unmarshal(e.getValue(), SyncModel.class).getMetadata();
				RowData rowData = new RowData(metadata.getDateSent(), metadata.getOperation());
				idAndDataMap.put(e.getKey(), rowData);
			});
			
			runBatchUpdate(tableName, idAndDataMap, conn);
			
			idAndPayloadMap = fetchBatchOfMessageDetails(tableName, conn);
		}
	}
	
	/**
	 * Fetches the next batch of sync message details
	 *
	 * @param tableName the name of the table to update
	 * @param connection The database connection
	 * @return Map of sync message ids and their payloads
	 * @throws Exception
	 */
	private Map<Long, String> fetchBatchOfMessageDetails(String tableName, JdbcConnection connection)
	    throws SQLException, DatabaseException {
		
		Map<Long, String> idAndPayloadMap = new HashMap(1000);
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(QUERY.replace("TABLE", tableName))) {
			while (rs.next()) {
				idAndPayloadMap.put(rs.getLong(1), rs.getString(2));
			}
		}
		
		return idAndPayloadMap;
	}
	
	/**
	 * Executes all the updates in a batch.
	 * 
	 * @param tableName the name of the table to update
	 * @param idAndDataMap mapping between sync message ids and their respective {@link RowData}
	 *            instances
	 * @param connection The database connection
	 */
	private void runBatchUpdate(String tableName, Map<Long, RowData> idAndDataMap, JdbcConnection connection)
	    throws SQLException, DatabaseException {
		
		Boolean autoCommit = null;
		try (PreparedStatement s = connection.prepareStatement(UPDATE_SQL.replace("TABLE", tableName))) {
			autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			for (Map.Entry<Long, RowData> entry : idAndDataMap.entrySet()) {
				s.setTimestamp(1, Timestamp.from(entry.getValue().dateSent.atZone(ZoneId.systemDefault()).toInstant()));
				s.setString(2, entry.getValue().operation);
				s.setLong(3, entry.getKey());
				s.addBatch();
			}
			
			s.executeBatch();
			connection.commit();
		}
		catch (SQLException | DatabaseException e) {
			connection.rollback();
			throw e;
		}
		finally {
			if (autoCommit != null) {
				connection.setAutoCommit(autoCommit);
			}
		}
	}
	
	@Override
	public String getConfirmationMessage() {
		return "Setting date_sent_by_sender for existing messages in " + tableName + " table completed successfully";
	}
	
	@Override
	public void setUp() throws SetupException {
	}
	
	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
	}
	
	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}
	
	private class RowData {
		
		LocalDateTime dateSent;
		
		String operation;
		
		RowData(LocalDateTime dateSent, String operation) {
			this.dateSent = dateSent;
			this.operation = operation;
		}
		
	}
	
}
