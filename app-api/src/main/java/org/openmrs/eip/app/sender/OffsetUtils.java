package org.openmrs.eip.app.sender;

import java.nio.ByteBuffer;
import java.util.Map;

import org.openmrs.eip.app.sender.OffsetVerifier.OffsetVerificationResult;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.debezium.connector.mysql.BinlogReader.BinlogPosition;

public class OffsetUtils {
	
	private static final Logger log = LoggerFactory.getLogger(OffsetUtils.class);
	
	/**
	 * Verifies the specified offset data and resets it if the contents are invalid
	 * 
	 * @param offsetRawData the offset raw data
	 * @throws Exception
	 */
	public static void verifyOffsetAndResetIfInvalid(Map<ByteBuffer, ByteBuffer> offsetRawData) throws Exception {
		if (offsetRawData.isEmpty()) {
			log.info("No existing offset file found, skipping offset verification");
			return;
		} else if (offsetRawData.size() > 1) {
			throw new EIPException("Invalid existing offset file content entry size: " + offsetRawData.size());
		}
		
		Map parsedOffsetData = parseOffsetData(offsetRawData);
		final String file = getBinlogFile(parsedOffsetData);
		final Integer position = Integer.valueOf(parsedOffsetData.get(SenderConstants.OFFSET_PROP_POSITION).toString());
		
		log.info("Verifying existing offset");
		BinlogPosition binlogPosition = new BinlogPosition(file, position);
		OffsetVerificationResult result = new OffsetVerifier(binlogPosition).verify();
		log.info("Offset verification result: " + result);
		if (result == OffsetVerificationResult.PASS) {
			log.info("Successfully verified existing offset");
			return;
		}
		
		if (result == OffsetVerificationResult.RESET) {
			log.info("Resetting offset to start position");
			
			BinlogPosition newPosition = new BinlogPosition(binlogPosition.getFilename(), 4);
			parsedOffsetData.put(SenderConstants.OFFSET_PROP_FILE, newPosition.getFilename());
			parsedOffsetData.put(SenderConstants.OFFSET_PROP_POSITION, newPosition.getPosition());
			parsedOffsetData.put(SenderConstants.OFFSET_PROP_ROW, 0);
			parsedOffsetData.put(SenderConstants.OFFSET_PROP_EVENT, 0);
			ByteBuffer valueByteBuffer = ByteBuffer.wrap(new ObjectMapper().writeValueAsBytes(parsedOffsetData));
			offsetRawData.put(offsetRawData.keySet().iterator().next(), valueByteBuffer);
			
			log.info("Successfully reset offset to start at binlog position " + newPosition);
		} else {
			throw new EIPException("Failed to verify existing offset: " + parsedOffsetData);
		}
	}
	
	private static Map parseOffsetData(Map<ByteBuffer, ByteBuffer> offsetRawData) throws Exception {
		log.info("Parsing existing offset data");
		Map parsed = new ObjectMapper().readValue(offsetRawData.values().iterator().next().array(), Map.class);
		log.info("Existing offset data: " + parsed);
		
		return parsed;
	}
	
	private static String getBinlogFile(Map offsetData) {
		return offsetData.get(SenderConstants.OFFSET_PROP_FILE).toString();
	}
	
}
