package org.openmrs.sync.common.model.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConceptAttributeModel extends AttributeModel {

    private String conceptClassUuid;

    private String conceptDatatypeUuid;
}
