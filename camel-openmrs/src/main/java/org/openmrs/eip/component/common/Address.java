package org.openmrs.eip.component.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;

@Data
@EqualsAndHashCode
@Embeddable
public class Address {

    private String address1;

    private String address2;

    private String address3;

    private String address4;

    private String address5;

    private String address6;

    private String address7;

    private String address8;

    private String address9;

    private String address10;

    private String address11;

    private String address12;

    private String address13;

    private String address14;

    private String address15;

    private String cityVillage;

    private String stateProvince;

    private String postalCode;

    private String country;

    private String latitude;

    private String longitude;

    private String countyDistrict;
}
