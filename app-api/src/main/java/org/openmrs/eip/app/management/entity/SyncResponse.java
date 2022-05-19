package org.openmrs.eip.app.management.entity;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sender_sync_response")
public class SyncResponse extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	public enum SyncResponseStatus {
		NEW,
		PROCESSED
	}
	
	@NotNull
	@Column(name = "message_uuid", length = 38, nullable = false, updatable = false)
	@Access(AccessType.FIELD)
	private String messageUuid;
	
	@NotNull
	@Column(name = "date_sent", nullable = false, updatable = false)
	@Access(AccessType.FIELD)
	private Date dateSent;
	
	@Column(name = "date_processed")
	@Access(AccessType.FIELD)
	private Date dateProcessed;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Access(AccessType.FIELD)
	private SyncResponseStatus status;
	
	public SyncResponse() {
	}
	
	public SyncResponse(@NotNull String messageUuid, @NotNull Date dateSent) {
		this.messageUuid = messageUuid;
		this.dateSent = dateSent;
		this.status = SyncResponseStatus.NEW;
		super.setDateCreated(new Date());
	}
	
	public String getMessageUuid() {
		return messageUuid;
	}
	
	public Date getDateSent() {
		return dateSent;
	}
	
	public Date getDateProcessed() {
		return dateProcessed;
	}
	
	public SyncResponseStatus getStatus() {
		return status;
	}
	
	public void markAsProcessed() {
		this.status = SyncResponseStatus.PROCESSED;
		this.dateProcessed = new Date();
	}
	
	@Override
	public String toString() {
		return "SyncResponse [messageUuid=" + messageUuid + ", dateSent=" + dateSent + ", dateProcessed=" + dateProcessed
		        + ", status=" + status + "]";
	}
	
}
