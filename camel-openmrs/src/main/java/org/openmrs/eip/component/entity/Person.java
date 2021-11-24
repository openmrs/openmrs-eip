package org.openmrs.eip.component.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.openmrs.eip.component.entity.light.ConceptLight;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
public class Person extends BaseChangeableDataEntity {
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "birthdate")
	private LocalDate birthdate;
	
	@NotNull
	@Column(name = "birthdate_estimated")
	private boolean birthdateEstimated;
	
	@NotNull
	@Column(name = "dead")
	private boolean dead;
	
	@Column(name = "death_date")
	private LocalDate deathDate;
	
	@ManyToOne
	@JoinColumn(name = "cause_of_death")
	private ConceptLight causeOfDeath;
	
	@NotNull
	@Column(name = "deathdate_estimated")
	private boolean deathdateEstimated;
	
	@Column(name = "birthtime")
	private LocalTime birthtime;
	
	@Column(name = "cause_of_death_non_coded")
	private String causeOfDeathNonCoded;
}
