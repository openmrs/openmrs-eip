package org.openmrs.eip.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public final class RestConstants {
	
	public static final String API_PATH = "/api/";
	
	public static final String PATH_LOGIN = "/login";
	
	public static final String PATH_VAR_ID = "id";
	
	public static final String SUB_PATH_RECEIVER = API_PATH + "dbsync/receiver/";
	
	public static final String PATH_RECEIVER_SYNC_MSG = SUB_PATH_RECEIVER + "sync";
	
	public static final String PATH_RECEIVER_SYNCED_MSG = SUB_PATH_RECEIVER + "synced";
	
	public static final String PATH_RECEIVER_ARCHIVE = SUB_PATH_RECEIVER + "archive";
	
	public static final String RES_RECEIVER_CONFLICT = SUB_PATH_RECEIVER + "conflict";
	
	public static final String RES_RECEIVER_CONFLICT_BY_ID = SUB_PATH_RECEIVER + "conflict/{" + PATH_VAR_ID + "}";
	
	public static final String ACTION_DIFF = "/{" + PATH_VAR_ID + "}/diff";
	
	public static final String PATH_RECEIVER_CONFLICT_DIFF = RES_RECEIVER_CONFLICT + ACTION_DIFF;
	
	public static final String ACTION_RESOLVE = "/{" + PATH_VAR_ID + "}/resolve";
	
	public static final String PATH_RECEIVER_CONFLICT_RESOLVE = RES_RECEIVER_CONFLICT + ACTION_RESOLVE;
	
	public static final String PARAM_GRP_PROP = "groupProperty";
	
	public static final String PARAM_START_DATE = "startDate";
	
	public static final String PARAM_END_DATE = "endDate";
	
	public static final String FIELD_COUNT = "count";
	
	public static final String FIELD_ITEMS = "items";
	
	public static final Integer DEFAULT_MAX_COUNT = 500;
	
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
	
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(RestConstants.DATE_PATTERN);
	
}
