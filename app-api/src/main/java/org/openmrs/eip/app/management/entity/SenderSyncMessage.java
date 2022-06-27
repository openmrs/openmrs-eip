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
	
	// TODO add ACKNOWLEDGED status, which will be set after successful processing of the receiver response
	public enum SenderSyncMessageStatus {
		NEW,
		SENT,
		ERROR
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
	@Column(nullable = false, length = 20)
	@Access(AccessType.FIELD)
	private SenderSyncMessageStatus status = SenderSyncMessageStatus.NEW;
	
	@Column(name = "date_changed")
	@Access(AccessType.FIELD)
	private Date dateChanged;
	
	@Column(name = "error_message", length = 200)
	@Access(AccessType.FIELD)
	private String errorMessage;
	
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
	
	public boolean isSnapshot() {
		return snapshot;
	}
	
	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}
	
	public SenderSyncMessageStatus getStatus() {
		return status;
	}
	
	public Date getDateChanged() {
		return dateChanged;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void markAsSent() {
		this.status = SenderSyncMessageStatus.SENT;
		this.dateChanged = new Date();
	}
	
	public void markAsError(@NotNull String errorMessage) {
		this.status = SenderSyncMessageStatus.ERROR;
		this.dateChanged = new Date();
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return "SenderSyncMessage [tableName=" + tableName + ", identifier=" + identifier + ", operation=" + operation
		        + ", messageUuid=" + messageUuid + ", requestUuid=" + requestUuid + ", snapshot=" + snapshot + ", status="
		        + status + ", dateChanged=" + dateChanged + ", errorMessage=" + errorMessage + "]";
	}
	
}
