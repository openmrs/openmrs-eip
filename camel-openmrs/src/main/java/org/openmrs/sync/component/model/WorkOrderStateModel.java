package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkOrderStateModel extends BaseModel {

    private String Uuid;

    private String action;

    private String workOrderUuid;

}
