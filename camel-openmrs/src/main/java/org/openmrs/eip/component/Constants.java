package org.openmrs.eip.component;

public class Constants {
	
	public static final String PLACEHOLDER_CLASS = "[class]";
	
	public static final String QUERY_SAVE_HASH = "jpa:" + PLACEHOLDER_CLASS;
	
	public static final String PLACEHOLDER_UUID = "[uuid]";
	
	public static final String QUERY_GET_HASH = "jpa:" + PLACEHOLDER_CLASS + "?query=SELECT h from " + PLACEHOLDER_CLASS
	        + " h WHERE h.identifier='" + PLACEHOLDER_UUID + "'";
	
	public static final String HASH_DELETED = "DELETED";
	
	public static final String VALUE_SITE_SEPARATOR = "|";
	
	public static final String DEFAULT_RETIRE_REASON = "Retired because it was deleted in the site of origin";
	
	public static final String PROP_OPENMRS_USER = "openmrs.username";
	
}
