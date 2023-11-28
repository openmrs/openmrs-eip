package org.openmrs.eip.app;

import org.openmrs.eip.component.SyncOperation;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter between {@link SyncOperation} to a string.
 */
@Converter
public class SyncOperationConverter implements AttributeConverter<SyncOperation, String> {
	
	@Override
	public String convertToDatabaseColumn(SyncOperation attribute) {
		return attribute.name();
	}
	
	@Override
	public SyncOperation convertToEntityAttribute(String dbData) {
		return SyncOperation.valueOf(dbData);
	}
	
}
