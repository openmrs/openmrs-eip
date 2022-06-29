package org.openmrs.eip.app.management.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SyncResponseModel {
	
	private String messageUuid;
	
	private LocalDateTime dateSent;
	
}
