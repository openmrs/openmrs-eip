package org.openmrs.eip.component.mapper.operations;

import lombok.Getter;
import org.openmrs.eip.component.entity.light.LightEntity;
import org.openmrs.eip.component.exception.EIPException;

@Getter
public class DecomposedUuid {
	
	private Class<? extends LightEntity> entityType;
	
	private String uuid;
	
	public DecomposedUuid(final String entityTypeName, final String uuid) {
		this.entityType = convertToClass(entityTypeName);
		this.uuid = uuid;
	}
	
	private Class<? extends LightEntity> convertToClass(final String entityTypeName) {
		try {
			return (Class<? extends LightEntity>) Class.forName(entityTypeName);
		}
		catch (ClassNotFoundException e) {
			throw new EIPException("No entity class exists with the name " + entityTypeName, e);
		}
	}
}
