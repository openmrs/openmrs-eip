package org.openmrs.eip.app.management.entity.receiver;

import java.time.LocalDateTime;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "site_reconcile")
public class SiteReconciliation extends AbstractEntity {
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false, unique = true)
	@Getter
	@Setter
	private SiteInfo site;
	
	@Column(name = "date_completed")
	@Getter
	@Setter
	private LocalDateTime dateCompleted;
	
}
