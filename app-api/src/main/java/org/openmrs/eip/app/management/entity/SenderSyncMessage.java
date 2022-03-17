package org.openmrs.eip.app.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sender_sync_message")
public class SenderSyncMessage extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	
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
	
	@Column(name = "request_uuid", length = 38, unique = true, updatable = false)
	private String requestUuid;
	
	@NotNull
	@Column(length = 1, nullable = false)
	private boolean sent;
	
	@NotNull
	@Column(length = 1, nullable = false, updatable = false)
	private boolean snapshot;
	
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
	
	public boolean isSent() {
		return sent;
	}
	
	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
	public boolean isSnapshot() {
		return snapshot;
	}
	
	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}
	
	@Override
	public String toString() {
		return "SenderSyncMessage [tableName=" + tableName + ", identifier=" + identifier + ", operation=" + operation
		        + ", messageUuid=" + messageUuid + ", requestUuid=" + requestUuid + ", sent=" + sent + ", snapshot="
		        + snapshot + "]";
	}
	
}
