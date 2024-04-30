package org.openmrs.eip.web.sender;

import org.openmrs.eip.web.RestConstants;

public class SenderRestConstants {
	
	public static final String RES_SENDER_DASHBOARD = RestConstants.RES_DASHBOARD + "/sender";
	
	public static final String PATH_NAME_COUNT_BY_STATUS = "/countByStatus";
	
	public static final String PATH_COUNT_BY_STATUS = RES_SENDER_DASHBOARD + PATH_NAME_COUNT_BY_STATUS;
	
	public static final String PATH_NAME_ERR_DETAILS = "/errorDetails";
	
	public static final String PATH_ERR_DETAILS = RES_SENDER_DASHBOARD + PATH_NAME_ERR_DETAILS;
	
	public static final String SUB_PATH_SENDER = RestConstants.SUB_PATH_DB_SYNC + "sender/";
	
	public static final String PATH_SENDER_RECONCILE = SUB_PATH_SENDER + "reconcile";
	
	public static final String PATH_REC_TABLE_RECONCILE = PATH_SENDER_RECONCILE + "/" + RestConstants.TABLE_RECONCILE;
	
}
