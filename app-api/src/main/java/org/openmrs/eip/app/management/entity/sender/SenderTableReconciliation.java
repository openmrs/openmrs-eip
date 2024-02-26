package org.openmrs.eip.app.management.entity.sender;

import java.time.LocalDateTime;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mgt_table_reconciliation")
public class SenderTableReconciliation extends AbstractEntity {

	@Column(name = "table_name", nullable = false, updatable = false, length = 100)
	@NotBlank
	@Getter
	@Setter
	private String tableName;

	@Column(name = "row_count", nullable = false)
	@Getter
	@Setter
	private long rowCount;

	@Column(name = "last_processed_id", nullable = false)
	@Getter
	@Setter
	private long lastProcessedId;

	@Column(name = "end_id", nullable = false)
	@Getter
	@Setter
	private long endId;

	@Column(name = "snapshot_date", nullable = false)
	@NotNull
	@Getter
	@Setter
	private LocalDateTime snapshotDate;

	@Column(name = "is_started", nullable = false)
	@Getter
	@Setter
	private boolean started;

	/**
	 * Convenience method to check if this table reconciliation is completed.
	 *
	 * @return true of completed otherwise false
	 */
	public boolean isCompleted() {
		return started && lastProcessedId == endId;
	}

}
