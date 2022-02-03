package org.openmrs.eip.component.management.hash.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseHashEntity implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, updatable = false)
	private String identifier;
	
	@Column(nullable = false)
	private String hash;
	
	@Column(name = "date_created", nullable = false, updatable = false)
	private LocalDateTime dateCreated;
	
	@Column(name = "date_changed")
	private LocalDateTime dateChanged;
	
}
