package org.openmrs.eip.component.entity.light;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
