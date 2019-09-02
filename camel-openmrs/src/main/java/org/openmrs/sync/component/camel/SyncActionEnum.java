package org.openmrs.sync.component.camel;

import org.apache.camel.Producer;
import org.openmrs.sync.component.exception.OpenMrsSyncException;

import java.util.Arrays;

public enum SyncActionEnum {
    EXTRACT(OpenMrsExtractProducer.class),
    LOAD(OpenMrsLoadProducer.class);

    private Class<? extends Producer> producerClass;

    SyncActionEnum(final Class<? extends Producer> producerClass) {
        this.producerClass = producerClass;
    }

    public Class<? extends Producer> getProducerClass() {
        return producerClass;
    }

    public static SyncActionEnum getAction(final String actionString) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(actionString))
                .findFirst()
                .orElseThrow(() -> new OpenMrsSyncException("No action found with name: " + actionString));
    }
}
