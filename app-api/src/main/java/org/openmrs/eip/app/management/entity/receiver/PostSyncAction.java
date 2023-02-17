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

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Table(name = "receiver_post_sync_action")
@Data
public class PostSyncAction extends AbstractEntity {
	
	public enum PostSyncActionStatus {
		NEW, SUCCESS, FAILURE
	}
	
	public enum PostSyncActionType {
		SEND_RESPONSE, CACHE_EVICT, SEARCH_INDEX_UPDATE
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Access(AccessType.FIELD)
	@Setter(AccessLevel.NONE)
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
	@Setter(AccessLevel.NONE)
	private Date dateProcessed;
	
	@Column(name = "status_msg", length = 1024)
	@Setter(AccessLevel.NONE)
	private String statusMessage;
	
	/**
	 * Checks if this action is completed successfully with no errors or not
	 * 
	 * @return true for a successfully completed action otherwise false
	 */
	public boolean isCompleted() {
		return getStatus() == PostSyncActionStatus.SUCCESS;
	}
	
	/**
	 * Marks this action as successfully processed
	 */
	public void markAsCompleted() {
		updateStatus(PostSyncActionStatus.SUCCESS);
		this.statusMessage = null;
	}
	
	/**
	 * Marks this action as processed with a failure
	 */
	public void markAsProcessedWithError(String statusMessage) {
		updateStatus(PostSyncActionStatus.FAILURE);
		this.statusMessage = statusMessage;
	}
	
	@Override
	public String toString() {
		return "{actionType=" + actionType + ", status=" + status + "}";
	}
	
	private void updateStatus(PostSyncActionStatus status) {
		this.status = status;
		this.dateProcessed = new Date();
	}
	
}
