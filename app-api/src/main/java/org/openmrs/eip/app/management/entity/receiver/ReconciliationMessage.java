
package org.openmrs.eip.app.management.entity.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mgt_reconciliation_msg")
public class ReconciliationMessage extends AbstractEntity {
	
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
	
	@Column(name = "batch_size", nullable = false, updatable = false)
	@Getter
	@Setter
	private Integer batchSize;
	
	@Column(name = "last_table_batch", nullable = false, updatable = false)
	@Getter
	@Setter
	private boolean lastTableBatch;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(columnDefinition = "mediumtext", nullable = false, updatable = false)
	@NotNull
	@Getter
	@Setter
	private String data;
	
	@Column(name = "processed_count", nullable = false)
	@Getter
	@Setter
	private int processedCount;
	
	/**
	 * Checks whether this message has been processed completely.
	 * 
	 * @return true if completed otherwise false
	 */
	public boolean isCompleted() {
		return getBatchSize() == getProcessedCount();
	}
	
}
