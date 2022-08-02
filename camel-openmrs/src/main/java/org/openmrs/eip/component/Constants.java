package org.openmrs.eip.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {
	
	public static final String PLACEHOLDER_CLASS = "[class]";
	
	public static final String QUERY_SAVE_HASH = "jpa:" + PLACEHOLDER_CLASS;
	
	public static final String PLACEHOLDER_UUID = "[uuid]";
	
	public static final String QUERY_GET_HASH = "jpa:" + PLACEHOLDER_CLASS + "?query=SELECT h from " + PLACEHOLDER_CLASS
	        + " h WHERE h.identifier='" + PLACEHOLDER_UUID + "'";
	
	public static final String HASH_DELETED = "DELETED";
	
	public static final String VALUE_SITE_SEPARATOR = "-";
	
	public static final String DEFAULT_RETIRE_REASON = "Retired because it was deleted in the site of origin";

    public static final String PROP_OPENMRS_URL = "openmrs.baseUrl";
	
	public static final String PROP_OPENMRS_USER = "openmrs.username";

    public static final String PROP_OPENMRS_PASS = "openmrs.password";
	
	public static final String PROP_IGNORE_MISSING_HASH = "receiver.ignore.missing.hash.for.existing.entity";
	
	public static final String OPENMRS_DATASOURCE_NAME = "openmrsDataSource";
	
	public static final String DAEMON_USER_UUID = "A4F30A1B-5EB9-11DF-A648-37A07F9C90FB";
	
	public static final String PROP_URI_ERROR_HANDLER = "uri.error.handler";
	
	public static final List<String> ORDER_SUBCLASS_TABLES = Arrays.asList("test_order", "drug_order");
	
	public static final List<String> SUBCLASS_TABLES;
	
	static {
		List<String> subclassTables = new ArrayList();
		subclassTables.add("patient");
		subclassTables.addAll(ORDER_SUBCLASS_TABLES);
		SUBCLASS_TABLES = Collections.unmodifiableList(subclassTables);
	}
}
