package org.openmrs.eip.app.management.entity.receiver;

import java.util.Collection;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reconciliation")
public class Reconciliation extends AbstractEntity {
	
	public enum ReconciliationStatus {
		NEW, PROCESSING, COMPLETED
	}
	
	@Column(name = "identifier", nullable = false, updatable = false, unique = true, length = 50)
	@NotBlank
	@Getter
	@Setter
	private String identifier;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 50)
	@NotNull
	@Getter
	@Setter
	private ReconciliationStatus status;
	
	@OneToMany(mappedBy = "reconciliation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Getter
	@Setter
	private Collection<SiteReconciliation> siteReconciliations;
	
}
