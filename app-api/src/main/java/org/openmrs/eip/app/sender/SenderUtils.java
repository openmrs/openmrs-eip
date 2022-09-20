package org.openmrs.eip.app.sender;

import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SenderUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(SenderUtils.class);
	
	/**
	 * Generates a mask for the specified object
	 * 
	 * @param object the object to mask
	 * @param <T>
	 * @return the masked value
	 */
	public static <T> T mask(T object) {
		if (object == null) {
			return null;
		}
		
		Object masked;
		if (String.class.isAssignableFrom(object.getClass())) {
			masked = SenderConstants.MASK;
		} else {
			throw new EIPException("Don't know how mask an object of type: " + object.getClass());
		}
		
		return (T) masked;
	}
	
}
