package org.openmrs.eip;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AppContext implements ApplicationContextAware {
	
	private static ApplicationContext appContext;
	
	private static final Map<String, Object> EIP_CONTEXT = new ConcurrentHashMap();
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appContext = applicationContext;
	}
	
	/**
	 * Gets the bean matching the specified type from the application context
	 *
	 * @return an instance of the bean matching the specified type
	 */
	public static <T> T getBean(Class<T> clazz) {
		return appContext.getBean(clazz);
	}
	
	/**
	 * Retrieves a value from the EIP cache associated to the specified key name, the key MUST be
	 * unique, recommended way to guarantee uniqueness is to prefix all keys with a unique route id
	 * 
	 * @param key the unique
	 * @return the value associated to the key
	 */
	public static Object get(String key) {
		return EIP_CONTEXT.get(key);
	}
	
	/**
	 * Adds the specified value to the EIP cache if none already exists for the same key otherwise
	 * updates the existing value
	 * 
	 * @param key the unique
	 * @param value the value to set
	 * @return the previous value associated with key, or null if there was no mapping for the key
	 */
	public static Object add(String key, Object value) {
		return EIP_CONTEXT.put(key, value);
	}
	
	/**
	 * Removes the value for specified key from the EIP cache
	 * 
	 * @return the previous value associated with key, or null if there was no mapping for key.
	 */
	public static Object remove(String key) {
		return EIP_CONTEXT.remove(key);
	}
	
}
