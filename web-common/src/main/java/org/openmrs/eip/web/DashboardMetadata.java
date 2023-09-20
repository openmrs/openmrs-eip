package org.openmrs.eip.web;

import java.util.List;
import java.util.Set;

import org.openmrs.eip.component.SyncOperation;

/**
 * Represents metadata required to load the dashboard
 */
public class DashboardMetadata {
	
	private Set<String> groups;
	
	private List<SyncOperation> operations;
	
	public DashboardMetadata(Set<String> groups, List<SyncOperation> operations) {
		this.groups = groups;
		this.operations = operations;
	}
	
}
