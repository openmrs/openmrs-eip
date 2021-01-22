package org.openmrs.eip.app;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

/**
 * Holds contextual information for the application
 */
public final class EipContext {
	
	private Map<String, TableMetadata> tableNameMetadataMap = new HashedMap();
	
}
