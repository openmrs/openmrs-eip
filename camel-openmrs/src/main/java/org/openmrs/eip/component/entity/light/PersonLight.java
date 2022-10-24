package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
public class PersonLight extends VoidableLightEntity {
	
	@NotNull
	@Column(name = "dead")
	private boolean dead;
	
	@NotNull
	@Column(name = "birthdate_estimated")
	private boolean birthdateEstimated;
	
	@NotNull
	@Column(name = "deathdate_estimated")
	private boolean deathdateEstimated;
	
}
