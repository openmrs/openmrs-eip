package org.openmrs.eip.app.management.entity.sender;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "deleted_entity")
public class DeletedEntity extends AbstractEntity {
	
	@Column(name = "table_name", nullable = false, updatable = false, length = 100)
	@NotBlank
	@Getter
	@Setter
	private String tableName;
	
	@NotBlank
	@Column(nullable = false, updatable = false)
	@Getter
	@Setter
	private String identifier;
	
	@Column(name = "primary_key_id", nullable = false, updatable = false)
	@Getter
	@Setter
	private String primaryKeyId;
	
}
