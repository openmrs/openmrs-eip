package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_DB_PASSWORD;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_DB_USER;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_SERVER_ID;
import static org.openmrs.eip.component.Constants.PROP_OPENMRS_DB_HOST;
import static org.openmrs.eip.component.Constants.PROP_OPENMRS_DB_PORT;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.BinaryLogClient.LifecycleListener;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer.CompatibilityMode;

import io.debezium.connector.mysql.BinlogReader.BinlogPosition;

public class SenderUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(SenderUtils.class);
	
	/**
	 * Generates a mask for the specified value
	 * 
	 * @param value the value to mask
	 * @param <T>
	 * @return the masked value
	 */
	public static <T> T mask(T value) {
		if (value == null) {
			if (log.isDebugEnabled()) {
				log.debug("Skipping masking for a null value");
			}
			
			return null;
		}
		
		Object masked;
		if (String.class.isAssignableFrom(value.getClass())) {
			masked = SenderConstants.MASK;
		} else {
			throw new EIPException("Don't know how mask a value of type: " + value.getClass());
		}
		
		return (T) masked;
	}
	
	/**
	 * Creates a {@link BinaryLogClient} instance to connect to the MySQL binlog at the filename and
	 * position of the specified {@link BinlogPosition}
	 * 
	 * @param binlogPosition {@link BinlogPosition} instance
	 * @return BinaryLogClient
	 */
	public static BinaryLogClient createBinlogClient(BinlogPosition binlogPosition, EventListener eventListener,
	                                                 LifecycleListener lifecycleListener) {
		
		Environment env = SyncContext.getBean(Environment.class);
		BinaryLogClient client = new BinaryLogClient(env.getProperty(PROP_OPENMRS_DB_HOST),
		        env.getProperty(PROP_OPENMRS_DB_PORT, int.class), env.getProperty(PROP_DBZM_DB_USER),
		        env.getProperty(PROP_DBZM_DB_PASSWORD));
		client.setServerId(env.getProperty(PROP_DBZM_SERVER_ID, int.class));
		client.setBinlogFilename(binlogPosition.getFilename());
		client.setBinlogPosition(binlogPosition.getPosition());
		client.setKeepAlive(false);
		EventDeserializer eventDeserializer = new EventDeserializer();
		eventDeserializer.setCompatibilityMode(CompatibilityMode.DATE_AND_TIME_AS_LONG,
		    CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY);
		client.setEventDeserializer(eventDeserializer);
		client.registerEventListener(eventListener);
		client.registerLifecycleListener(lifecycleListener);
		
		return client;
	}
	
	/**
	 * Fetches the current list of all the mysql binary log files .
	 * 
	 * @return list of binary log file names
	 * @throws SQLException
	 */
	public static List<String> getBinLogFiles() throws SQLException {
		List<String> files = new ArrayList<>();
		try (Connection c = getConnectionToBinaryLogs(); Statement s = c.createStatement()) {
			ResultSet r = s.executeQuery("SHOW BINARY LOGS");
			while (r.next()) {
				files.add(r.getString(1));
			}
		}
		
		return files;
	}
	
	/**
	 * Gets the name of the last binary log file name to keep based on the specified maximum allowed
	 * count of processed binlog to be kept.
	 * 
	 * @param debeziumOffsetFile the debezium offset file
	 * @param maxKeepCount the maximum number of binlog files to be keep
	 * @return the name of the last binary log file to keep
	 * @throws SQLException
	 */
	public static String getLastProcessedBinLogFileToKeep(File debeziumOffsetFile, int maxKeepCount) throws Exception {
		String binlogFile = OffsetUtils.getBinlogFileName(debeziumOffsetFile);
		if (binlogFile == null) {
			return null;
		}
		
		List<String> binlogFiles = getBinLogFiles();
		if (!binlogFiles.stream().anyMatch(binlogFile::equals)) {
			throw new EIPException("Debezium offset binlog file " + binlogFile + " is unknown by MySQL server");
		}
		
		int lastFileIndex = binlogFiles.indexOf(binlogFile) - maxKeepCount;
		if (lastFileIndex < 0) {
			return null;
		}
		
		return binlogFiles.get(lastFileIndex);
	}
	
	/**
	 * Deletes all the binary log files prior to the specified binary log file name, the specified file
	 * is not deleted.
	 * 
	 * @param priorToBinLogFile the name of the last file to keep
	 * @return the count of the deleted binary log files
	 * @throws SQLException
	 */
	public static int purgeBinaryLogs(String priorToBinLogFile) throws SQLException {
		try (Connection c = getConnectionToBinaryLogs(); Statement s = c.createStatement()) {
			return s.executeUpdate("PURGE BINARY LOGS TO '" + priorToBinLogFile + "'");
		}
	}
	
	private static Connection getConnectionToBinaryLogs() throws SQLException {
		Environment env = SyncContext.getBean(Environment.class);
		final String host = env.getProperty(PROP_OPENMRS_DB_HOST);
		final String port = env.getProperty(PROP_OPENMRS_DB_PORT);
		final String user = env.getProperty(PROP_DBZM_DB_USER);
		final String url = "jdbc:mysql://" + host + ":" + port
		        + "?autoReconnect=true&sessionVariables=storage_engine=InnoDB&useUnicode=true&characterEncoding=UTF-8";
		
		return DriverManager.getConnection(url, user, env.getProperty(PROP_DBZM_DB_PASSWORD));
	}
	
}
