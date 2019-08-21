package org.openmrs.sync.odoo.service;

import org.openmrs.sync.odoo.exeption.OdooException;

import java.util.Arrays;

public enum OdooModelEnum {
    PARTNER("CUSTOMER", "res.partner");

    private String incomingType;

    private String odooModelName;

    private OdooModelEnum(final String incomingType,
                          final String odooModelName) {
        this.incomingType = incomingType;
        this.odooModelName = odooModelName;
    }

    public String getIncomingType() {
        return incomingType;
    }

    public String getOdooModelName() {
        return odooModelName;
    }

    public static OdooModelEnum getOdooModelEnum(final String incomingType) {
        return Arrays.stream(values())
                .filter(e -> e.getIncomingType().equals(incomingType))
                .findFirst()
                .orElseThrow(() -> new OdooException("No Odoo model exists for incoming model " + incomingType));
    }
}
