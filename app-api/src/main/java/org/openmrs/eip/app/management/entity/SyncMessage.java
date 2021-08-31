package org.openmrs.eip.app.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Encapsulates info about a sync message received by the receiver
 */
@Entity
@Table(name = "receiver_sync_msg")
public class SyncMessage extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	//Unique identifier for the entity usually a uuid or name for an entity like a privilege that has no uuid
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@Column(name = "entity_payload", columnDefinition = "text", nullable = false)
	private String entityPayload;
	
	@Column(name = "model_class_name", nullable = false, updatable = false)
	private String modelClassName;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false)
	private SiteInfo site;
	
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
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " {id=" + getId() + ", identifier=" + identifier + ", modelClassName="
		        + modelClassName + ", site=" + site + "}";
	}
	
}
