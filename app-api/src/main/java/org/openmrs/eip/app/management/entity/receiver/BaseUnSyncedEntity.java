package org.openmrs.eip.app.management.entity.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class BaseUnSyncedEntity extends AbstractEntity {
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false)
	@Getter
	@Setter
	private SiteInfo site;
	
	@Column(name = "table_name", nullable = false, updatable = false, length = 100)
	@NotBlank
	@Getter
	@Setter
	private String tableName;
	
	@NotBlank
	@Column(nullable = false, updatable = false)
	@Getter
	@Setter
	private String identifier;
	
	@Column(name = "in_sync_queue", nullable = false)
	@Getter
	@Setter
	private boolean inSyncQueue;
	
	@Column(name = "in_error_queue", nullable = false)
	@Getter
	@Setter
	private boolean inErrorQueue;
	
}
