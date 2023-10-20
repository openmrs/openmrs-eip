package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_DB_PASSWORD;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_DB_USER;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_SERVER_ID;
import static org.openmrs.eip.component.Constants.PROP_OPENMRS_DB_HOST;
import static org.openmrs.eip.component.Constants.PROP_OPENMRS_DB_PORT;

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
	public static List<String> getBinaryLogFileNames() throws SQLException {
		Environment env = SyncContext.getBean(Environment.class);
		final String host = env.getProperty(PROP_OPENMRS_DB_HOST);
		final String port = env.getProperty(PROP_OPENMRS_DB_PORT);
		final String user = env.getProperty(PROP_DBZM_DB_USER);
		final String url = "jdbc:mysql://" + host + ":" + port
		        + "?autoReconnect=true&sessionVariables=storage_engine=InnoDB&useUnicode=true&characterEncoding=UTF-8";
		
		List<String> files = new ArrayList<>();
		
		try (Connection c = DriverManager.getConnection(url, user, env.getProperty(PROP_DBZM_DB_PASSWORD));
		        Statement s = c.createStatement()) {
			
			ResultSet r = s.executeQuery("SHOW BINARY LOGS");
			while (r.next()) {
				files.add(r.getString("Log_name"));
			}
		}
		
		return files;
	}
	
}
