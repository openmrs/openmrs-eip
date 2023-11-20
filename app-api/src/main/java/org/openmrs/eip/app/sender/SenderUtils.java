package org.openmrs.eip.app.sender;

import org.openmrs.eip.component.Constants;
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

import io.debezium.connector.mysql.MySqlStreamingChangeEventSource.BinlogPosition;

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
		BinaryLogClient client = new BinaryLogClient(env.getProperty(Constants.PROP_OPENMRS_DB_HOST),
		        env.getProperty(Constants.PROP_OPENMRS_DB_PORT, int.class),
		        env.getProperty(SenderConstants.PROP_DBZM_DB_USER), env.getProperty(SenderConstants.PROP_DBZM_DB_PASSWORD));
		client.setServerId(env.getProperty(SenderConstants.PROP_DBZM_SERVER_ID, int.class));
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
	
}
