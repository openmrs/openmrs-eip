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
public class TableReconciliation extends AbstractEntity {
	
	@Column(name = "table_name", nullable = false, updatable = false, length = 100)
	@NotBlank
	@Getter
	@Setter
	private String tableName;
	
	@Column(name = "row_count", nullable = false)
	@Getter
	@Setter
	private long rowCount;
	
	@Column(name = "start_id", nullable = false)
	@Getter
	@Setter
	private long startId;
	
	@Column(name = "end_id", nullable = false)
	@Getter
	@Setter
	private long endId;
	
	@Column(name = "start_date", nullable = false)
	@NotNull
	@Getter
	@Setter
	private LocalDateTime startDate;
	
	@Column(name = "last_processed_id")
	@Getter
	@Setter
	private Long lastProcessedId;
	
}
