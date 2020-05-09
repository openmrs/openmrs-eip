package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.common.Address;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocationModel extends BaseChangeableMetadataModel {

    private String name;

    private String description;

    private Address address;

    private String parentLocationUuid;
}
