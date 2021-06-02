package org.openmrs.eip.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dashboard {
	
	private static final Map<String, Object> ENTRIES = new ConcurrentHashMap();
	
	/**
	 * Gets the entries
	 *
	 * @return the entries
	 */
	public Map<String, Object> getEntries() {
		return ENTRIES;
	}
	
	/**
	 * Associates the specified value with the specified key in the dashboard
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void add(String key, Object value) {
		ENTRIES.put(key, value);
	}
	
}
