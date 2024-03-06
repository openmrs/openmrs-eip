package org.openmrs.eip.app.management.entity.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reconcile_table_summary")
public class ReconcileTableSummary extends AbstractEntity {
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "reconcile_id", nullable = false, updatable = false)
	@Getter
	@Setter
	private ReceiverReconciliation reconciliation;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false)
	@Getter
	@Setter
	private SiteInfo site;
	
	@Column(name = "table_name", nullable = false, updatable = false, length = 100)
	@NotBlank
	@Getter
	@Setter
	private String tableName;
	
	@Column(name = "missing_count", nullable = false, updatable = false)
	@Getter
	@Setter
	private long missingCount;
	
	@Column(name = "missing_sync_count", nullable = false, updatable = false)
	@Getter
	@Setter
	private long missingSyncCount;
	
	@Column(name = "missing_error_count", nullable = false, updatable = false)
	@Getter
	@Setter
	private long missingErrorCount;
	
	@Column(name = "undeleted_count", nullable = false, updatable = false)
	@Getter
	@Setter
	private long undeletedCount;
	
	@Column(name = "undeleted_sync_count", nullable = false, updatable = false)
	@Getter
	@Setter
	private long undeletedSyncCount;
	
	@Column(name = "undeleted_error_count", nullable = false, updatable = false)
	@Getter
	@Setter
	private long undeletedErrorCount;
	
}
