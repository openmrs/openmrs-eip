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
	@Column(name = "date_sent_by_receiver", nullable = false, updatable = false)
	private LocalDateTime dateSentByReceiver;
	
	@NotNull
	@Column(name = "date_received_by_receiver", nullable = false, updatable = false)
	private LocalDateTime dateReceivedByReceiver;
	
	public String getMessageUuid() {
		return messageUuid;
	}
	
	public void setMessageUuid(String messageUuid) {
		this.messageUuid = messageUuid;
	}
	
	public LocalDateTime getDateReceivedByReceiver() {
		return dateReceivedByReceiver;
	}
	
	public void setDateReceivedByReceiver(LocalDateTime dateReceivedByReceiver) {
		this.dateReceivedByReceiver = dateReceivedByReceiver;
	}
	
	public LocalDateTime getDateSentByReceiver() {
		return dateSentByReceiver;
	}
	
	public void setDateSentByReceiver(LocalDateTime dateSentByReceiver) {
		this.dateSentByReceiver = dateSentByReceiver;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " {messageUuid=" + messageUuid + ", dateSentByReceiver=" + dateSentByReceiver
		        + ", dateReceivedByReceiver=" + dateReceivedByReceiver + "}";
	}
	
}
