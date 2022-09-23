package org.openmrs.eip.deindentification;

import java.lang.reflect.Field;

/**
 * Encapsulates de-indentification metadata about a single sync entity class
 */
public interface DeIdentifyClassMetadata {
	
	/**
	 * Checks if the specified Field's value should be de-identified
	 * 
	 * @param field the {@link Field} object
	 * @return true if the value should be de-identified otherwise false
	 */
	boolean deIndentify(Field field);
	
	/**
	 * Checks if the specified Field's value is required
	 *
	 * @param field the {@link Field} object
	 * @return true if a value is required otherwise false
	 */
	boolean isRequired(Field field);
	
	/**
	 * Checks if the specified Field's value should be unique
	 *
	 * @param field the {@link Field} object
	 * @return true if the value should be unique otherwise false
	 */
	boolean isUnique(Field field);
	
	/**
	 * Gets the column length of the specified Field
	 *
	 * @param field the {@link Field} object
	 * @return the column length
	 */
	Integer getLength(Field field);
	
}
