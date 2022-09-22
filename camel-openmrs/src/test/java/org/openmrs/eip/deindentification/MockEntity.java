package org.openmrs.eip.deindentification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.BaseCreatableEntity;
import org.openmrs.eip.component.entity.BaseEntity;

@Entity
@Table(name = "mock")
public class MockEntity extends BaseCreatableEntity {
	
	@NotNull
	@Column(nullable = false, unique = true)
	private String identifier;
	
	@NotNull
	@Column(nullable = false)
	private String name;
	
	@Column
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
