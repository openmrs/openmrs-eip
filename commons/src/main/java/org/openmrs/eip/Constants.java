package org.openmrs.eip;

public class Constants {
	
	public static final String OPENMRS_DATASOURCE_NAME = "openmrsDataSource";
	
	public static final String LIQUIBASE_BEAN_NAME = "springLiquibase";
	
	public static final String COMMON_PROP_SOURCE_BEAN_NAME = "commonPropSource";
	
	public static final String PROP_PACKAGES_TO_SCAN = "jpaPackagesToScan";
	
	public static final String MGT_DATASOURCE_NAME = "mngtDataSource";
	
	public static final String HTTP_HEADER_AUTH = "Authorization";
	
	public static final String PROP_WATCHED_TABLES = "eip.watchedTables";
	
	public static final String MGT_TX_MGR_NAME = "mngtTransactionManager";
	
	public static final String EX_PROP_RESOURCE_NAME = "resourceName";
	
	public static final String EX_PROP_RESOURCE_ID = "resourceId";
	
	public static final String EX_PROP_SUB_RESOURCE_NAME = "subResourceName";
	
	public static final String EX_PROP_SUB_RESOURCE_ID = "subResourceId";
	
	public static final String EX_PROP_RESOURCE_REP = "resourceRepresentation";
	
	public static final String EX_PROP_IS_SUB_RESOURCE = "isSubResource";
	
	public static final String EX_PROP_SOURCE = "conceptSource";
	
	public static final String EX_PROP_CODE = "conceptCode";
	
	public static final String ROUTE_ID_GET_CONCEPT_BY_MAPPING = "get-concept-by-mapping-from-openmrs";
	
	public static final String URI_GET_CONCEPT_BY_MAPPING = "direct:" + ROUTE_ID_GET_CONCEPT_BY_MAPPING;
	
}
