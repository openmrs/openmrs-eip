package org.openmrs.eip.app.receiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates conflict resolutions details a single entity
 */
public class EntityConflictResolution {
	
	private final List<PropertyConflictResolution> propertyResolutions = new ArrayList<>();
	
	/**
	 * Adds a resolution for a single entity property
	 * 
	 * @param propertyName the name of the property
	 * @param ignoreNewValue Specifies if the new value should be ignored or synced
	 */
	public void addPropertyResolution(String propertyName, boolean ignoreNewValue) {
		propertyResolutions.add(new PropertyConflictResolution(propertyName, ignoreNewValue));
	}
	
}
