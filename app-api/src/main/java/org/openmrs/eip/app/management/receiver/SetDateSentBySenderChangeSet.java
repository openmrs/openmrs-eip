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

import org.openmrs.eip.component.exception.EIPException;
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
 * {@link CustomTaskChange} that sets receiver_sync_msg.date_sent_by_sender values for existing sync
 * messages
 */
public class SetDateSentBySenderChangeSet implements CustomTaskChange {
	
	private static final Logger log = LoggerFactory.getLogger(SetDateSentBySenderChangeSet.class);
	
	private static final String QUERY = "SELECT id, entity_payload FROM receiver_sync_msg WHERE date_sent_by_sender IS NULL LIMIT 1000";
	
	private static final String UPDATE_SQL = "UPDATE receiver_sync_msg SET date_sent_by_sender = ? WHERE id = ?";
	
	@Override
	public void execute(Database database) throws CustomChangeException {
		log.info("Setting date_sent_by_sender for existing sync messages");
		
		try {
			JdbcConnection conn = (JdbcConnection) database.getConnection();
			Map<Long, String> idAndPayloadMap = fetchBatchOfMessageDetails(conn);
			while (idAndPayloadMap.size() > 0) {
				Map<Long, LocalDateTime> idAndDateMap = new HashMap(idAndPayloadMap.size());
				idAndPayloadMap.entrySet().stream().forEach(e -> {
					SyncMetadata metadata = JsonUtils.unmarshal(e.getValue(), SyncModel.class).getMetadata();
					idAndDateMap.put(e.getKey(), metadata.getDateSent());
				});
				
				runBatchUpdate(idAndDateMap, conn);
				
				idAndPayloadMap = fetchBatchOfMessageDetails(conn);
			}
		}
		catch (SQLException | DatabaseException e) {
			throw new CustomChangeException("An error occurred while setting date_sent_by_sender for existing sync messages",
			        e);
		}
	}
	
	/**
	 * Fetches the next batch of sync message details
	 * 
	 * @param connection The database connection
	 * @return Map of sync message ids and their payloads
	 * @throws Exception
	 */
	protected Map<Long, String> fetchBatchOfMessageDetails(JdbcConnection connection)
	    throws SQLException, DatabaseException {
		
		Map<Long, String> idAndPayloadMap = new HashMap(1000);
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(QUERY)) {
			while (rs.next()) {
				idAndPayloadMap.put(rs.getLong(1), rs.getString(2));
			}
		}
		
		return idAndPayloadMap;
	}
	
	/**
	 * Executes all the updates in a batch.
	 * 
	 * @param idAndDateMap mapping between sync message ids and their respective date sent by sender
	 *            values
	 * @param connection The database connection
	 */
	private void runBatchUpdate(Map<Long, LocalDateTime> idAndDateMap, JdbcConnection connection)
	    throws SQLException, DatabaseException {
		
		Boolean autoCommit = null;
		try (PreparedStatement s = connection.prepareStatement(UPDATE_SQL);) {
			autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			
			idAndDateMap.entrySet().stream().forEach(entry -> {
				try {
					s.setTimestamp(1, Timestamp.from(entry.getValue().atZone(ZoneId.systemDefault()).toInstant()));
					s.setLong(2, entry.getKey());
					s.addBatch();
				}
				catch (SQLException se) {
					throw new EIPException("Failed to add to batch", se);
				}
			});
			
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
		return "Setting date_sent_by_sender for existing sync messages completed successfully";
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
	
}
