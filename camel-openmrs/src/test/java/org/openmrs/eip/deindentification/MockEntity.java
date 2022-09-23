package org.openmrs.eip.deindentification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.BaseCreatableEntity;
import org.openmrs.eip.component.entity.BaseEntity;

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
