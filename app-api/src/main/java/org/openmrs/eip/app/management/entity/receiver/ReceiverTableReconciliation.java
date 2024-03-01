package org.openmrs.eip.app.management.entity.receiver;

import java.time.LocalDateTime;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "receiver_table_reconcile")
public class ReceiverTableReconciliation extends AbstractEntity {
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_reconcile_id", nullable = false, updatable = false)
	@Getter
	@Setter
	private SiteReconciliation siteReconciliation;
	
	@Column(name = "table_name", nullable = false, updatable = false, length = 100)
	@NotBlank
	@Getter
	@Setter
	private String tableName;
	
	@Column(name = "row_count", nullable = false)
	@Getter
	@Setter
	private long rowCount;
	
	@Column(name = "remote_start_date", nullable = false)
	@NotNull
	@Getter
	@Setter
	private LocalDateTime remoteStartDate;
	
	@Column(name = "processed_count", nullable = false)
	@Getter
	@Setter
	private long processedCount;
	
	@Column(name = "last_batch_received", nullable = false)
	@Getter
	@Setter
	private boolean lastBatchReceived;
	
	@Column(name = "completed", nullable = false)
	@Getter
	@Setter
	private boolean completed;
	
	@Column(name = "date_changed")
	@Getter
	@Setter
	private LocalDateTime dateChanged;
	
}
