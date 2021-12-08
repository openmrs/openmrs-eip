package org.openmrs.eip.app.management.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
	
	@NotNull
	@Column(name = "model_class_name", nullable = false, updatable = false)
	private String modelClassName;
	
	@NotNull
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@NotNull
	@Column(nullable = false, unique = true, updatable = false, length = 38)
	private String uuid;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private Resolution resolution;
	
	@Column(name = "date_changed")
	private Date dateChanged;
	
}
