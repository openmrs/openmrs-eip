package org.openmrs.eip.app.sender;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.AbstractLifecycleListener;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;

import io.debezium.connector.mysql.MySqlStreamingChangeEventSource.BinlogPosition;

/**
 * Base class for listeners for both binlog and client life cycle events
 */
public abstract class BaseBinlogClient extends AbstractLifecycleListener implements EventListener {
	
	private static final Logger log = LoggerFactory.getLogger(BaseBinlogClient.class);
	
	private BinaryLogClient client;
	
	protected BinlogPosition binlogPosition;
	
	public BaseBinlogClient(BinlogPosition binlogPosition) {
		this.binlogPosition = binlogPosition;
		client = SenderUtils.createBinlogClient(binlogPosition, this, this);
	}
	
	protected void connect() {
		try {
			log.info("Connecting to binlogs at " + binlogPosition);
			
			client.connect();
		}
		catch (IOException e) {
			log.error("An error occurred while connecting to the binlogs", e);
			onConnectionFailure();
		}
	}
	
	protected void disconnect() {
		log.info("Disconnecting from binlogs");
		
		try {
			client.disconnect();
		}
		catch (IOException e) {
			log.error("Failed to disconnect from binlogs", e);
			onDisconnectionFailure();
		}
	}
	
	/**
	 * Called after an error is encountered when the client is connecting to the MysQL binlogs
	 */
	public void onConnectionFailure() {
	}
	
	/**
	 * Called after an error is encountered when the client is disconnecting from the MysQL binlogs
	 */
	public void onDisconnectionFailure() {
	}
	
}
