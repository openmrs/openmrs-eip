package org.openmrs.eip.app.management.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sender_sync_response")
public class SenderSyncResponse extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	@NotNull
	@Column(name = "message_uuid", length = 38, nullable = false, updatable = false)
	private String messageUuid;
	
	@NotNull
	@Column(name = "date_sent", nullable = false, updatable = false)
	private LocalDateTime dateSent;
	
	public String getMessageUuid() {
		return messageUuid;
	}
	
	public void setMessageUuid(String messageUuid) {
		this.messageUuid = messageUuid;
	}
	
	public LocalDateTime getDateSent() {
		return dateSent;
	}
	
	public void setDateSent(LocalDateTime dateSent) {
		this.dateSent = dateSent;
	}
	
	@Override
	public String toString() {
		return "SyncResponse [messageUuid=" + messageUuid + ", dateSent=" + dateSent + "]";
	}
	
}
