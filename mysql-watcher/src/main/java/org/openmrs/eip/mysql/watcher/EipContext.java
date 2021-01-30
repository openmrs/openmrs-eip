package org.openmrs.eip.mysql.watcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds contextual information for the application
 */
public final class EipContext {
	
	private Map<String, TableMetadata> tableNameMetadataMap = new HashMap();
	
}
