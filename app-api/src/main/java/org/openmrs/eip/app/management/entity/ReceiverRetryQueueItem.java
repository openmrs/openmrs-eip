package org.openmrs.eip.app.management.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "receiver_retry_queue")
public class ReceiverRetryQueueItem extends BaseRetryQueueItem {
	
	public static final long serialVersionUID = 1;
	
	@Column(name = "model_class_name", nullable = false, updatable = false)
	private String modelClassName;
	
	//Unique identifier for the entity usually a uuid or name for an entity like a privilege that has no uuid
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@Column(name = "entity_payload", columnDefinition = "text", nullable = false)
	private String entityPayload;
	
	@ManyToOne
	@JoinColumn(name = "site_id", updatable = false)
	private SiteInfo site;
	
	@NotNull
	@Column(name = "date_sent_by_sender", nullable = false, updatable = false)
	private LocalDateTime dateSentBySender;
	
	/**
	 * Gets the modelClassName
	 *
	 * @return the modelClassName
	 */
	public String getModelClassName() {
		return modelClassName;
	}
	
	/**
	 * Sets the modelClassName
	 *
	 * @param modelClassName the modelClassName to set
	 */
	public void setModelClassName(String modelClassName) {
		this.modelClassName = modelClassName;
	}
	
	/**
	 * Gets the identifier
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Sets the identifier
	 *
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Gets the entityPayload
	 *
	 * @return the entityPayload
	 */
	public String getEntityPayload() {
		return entityPayload;
	}
	
	/**
	 * Sets the entityPayload
	 *
	 * @param entityPayload the entityPayload to set
	 */
	public void setEntityPayload(String entityPayload) {
		this.entityPayload = entityPayload;
	}
	
	/**
	 * Gets the site
	 *
	 * @return the site
	 */
	public SiteInfo getSite() {
		return site;
	}
	
	/**
	 * Sets the site
	 *
	 * @param site the site to set
	 */
	public void setSite(SiteInfo site) {
		this.site = site;
	}
	
	/**
	 * Gets the dateSentBySender
	 *
	 * @return the dateSentBySender
	 */
	public LocalDateTime getDateSentBySender() {
		return dateSentBySender;
	}
	
	/**
	 * Sets the dateSentBySender
	 *
	 * @param dateSentBySender the dateSentBySender to set
	 */
	public void setDateSentBySender(LocalDateTime dateSentBySender) {
		this.dateSentBySender = dateSentBySender;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " {identifier=" + identifier + ", modelClassName=" + modelClassName
		        + ", attemptCount=" + getAttemptCount() + ", site=" + site + ", dateSentBySender=" + dateSentBySender + "}";
	}
	
}
