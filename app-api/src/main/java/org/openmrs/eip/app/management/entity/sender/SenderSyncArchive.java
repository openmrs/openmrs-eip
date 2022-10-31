package org.openmrs.eip.app.management.entity.sender;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "sender_sync_archive")
public class SenderSyncArchive extends AbstractEntity {
	
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
	@Column(name = "message_uuid", length = 38, nullable = false, updatable = false)
	private String messageUuid;
	
	@Column(name = "request_uuid", length = 38, updatable = false)
	private String requestUuid;
	
	@NotNull
	@Column(nullable = false, updatable = false)
	private boolean snapshot;
	
	@Column(name = "sync_data", columnDefinition = "text")
	private String data;
	
	@NotNull
	@Column(name = "date_sent", nullable = false, updatable = false)
	private Date dateSent;
	
	@Column(name = "event_date", updatable = false)
	private Date eventDate;
	
	@NotNull
	@Column(name = "date_received_by_receiver", nullable = false, updatable = false)
	private LocalDateTime dateReceivedByReceiver;
	
	public SenderSyncArchive() {
	}
	
	public SenderSyncArchive(SenderSyncMessage syncMessage) {
		BeanUtils.copyProperties(syncMessage, this, "id", "dateCreated");
	}
	
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
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public Date getDateSent() {
		return dateSent;
	}
	
	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}
	
	public Date getEventDate() {
		return eventDate;
	}
	
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	public LocalDateTime getDateReceivedByReceiver() {
		return dateReceivedByReceiver;
	}
	
	public void setDateReceivedByReceiver(LocalDateTime dateReceivedByReceiver) {
		this.dateReceivedByReceiver = dateReceivedByReceiver;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " {id=" + getId() + ", tableName=" + tableName + ", identifier=" + identifier
		        + ", operation=" + operation + ", messageUuid=" + messageUuid + ", requestUuid=" + requestUuid
		        + ", snapshot=" + snapshot + ", dateSent=" + dateSent + ", dateReceivedByReceiver=" + dateReceivedByReceiver
		        + "}";
	}
	
}
