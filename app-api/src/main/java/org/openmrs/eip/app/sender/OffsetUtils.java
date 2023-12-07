package org.openmrs.eip.app.sender;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.apache.kafka.connect.storage.MemoryOffsetBackingStore;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.CustomFileOffsetBackingStore;
import org.openmrs.eip.app.sender.OffsetVerifier.OffsetVerificationResult;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.debezium.connector.mysql.MySqlStreamingChangeEventSource.BinlogPosition;

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
	
	/**
	 * Retrieves the current binlog file name from the specified debezium offset file
	 * 
	 * @param offsetFile the debezium offset file
	 * @return the binlog file name
	 * @throws Exception
	 */
	public static String getBinlogFileName(File offsetFile) throws Exception {
		if (!offsetFile.exists()) {
			log.info("No existing debezium offset file found");
			return null;
		}
		
		CustomFileOffsetBackingStore store = new CustomFileOffsetBackingStore();
		AppUtils.setFieldValue(store, FileOffsetBackingStore.class.getDeclaredField("file"), offsetFile);
		AppUtils.invokeMethod(store, FileOffsetBackingStore.class.getDeclaredMethod("load"));
		Map<ByteBuffer, ByteBuffer> offsetRawData = AppUtils.getFieldValue(store,
		    MemoryOffsetBackingStore.class.getDeclaredField("data"));
		
		return getBinlogFile(parseOffsetData(offsetRawData));
	}
	
	/**
	 * Transforms the specified offset file to match the new expected structure after the debezium and
	 * kafka upgrades in version 1.6
	 * 
	 * @param offset the offset data
	 * @throws IOException
	 */
	public static void transformOffsetIfNecessary(Map<ByteBuffer, ByteBuffer> offset) throws IOException {
		if (offset.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("No existing offset file found, skipping offset transformation check");
			}
			
			return;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ByteBuffer keyByteBuf = offset.keySet().iterator().next();
		ByteBuffer valueByteBuf = offset.get(keyByteBuf);
		JsonNode keyNode = mapper.readValue(keyByteBuf.array(), JsonNode.class);
		if (keyNode.isObject()) {
			log.info("Transforming offset to structure that conforms to the new kafka API");
			
			offset.remove(keyByteBuf);
			byte[] newKeyBytes = mapper.writeValueAsBytes(keyNode.get("payload"));
			offset.put(ByteBuffer.wrap(newKeyBytes), valueByteBuf);
		}
	}
	
	private static Map parseOffsetData(Map<ByteBuffer, ByteBuffer> offsetRawData) throws Exception {
		log.info("Parsing existing offset data");
		if (offsetRawData.isEmpty()) {
			return Collections.emptyMap();
		}
		
		Map parsed = new ObjectMapper().readValue(offsetRawData.values().iterator().next().array(), Map.class);
		log.info("Existing offset data: " + parsed);
		
		return parsed;
	}
	
	private static String getBinlogFile(Map offsetData) {
		Object file = offsetData.get(SenderConstants.OFFSET_PROP_FILE);
		return file != null ? file.toString() : null;
	}
	
}
