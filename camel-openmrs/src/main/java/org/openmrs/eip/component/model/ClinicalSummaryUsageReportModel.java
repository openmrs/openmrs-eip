package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClinicalSummaryUsageReportModel extends BaseChangeableDataModel {
	
	private String report;
	
	private String healthFacility;
	
	private String username;
	
	private String confidentialTerms;
	
	private String appversion;
	
	private LocalDate dateOpened;
	
}
