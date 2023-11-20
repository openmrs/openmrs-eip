package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.common.Address;
import org.openmrs.eip.component.entity.light.LocationLight;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "location")
@AttributeOverride(name = "id", column = @Column(name = "location_id"))
public class Location extends BaseChangeableMetaDataEntity {
	
	@NotNull
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
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
	
	@ManyToOne
	@JoinColumn(name = "parent_location")
	private LocationLight parentLocation;
}
