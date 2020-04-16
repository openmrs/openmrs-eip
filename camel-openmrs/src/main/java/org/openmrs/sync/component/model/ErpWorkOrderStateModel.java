package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ErpWorkOrderStateModel extends BaseModel {

    private String action;

    private String erpWorkOrderUuid;

}
