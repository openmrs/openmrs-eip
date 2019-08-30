package org.openmrs.sync.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.entity.light.LocationLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "location")
@AttributeOverride(name = "id", column = @Column(name = "location_id"))
public class Location extends MetaDataEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "address3")
    private String address3;

    @Column(name = "address4")
    private String address4;

    @Column(name = "address5")
    private String address5;

    @Column(name = "address6")
    private String address6;

    @Column(name = "address7")
    private String address7;

    @Column(name = "address8")
    private String address8;

    @Column(name = "address9")
    private String address9;

    @Column(name = "address10")
    private String address10;

    @Column(name = "address11")
    private String address11;

    @Column(name = "address12")
    private String address12;

    @Column(name = "address13")
    private String address13;

    @Column(name = "address14")
    private String address14;

    @Column(name = "address15")
    private String address15;

    @Column(name = "city_village")
    private String cityVillage;

    @Column(name = "state_province")
    private String stateProvince;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "county_district")
    private String countyDistrict;

    @ManyToOne
    @JoinColumn(name = "parent_location")
    private LocationLight parentLocation;
}
