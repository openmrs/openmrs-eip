package org.openmrs.eip.component.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@MappedSuperclass
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotNull
	@Column(name = "uuid", unique = true)
	private String uuid;
	
	/**
	 * Tests if an entity is out of date compared the given entity
	 * 
	 * @param entity entity to test
	 * @return true if out of date
	 */
	public abstract boolean wasModifiedAfter(BaseEntity entity);
}
