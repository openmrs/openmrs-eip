package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.common.Address;
import org.openmrs.eip.component.entity.light.PersonLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person_address")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "id", column = @Column(name = "person_address_id"))
public class PersonAddress extends BaseChangeableDataEntity {
	
	@ManyToOne
	@JoinColumn(name = "person_id")
	private PersonLight person;
	
	@NotNull
	@Column(name = "preferred")
	private boolean preferred;
	
	@Embedded
	@AttributeOverride(name = "address1", column = @Column(name = "address1"))
	@AttributeOverride(name = "address2", column = @Column(name = "address2"))
	@AttributeOverride(name = "address3", column = @Column(name = "address3"))
	@AttributeOverride(name = "address4", column = @Column(name = "address4"))
	@AttributeOverride(name = "address5", column = @Column(name = "address5"))
	@AttributeOverride(name = "address6", column = @Column(name = "address6"))
	@AttributeOverride(name = "address7", column = @Column(name = "address7"))
	@AttributeOverride(name = "address8", column = @Column(name = "address8"))
	@AttributeOverride(name = "address9", column = @Column(name = "address9"))
	@AttributeOverride(name = "address10", column = @Column(name = "address10"))
	@AttributeOverride(name = "address11", column = @Column(name = "address11"))
	@AttributeOverride(name = "address12", column = @Column(name = "address12"))
	@AttributeOverride(name = "address13", column = @Column(name = "address13"))
	@AttributeOverride(name = "address14", column = @Column(name = "address14"))
	@AttributeOverride(name = "address15", column = @Column(name = "address15"))
	@AttributeOverride(name = "cityVillage", column = @Column(name = "city_village"))
	@AttributeOverride(name = "stateProvince", column = @Column(name = "state_province"))
	@AttributeOverride(name = "postalCode", column = @Column(name = "postal_code"))
	@AttributeOverride(name = "country", column = @Column(name = "country"))
	@AttributeOverride(name = "latitude", column = @Column(name = "latitude"))
	@AttributeOverride(name = "longitude", column = @Column(name = "longitude"))
	@AttributeOverride(name = "countyDistrict", column = @Column(name = "county_district"))
	private Address address;
	
	@Column(name = "start_date")
	private LocalDateTime startDate;
	
	@Column(name = "end_date")
	private LocalDateTime endDate;
}
