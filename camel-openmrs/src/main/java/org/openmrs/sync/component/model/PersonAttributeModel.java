package org.openmrs.sync.component.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonAttributeModel extends BaseChangeableDataModel {

    private String value;

    private String personUuid;

    private String personAttributeTypeUuid;
}
