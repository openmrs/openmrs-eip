package org.openmrs.eip.app;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Holds contextual information for the application
 */
public final class EipContext {

    private Map<String, TableMetadata> tableNameMetadataMap = new HashedMap();

}
