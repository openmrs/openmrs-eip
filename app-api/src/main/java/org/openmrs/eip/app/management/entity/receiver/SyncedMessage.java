package org.openmrs.eip.app.management.entity.receiver;

import java.time.LocalDateTime;
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
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.SyncOperation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "receiver_synced_msg")
@Getter
@Setter
public class SyncedMessage extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	public enum SyncOutcome {
		SUCCESS, ERROR, CONFLICT
	}
	
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
	
	@Column(name = "date_received", nullable = false, updatable = false)
	private Date dateReceived;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "sync_outcome", nullable = false, length = 50)
	@Access(AccessType.FIELD)
	@Setter(AccessLevel.NONE)
	private SyncOutcome outcome;
	
	@Column(name = "response_sent", nullable = false)
	private boolean responseSent = false;
	
	@Column(name = "is_cached", nullable = false, updatable = false)
	private boolean cached = false;
	
	@Column(name = "evicted_from_cache", nullable = false)
	private boolean evictedFromCache = false;
	
	@Column(name = "is_indexed", nullable = false, updatable = false)
	private boolean indexed = false;
	
	@Column(name = "search_index_updated", nullable = false)
	private boolean searchIndexUpdated = false;
	
	public SyncedMessage() {
	}
	
	public SyncedMessage(SyncOutcome outcome) {
		this.outcome = outcome;
	}
	
	@Override
	public String toString() {
		return "{id=" + getId() + ", identifier=" + identifier + ", modelClassName=" + modelClassName + ", operation="
		        + operation + ", snapshot=" + snapshot + ", messageUuid=" + messageUuid + ", outcome=" + outcome
		        + ", responseSent=" + responseSent + ", cached=" + cached + ", evictedFromCache=" + evictedFromCache
		        + ", indexed=" + indexed + ", searchIndexUpdated=" + searchIndexUpdated + "}";
	}
	
}
