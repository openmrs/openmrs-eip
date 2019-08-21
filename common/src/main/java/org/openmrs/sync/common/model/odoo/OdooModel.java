package org.openmrs.sync.common.model.odoo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class OdooModel {

    private String type;

    private Map<String, String> data;

    public void addValue(final String key,
                         final String value) {
        if (data == null) {
            data = new HashMap<>();
        }

        data.put(key, value);
    }
}
