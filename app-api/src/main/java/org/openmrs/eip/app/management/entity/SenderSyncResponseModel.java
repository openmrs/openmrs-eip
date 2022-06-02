package org.openmrs.eip.app.management.entity;

import java.util.Date;

import lombok.Data;

@Data
public class SenderSyncResponseModel {
	
	private String messageUuid;
	
	private Date dateSent;
	
}
