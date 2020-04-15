package org.openmrs.utils.odoo.workordermanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ErpWorkOrderStateEnum {
    @JsonProperty("pending")
    PENDING("pending"),
    @JsonProperty("ready")
    READY("ready"),
    @JsonProperty("progress")
    PROGRESS("progress"),
    @JsonProperty("skip")
    SKIP("skip"),
    @JsonProperty("done")
    DONE("done"),
    @JsonProperty("cancel")
    CANCEL("cancel");

    private String odooValue;

    ErpWorkOrderStateEnum(final String odooValue) {
        this.odooValue = odooValue;
    }

    public String getOdooValue() {
        return odooValue;
    }
}
