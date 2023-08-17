package org.openmrs.eip.app.receiver;

import lombok.Getter;

/**
 * Encapsulates conflict resolutions details for a single field for an entity
 */
public class PropertyConflictResolution {
	
	/**
	 * The name of the property associated to this resolution
	 */
	@Getter
	private String propertyName;
	
	/**
	 * Specifies if the new value should be ignored or synced
	 */
	@Getter
	private boolean ignoreNewValue;
	
	public PropertyConflictResolution(String propertyName, boolean ignoreNewValue) {
		this.propertyName = propertyName;
		this.ignoreNewValue = ignoreNewValue;
	}
	
}
