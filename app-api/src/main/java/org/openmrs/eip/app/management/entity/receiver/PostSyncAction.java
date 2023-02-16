package org.openmrs.eip.app.management.entity.receiver;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import lombok.Data;

@Entity
@Table(name = "receiver_post_sync_action")
@Data
public class PostSyncAction extends AbstractEntity {
	
	public enum PostSyncActionStatus {
		NEW, PASS, FAIL
	}
	
	public enum PostSyncActionType {
		SEND_RESPONSE, CACHE_EVICT, SEARCH_INDEX_UPDATE
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Access(AccessType.FIELD)
	private PostSyncActionStatus status = PostSyncActionStatus.NEW;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "action_type", nullable = false, updatable = false, length = 50)
	@Access(AccessType.FIELD)
	private PostSyncActionType actionType;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "msg_id", nullable = false, updatable = false)
	private SyncedMessage message;
	
	@Column(name = "date_processed")
	private Date dateProcessed;
	
	@Column(name = "date_changed")
	private Date dateChanged;
	
	@Column(name = "error_msg", length = 1024)
	private String errorMessage;
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " {id=" + getId() + ", status=" + status + ", actionType=" + actionType
		        + ", dateProcessed=" + dateProcessed + ", dateChanged=" + dateChanged + ", messageUuid="
		        + message.getMessageUuid() + ", errorMessage=" + errorMessage + "}";
	}
	
}
