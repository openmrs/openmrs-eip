package org.openmrs.eip.app.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.network.ServerException;
import com.mysql.cj.exceptions.MysqlErrorNumbers;

import io.debezium.connector.mysql.MySqlStreamingChangeEventSource.BinlogPosition;

/**
 * Verifies the {@link BinlogPosition} information against the MySQL server binlogs
 */
public class OffsetVerifier extends BaseBinlogClient {
	
	private static final Logger log = LoggerFactory.getLogger(OffsetVerifier.class);
	
	private OffsetVerificationResult result;
	
	public enum OffsetVerificationResult {
		PASS, RESET, ERROR
	}
	
	public OffsetVerifier(BinlogPosition binlogPosition) {
		super(binlogPosition);
	}
	
	public OffsetVerificationResult verify() {
		log.info("Verifying binlog position: " + binlogPosition);
		
		connect();
		
		return result;
	}
	
	@Override
	public void onCommunicationFailure(BinaryLogClient client, Exception ex) {
		log.warn("Invalid binlog position: " + binlogPosition, ex);
		
		OffsetVerificationResult resultValue = null;
		if (ex instanceof ServerException) {
			ServerException se = (ServerException) ex;
			if (se.getErrorCode() == MysqlErrorNumbers.ER_MASTER_FATAL_ERROR_READING_BINLOG) {
				resultValue = OffsetVerificationResult.RESET;
			}
		}
		
		if (resultValue == null) {
			resultValue = OffsetVerificationResult.ERROR;
		}
		
		setResult(resultValue);
		
		disconnect();
	}
	
	@Override
	public void onEventDeserializationFailure(BinaryLogClient client, Exception ex) {
		log.warn("Failed to deserialize event data for binlog position: " + binlogPosition, ex);
		setResult(OffsetVerificationResult.ERROR);
		disconnect();
	}
	
	/**
	 * @see EventListener#onEvent(Event)
	 */
	@Override
	public void onEvent(Event event) {
		log.info("Received event -> " + event);
		
		setResult(OffsetVerificationResult.PASS);
		disconnect();
	}
	
	private void setResult(OffsetVerificationResult result) {
		this.result = result;
	}
	
	/**
	 * @see BaseBinlogClient#onConnectionFailure()
	 */
	@Override
	public void onConnectionFailure() {
		setResult(OffsetVerificationResult.ERROR);
	}
	
	/**
	 * @see BaseBinlogClient#onDisconnectionFailure()
	 */
	@Override
	public void onDisconnectionFailure() {
		setResult(OffsetVerificationResult.ERROR);
	}
	
}
