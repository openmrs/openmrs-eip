package org.openmrs.sync.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VisitAttributeModel extends AttributeModel {

    private String patientUuid;

    private String visitTypeUuid;
}
