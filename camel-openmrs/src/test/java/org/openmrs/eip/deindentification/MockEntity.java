package org.openmrs.eip.deindentification;

import org.openmrs.eip.component.entity.BaseCreatableEntity;
import org.openmrs.eip.component.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "mock")
public class MockEntity extends BaseCreatableEntity {
	
	@NotNull
	@Column(unique = true)
	private String identifier;
	
	@Column(nullable = false)
	private String name;
	
	@Column(length = 1)
	private String gender;
	
	@Column(name = "birth_date")
	private String birthdate;
	
	@Column
	private String address;
	
	@Override
	public boolean wasModifiedAfter(BaseEntity entity) {
		return false;
	}
	
}
