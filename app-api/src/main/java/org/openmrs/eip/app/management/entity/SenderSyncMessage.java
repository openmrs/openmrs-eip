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
@Table(name = "sender_sync_message")
public class SenderSyncMessage extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	
	public enum SenderSyncMessageStatus {
		NEW,
		SENT
	}
	
	@NotNull
	@Column(name = "table_name", length = 100, nullable = false, updatable = false)
	private String tableName;
	
	@NotNull
	@Column(length = 255, nullable = false, updatable = false)
	private String identifier;
	
	@NotNull
	@Column(length = 1, nullable = false, updatable = false)
	private String operation;
	
	@NotNull
	@Column(name = "message_uuid", length = 38, nullable = false, unique = true, updatable = false)
	private String messageUuid;
	
	@Column(name = "request_uuid", length = 38, updatable = false)
	private String requestUuid;
	
	@NotNull
	@Column(nullable = false, updatable = false)
	private boolean snapshot;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Access(AccessType.FIELD)
	private SenderSyncMessageStatus status = SenderSyncMessageStatus.NEW;
	
	@Column(name = "date_sent")
	@Access(AccessType.FIELD)
	private Date dateSent;
	
	@NotNull
	@Column(name = "event_date", nullable = false)
	private Date eventDate;
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getMessageUuid() {
		return messageUuid;
	}
	
	public void setMessageUuid(String messageUuid) {
		this.messageUuid = messageUuid;
	}
	
	public String getRequestUuid() {
		return requestUuid;
	}
	
	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}
	
	public boolean getSnapshot() {
		return snapshot;
	}
	
	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}
	
	public SenderSyncMessageStatus getStatus() {
		return status;
	}
	
	public Date getDateSent() {
		return dateSent;
	}	
	
	public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void markAsSent() {
		this.status = SenderSyncMessageStatus.SENT;
		this.dateSent = new Date();
	}
	
	@Override
	public String toString() {
		return "SenderSyncMessage [tableName=" + tableName + ", identifier=" + identifier + ", operation=" + operation
		        + ", messageUuid=" + messageUuid + ", requestUuid=" + requestUuid + ", snapshot=" + snapshot + ", status="
		        + status + ", dateSent=" + dateSent + "]";
	}
	
}
