package org.openmrs.eip.deindentification;

import java.lang.reflect.Field;

/**
 * Encapsulates de-indentification metadata about a single sync entity class
 */
public interface DeIdentifyClassMetadata {
	
	boolean isRequired(Field field);
	
	boolean isUnique(Field field);
	
	boolean deIndentify(Field field);
	
}
