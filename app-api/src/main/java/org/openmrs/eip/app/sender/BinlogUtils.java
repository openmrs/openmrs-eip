package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_DB_PASSWORD;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_DB_USER;
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

/**
 * Contains utilities to work with MySQL binary logs
 */
public class BinlogUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(BinlogUtils.class);
	
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
	 * Gets the name of the last binary log file name to keep after the specified bin log file and
	 * specified maximum allowed count of processed binlog to be kept.
	 *
	 * @param binlogFile the current binlog file from the debezium offset
	 * @param maxKeepCount the maximum number of processed binlog files to be keep
	 * @return the name of the last binary log file to keep
	 * @throws SQLException
	 */
	public static String getLastProcessedBinLogFileToKeep(String binlogFile, int maxKeepCount) throws Exception {
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
	 * Deletes all the processed binary log files prior to the specified binary log file name, the
	 * specified file is not deleted.
	 *
	 * @param priorToBinLogFile the name of the last file to keep
	 * @throws SQLException
	 */
	public static void purgeBinLogsTo(String priorToBinLogFile) throws SQLException {
		try (Connection c = getConnectionToBinaryLogs(); Statement s = c.createStatement()) {
			log.info("Purging binlog files up to " + priorToBinLogFile);
			int purgeCount = s.executeUpdate("PURGE BINARY LOGS TO '" + priorToBinLogFile + "'");
			log.info("Successfully purged " + purgeCount);
		}
	}
	
	/**
	 * Deletes all the processed binary log files so that the remaining processed count does not exceed
	 * the specified maximum allowed.
	 *
	 * @param debeziumOffsetFile the debezium offset file to extract the current log file used to
	 *            determine processed files
	 * @param maxKeepCount the maximum number of processed binlog files to be keep
	 * @throws SQLException
	 */
	public static void purgeBinLogs(File debeziumOffsetFile, int maxKeepCount) throws Exception {
		String binlogFile = OffsetUtils.getBinlogFileName(debeziumOffsetFile);
		if (debeziumOffsetFile.exists() && binlogFile == null) {
			log.info("Debezium offset file contains no binlog file name");
			return;
		}
		
		final String priorToBinLogFile = getLastProcessedBinLogFileToKeep(binlogFile, maxKeepCount);
		if (priorToBinLogFile == null) {
			log.info("No binlog files to be purged");
			return;
		}
		
		purgeBinLogsTo(priorToBinLogFile);
	}
	
	public static Connection getConnectionToBinaryLogs() throws SQLException {
		Environment env = SyncContext.getBean(Environment.class);
		final String host = env.getProperty(PROP_OPENMRS_DB_HOST);
		final String port = env.getProperty(PROP_OPENMRS_DB_PORT);
		final String user = env.getProperty(PROP_DBZM_DB_USER);
		final String url = "jdbc:mysql://" + host + ":" + port
		        + "?autoReconnect=true&sessionVariables=storage_engine=InnoDB&useUnicode=true&characterEncoding=UTF-8";
		
		return DriverManager.getConnection(url, user, env.getProperty(PROP_DBZM_DB_PASSWORD));
	}
	
}
