package org.openmrs.eip.mysql.watcher.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.openmrs.eip.app.management.entity.AbstractEntity;

//@Entity
@Table(name = "sender_identifier_mapping")
public class IdentifierMapping extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	@Column(name = "entity_type", nullable = false)
	private String entityType;
	
	@Column(name = "openmrs_identifier", nullable = false)
	private String openmrsIdentifier;
	
	@Column(name = "external_id", nullable = false)
	private String externalId;
	
	@ManyToOne
	@JoinColumn(name = "app_id", nullable = false)
	private App app;
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " {entityType=" + entityType + ", openmrsIdentifier=" + openmrsIdentifier
		        + ", externalId=" + externalId + ", app=" + app + "}";
	}
	
}
