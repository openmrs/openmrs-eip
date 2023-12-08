package org.openmrs.eip.component.model;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ClinicalSummaryUsageReportModel extends BaseChangeableDataModel {
	
	private String report;
	
	private String healthFacility;
	
	private String username;
	
	private String confidentialTerms;
	
	private String appversion;
	
	private LocalDate dateOpened;
	
}
