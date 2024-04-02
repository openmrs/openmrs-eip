package org.openmrs.eip.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {
	
	public static final String OPENMRS_ROOT_PGK = "org.openmrs";
	
	public final static String MGT_ENTITY_MGR = "mngtEntityManager";
	
	public final static String MGT_TX_MGR = "mngtTransactionManager";
	
	public static final String HASH_DELETED = "DELETED";
	
	public static final String PROP_OPENMRS_USER = "openmrs.username";
	
	public static final String PROP_OPENMRS_DB_HOST = "openmrs.db.host";
	
	public static final String PROP_OPENMRS_DB_PORT = "openmrs.db.port";
	
	public static final String PROP_IGNORE_MISSING_HASH = "receiver.ignore.missing.hash.for.existing.entity";
	
	public static final String EX_PROP_EXCEPTION = "org.openmrs.eip.exception";
	
	public static final String OPENMRS_DATASOURCE_NAME = "openmrsDataSource";
	
	public static final String PROP_URI_ERROR_HANDLER = "uri.error.handler";
	
	public static final List<String> ORDER_SUBCLASS_TABLES = Arrays.asList("test_order", "drug_order");
	
	public static final List<String> SUBCLASS_TABLES;
	
	public final static String CUSTOM_PROP_SOURCE_BEAN_NAME = "customPropSource";
	
	public static final String ZIP_ENTRY_NAME = "data";
	
	static {
		List<String> subclassTables = new ArrayList();
		subclassTables.add("patient");
		subclassTables.addAll(ORDER_SUBCLASS_TABLES);
		SUBCLASS_TABLES = Collections.unmodifiableList(subclassTables);
	}
}
