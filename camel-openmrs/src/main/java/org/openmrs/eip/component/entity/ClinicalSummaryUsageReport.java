package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "clinical_summary_usage_report")
@AttributeOverride(name = "id", column = @Column(name = "clinical_summary_usage_report_id"))
public class ClinicalSummaryUsageReport extends BaseChangeableDataEntity {
	
	@Column(name = "report")
	private String report;
	
	@Column(name = "health_facility")
	private String healthFacility;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "confidential_terms")
	private String confidentialTerms;
	
	@Column(name = "app_version")
	private String appversion;
	
	@Column(name = "date_opened")
	private LocalDateTime dateOpened;
	
}
