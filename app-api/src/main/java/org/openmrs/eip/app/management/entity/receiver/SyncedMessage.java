package org.openmrs.eip.app.management.entity.receiver;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncOperation;
import org.springframework.beans.BeanUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "receiver_synced_msg")
@Data
public class SyncedMessage extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@Column(name = "entity_payload", columnDefinition = "text", nullable = false)
	private String entityPayload;
	
	@Column(name = "model_class_name", nullable = false, updatable = false)
	private String modelClassName;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false, length = 1)
	private SyncOperation operation;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false)
	private SiteInfo site;
	
	@Column(name = "is_snapshot", nullable = false, updatable = false)
	private Boolean snapshot = false;
	
	@Column(name = "message_uuid", length = 38, updatable = false)
	private String messageUuid;
	
	@NotNull
	@Column(name = "date_sent_by_sender", nullable = false, updatable = false)
	private LocalDateTime dateSentBySender;
	
	@Column(name = "date_received", updatable = false)
	private Date dateReceived;
	
	@Column(name = "is_itemized", nullable = false)
	private boolean itemized;
	
	@OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Collection<PostSyncAction> actions;
	
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
	
	/**
	 * Gets the postSyncActions
	 *
	 * @return the postSyncActions
	 */
	public Collection<PostSyncAction> getActions() {
		if (actions == null) {
			actions = new LinkedHashSet();
		}
		
		return actions;
	}
	
	/**
	 * Adds the specified {@link PostSyncAction}
	 *
	 * @param action the {@link PostSyncAction} to add
	 */
	public void addAction(PostSyncAction action) {
		getActions().add(action);
		action.setMessage(this);
	}
	
	/**
	 * Checks whether this entity is ready for archiving i.e. it is already itemized and all its
	 * {@link PostSyncAction} items have been processed successfully
	 *
	 * @return true if ready for archiving otherwise false
	 */
	public boolean readyForArchive() {
		return isItemized() && getActions().stream().allMatch(a -> a.isCompleted());
	}
	
	@Override
	public String toString() {
		return "{id=" + getId() + ", identifier=" + identifier + ", modelClassName=" + modelClassName + ", operation="
		        + operation + ", snapshot=" + snapshot + ", messageUuid=" + messageUuid + ", itemized=" + itemized
		        + ", actions=" + actions + "}";
	}
	
}
