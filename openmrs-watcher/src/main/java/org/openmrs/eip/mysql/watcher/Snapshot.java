package org.openmrs.eip.mysql.watcher;

/**
 * Enumeration for snapshot values used by debezium
 */
public enum Snapshot {
	
	TRUE("true"),
	
	FALSE("false"),
	
	LAST("last");
	
	//Raw value as represented by debezium
	private String rawValue;
	
	Snapshot(String rawValue) {
		this.rawValue = rawValue;
	}
	
	/**
	 * Gets the rawValue
	 *
	 * @return the rawValue
	 */
	public String getRawValue() {
		return rawValue;
	}
	
}
