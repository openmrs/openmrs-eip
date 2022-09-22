package org.openmrs.eip.app.sender;

import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SenderUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(SenderUtils.class);
	
	/**
	 * Generates a mask for the specified value
	 * 
	 * @param value the value to mask
	 * @param <T>
	 * @return the masked value
	 */
	public static <T> T mask(T value) {
		if (value == null) {
			if (log.isDebugEnabled()) {
				log.debug("Skipping masking for a null value");
			}
			
			return null;
		}
		
		Object masked;
		if (String.class.isAssignableFrom(value.getClass())) {
			masked = SenderConstants.MASK;
		} else {
			throw new EIPException("Don't know how mask a value of type: " + value.getClass());
		}
		
		return (T) masked;
	}
	
}
