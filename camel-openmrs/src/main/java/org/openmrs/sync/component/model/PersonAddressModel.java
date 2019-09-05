package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonAddressModel extends BaseModel {

    private String personUuid;

    private boolean preferred;

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

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
