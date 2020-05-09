package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.common.Address;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonAddressModel extends BaseChangeableDataModel {

    private String personUuid;

    private boolean preferred;

    private Address address;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
