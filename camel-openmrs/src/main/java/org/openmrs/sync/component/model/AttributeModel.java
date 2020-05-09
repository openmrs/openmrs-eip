package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AttributeModel extends BaseChangeableDataModel {

    private String referencedEntityUuid;

    private String attributeTypeUuid;

    private String valueReference;
}
