package org.openmrs.eip.app.management.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SenderSyncResponseModel {
	
	private String messageUuid;
	
	private LocalDateTime dateSent;
	
}
