package org.openmrs.eip.app.management.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseHashEntity extends AbstractEntity {
	
	@Column(nullable = false, unique = true, updatable = false)
	private String identifier;
	
	@Column(nullable = false)
	private String hash;
	
	@Column(name = "date_changed")
	private LocalDateTime dateChanged;
	
}
