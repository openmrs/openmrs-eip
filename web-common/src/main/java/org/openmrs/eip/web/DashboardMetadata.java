package org.openmrs.eip.web;

import java.util.List;
import java.util.Set;

import org.openmrs.eip.component.SyncOperation;

/**
 * Represents metadata required to load the dashboard
 */
public class DashboardMetadata {
	
	private Set<String> entityClassNames;
	
	private List<SyncOperation> operations;
	
	public DashboardMetadata(Set<String> entityClassNames, List<SyncOperation> operations) {
		this.entityClassNames = entityClassNames;
		this.operations = operations;
	}
	
}
