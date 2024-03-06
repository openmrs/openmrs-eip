package org.openmrs.eip.app.management.entity.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "receiver_reconcile")
public class ReceiverReconciliation extends AbstractEntity {
	
	public enum ReconciliationStatus {
		NEW, PROCESSING, POST_PROCESSING, COMPLETED
	}
	
	@Column(name = "identifier", nullable = false, updatable = false, unique = true, length = 50)
	@NotBlank
	@Getter
	@Setter
	private String identifier;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Getter
	@Setter
	private ReconciliationStatus status = ReconciliationStatus.NEW;
	
}
