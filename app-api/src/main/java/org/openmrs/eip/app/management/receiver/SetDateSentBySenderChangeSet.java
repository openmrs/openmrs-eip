package org.openmrs.eip.app.management.receiver;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.openmrs.eip.component.exception.EIPException;
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
	
	private static final String SQL = "UPDATE receiver_sync_msg SET date_sent_by_sender = ? WHERE id = ?";
	
	@Override
	public void execute(Database database) throws CustomChangeException {
		log.info("Setting date_sent_by_sender for existing sync messages");
		
		try {
			JdbcConnection conn = (JdbcConnection) database.getConnection();
			
			runBatchUpdate(null, conn);
		}
		catch (SQLException | DatabaseException e) {
			throw new CustomChangeException("An error occurred while setting date_sent_by_sender for existing sync messages",
			        e);
		}
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
		try (PreparedStatement s = connection.prepareStatement(SQL);) {
			autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			
			idAndDateMap.entrySet().parallelStream().forEach(entry -> {
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
		return getClass().getSimpleName() + " completed successfully";
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
