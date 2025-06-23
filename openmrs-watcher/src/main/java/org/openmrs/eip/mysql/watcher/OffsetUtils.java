package org.openmrs.eip.mysql.watcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OffsetUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(OffsetUtils.class);
	
	/**
	 * Fixes the specified data from an existing offset file to match the Kafka API.
	 * 
	 * @param offset the offset data
	 * @throws IOException
	 */
	public static void transformOffsetIfNecessary(Map<ByteBuffer, ByteBuffer> offset) throws IOException {
		if (offset.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Skip transforming offset because no existing offset file was found");
			}
			
			return;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ByteBuffer keyByteBuf = offset.keySet().iterator().next();
		ByteBuffer valueByteBuf = offset.get(keyByteBuf);
		JsonNode keyNode = mapper.readValue(keyByteBuf.array(), JsonNode.class);
		if (keyNode.isObject()) {
			LOG.info("Transforming offset to be compatible with the new kafka API");
			
			offset.remove(keyByteBuf);
			byte[] newKeyBytes = mapper.writeValueAsBytes(keyNode.get("payload"));
			offset.put(ByteBuffer.wrap(newKeyBytes), valueByteBuf);
		}
	}
	
}
