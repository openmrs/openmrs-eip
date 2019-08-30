package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VisitAttributeModel extends AttributeModel {

    private String patientUuid;

    private String visitTypeUuid;
}
