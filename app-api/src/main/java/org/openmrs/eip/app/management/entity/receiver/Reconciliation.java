package org.openmrs.eip.app.management.entity.receiver;

import java.time.LocalDateTime;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reconciliation")
public class Reconciliation extends AbstractEntity {
	
	@Column(name = "identifier", nullable = false, updatable = false, unique = true, length = 50)
	@NotBlank
	@Getter
	@Setter
	private String identifier;
	
	@Column(name = "started", nullable = false)
	@Getter
	@Setter
	private boolean started;
	
	@Column(name = "date_completed")
	@Getter
	@Setter
	private LocalDateTime dateCompleted;
	
}
