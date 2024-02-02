package org.openmrs.eip.component.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.SyncModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JsonUtils {
	
	private static final ObjectMapper MAPPER;
	
	static {
		MAPPER = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(new LocalDateSerializer());
		module.addSerializer(new LocalDateTimeSerializer());
		module.addDeserializer(BaseModel.class, new BaseModelDeserializer());
		module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
		MAPPER.registerModule(module);
	}
	
	private JsonUtils() {
	}
	
	/**
	 * Utility method to marshal an object to JSON
	 * 
	 * @param object
	 * @return the object as a JSON string
	 */
	public static String marshall(final Object object) {
		try {
			return MAPPER.writeValueAsString(object);
		}
		catch (JsonProcessingException e) {
			log.error("Error while marshalling object", e);
			throw new EIPException("Error while marshalling object", e);
		}
	}
	
	/**
	 * Marshals an object to a byte array
	 * 
	 * @param object the object to marshal
	 * @return byte array
	 */
	public static byte[] marshalToBytes(Object object) {
		try {
			return MAPPER.writeValueAsBytes(object);
		}
		catch (IOException e) {
			throw new EIPException("Error while marshalling object to bytes", e);
		}
	}
	
	/**
	 * Utility method to unmarshal a JSON string
	 * 
	 * @param json
	 * @param objectClass
	 * @param <C>
	 * @return the object
	 */
	public static <C> C unmarshal(final String json, final Class<C> objectClass) {
		try {
			return MAPPER.readValue(json, objectClass);
		}
		catch (IOException e) {
			log.error("Error while unmarshalling json", e);
			throw new EIPException("Error while unmarshalling json", e);
		}
	}
	
	/**
	 * Unmarshalls the specified byte array
	 *
	 * @param data the data to unmarshal
	 * @param objectClass the class to unmarshal to
	 * @return the object
	 */
	public static <C> C unmarshalBytes(byte[] data, Class<C> objectClass) {
		try {
			return MAPPER.readValue(data, objectClass);
		}
		catch (IOException e) {
			log.error("Error while unmarshalling bytes", e);
			throw new EIPException("Error while unmarshalling bytes", e);
		}
	}
	
	/**
	 * Utility method to unmarshal a JSON string representing a SyncModel object
	 *
	 * @param json
	 * @return the SyncModel object
	 */
	public static SyncModel unmarshalSyncModel(String json) {
		return unmarshal(json, SyncModel.class);
	}
	
}
