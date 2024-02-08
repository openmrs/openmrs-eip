package org.openmrs.eip.app.management.entity.receiver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
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
	@Access(AccessType.FIELD)
	private Collection<TableReconciliation> tableReconciliations;
	
	/**
	 * Gets the tableReconciliations
	 *
	 * @return the tableReconciliations
	 */
	public Collection<TableReconciliation> getTableReconciliations() {
		if (tableReconciliations == null) {
			tableReconciliations = new ArrayList<>();
		}
		
		return tableReconciliations;
	}
	
	/**
	 * Adds a new table TableReconciliation instance
	 * 
	 * @param tableReconciliation the TableReconciliation instance to add
	 */
	public void addTableReconciliation(TableReconciliation tableReconciliation) {
		tableReconciliation.setSiteReconciliation(this);
		getTableReconciliations().add(tableReconciliation);
	}
	
}
