package org.openmrs.eip.app.management.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Base class for sync request classes for the Sender and Receiver
 */
@MappedSuperclass
@Data
public abstract class BaseSyncRequest extends AbstractEntity {
	
	public enum Resolution {
		PENDING, FOUND, NOT_FOUND
	}
	
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@Column(name = "table_name", nullable = false, updatable = false)
	private String tableName;
	
	@NotNull
	@Column(length = 38, nullable = false, unique = true, updatable = false)
	private String uuid;
	
	@NotNull
	@Column(nullable = false)
	private Resolution resolution;
	
}
