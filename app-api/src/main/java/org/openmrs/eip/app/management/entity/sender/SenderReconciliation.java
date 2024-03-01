
package org.openmrs.eip.app.management.entity.sender;

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
@Table(name = "sender_reconcile")
public class SenderReconciliation extends AbstractEntity {
	
	public enum SenderReconcileStatus {
		NEW, PROCESSING, POST_PROCESSING, COMPLETED
	}
	
	@Column(name = "identifier", nullable = false, length = 50)
	@NotBlank
	@Getter
	@Setter
	private String identifier;
	
	@Column(name = "batch_size", nullable = false)
	@Getter
	@Setter
	private int batchSize;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Getter
	@Setter
	private SenderReconcileStatus status = SenderReconcileStatus.NEW;
	
}
