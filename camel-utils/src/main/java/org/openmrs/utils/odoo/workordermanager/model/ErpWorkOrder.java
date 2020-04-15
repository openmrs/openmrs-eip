package org.openmrs.utils.odoo.workordermanager.model;

import org.openmrs.utils.odoo.IdDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
public class ErpWorkOrder {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("state")
    private ErpWorkOrderStateEnum state;

    @JsonProperty("next_work_order_id")
    @JsonDeserialize(using = IdDeserializer.class)
    private Integer nextWorkOrderId;

    @JsonProperty("workcenter_id")
    @JsonDeserialize(using = IdDeserializer.class)
    private Integer workCenterId;
}
