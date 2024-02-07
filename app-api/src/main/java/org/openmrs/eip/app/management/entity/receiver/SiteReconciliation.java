package org.openmrs.eip.app.management.entity.receiver;

import java.time.LocalDateTime;
import java.util.Collection;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mgt_site_reconciliation")
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
	
	@OneToMany(mappedBy = "siteReconciliation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Getter
	@Setter
	private Collection<TableReconciliation> tableReconciliations;
	
}
