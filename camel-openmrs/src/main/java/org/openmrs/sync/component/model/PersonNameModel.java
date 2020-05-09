package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonNameModel extends BaseChangeableDataModel {

    private boolean preferred;

    private String personUuid;

    private String prefix;

    private String givenName;

    private String middleName;

    private String familyNamePrefix;

    private String familyName;

    private String familyName2;

    private String familyNameSuffix2;

    private String degree;
}
