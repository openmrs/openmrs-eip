package org.openmrs.eip.app.management.entity.receiver;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncOperation;
import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "receiver_synced_msg")
@Getter
@Setter
@DynamicUpdate
public class SyncedMessage extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	@NotNull
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@NotNull
	@Column(name = "entity_payload", columnDefinition = "text", nullable = false)
	private String entityPayload;
	
	@NotNull
	@Column(name = "model_class_name", nullable = false, updatable = false)
	private String modelClassName;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false, length = 1)
	private SyncOperation operation;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false)
	private SiteInfo site;
	
	@NotNull
	@Column(name = "is_snapshot", nullable = false, updatable = false)
	private Boolean snapshot = false;
	
	@Column(name = "message_uuid", length = 38, updatable = false)
	private String messageUuid;
	
	@NotNull
	@Column(name = "date_sent_by_sender", nullable = false, updatable = false)
	private LocalDateTime dateSentBySender;
	
	@Column(name = "date_received", updatable = false)
	private Date dateReceived;
	
	@Column(name = "response_sent", nullable = false)
	private boolean responseSent = false;
	
	@Column(name = "is_itemized", nullable = false)
	private boolean itemized = false;
	
	@Column(name = "is_cached")
	private Boolean cached;
	
	@Column(name = "evicted_from_cache")
	private Boolean evictedFromCache;
	
	@Column(name = "is_indexed")
	private Boolean indexed;
	
	@Column(name = "search_index_updated")
	private Boolean searchIndexUpdated;
	
	public SyncedMessage() {
	}
	
	public SyncedMessage(SyncMessage syncMessage) {
		BeanUtils.copyProperties(syncMessage, this, "id", "dateCreated");
		setDateReceived(syncMessage.getDateCreated());
	}
	
	public SyncedMessage(ReceiverRetryQueueItem retry) {
		BeanUtils.copyProperties(retry, this, "id", "dateCreated");
	}
	
	public SyncedMessage(ConflictQueueItem conflict) {
		BeanUtils.copyProperties(conflict, this, "id", "dateCreated");
	}
	
	@Override
	public String toString() {
		return "{id=" + getId() + ", identifier=" + identifier + ", modelClassName=" + modelClassName + ", operation="
		        + operation + ", snapshot=" + snapshot + ", messageUuid=" + messageUuid + ", responseSent=" + responseSent
		        + ", itemized=" + itemized + ", cached=" + cached + ", evictedFromCache=" + evictedFromCache + ", indexed="
		        + indexed + ", searchIndexUpdated=" + searchIndexUpdated + "}";
	}
	
}
