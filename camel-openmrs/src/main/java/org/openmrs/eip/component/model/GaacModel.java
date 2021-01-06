package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class GaacModel extends BaseChangeableDataModel {
	private String name;
	private String description;
	protected LocalDateTime startDate;
	protected LocalDateTime endDate;
	private String focalPatientUuid;
	private Integer affinityType;
	private String locationUuid;
	private Integer crumbled;
	private String reasonCrumbled;
	protected LocalDateTime dateCrumbled;	    
}
