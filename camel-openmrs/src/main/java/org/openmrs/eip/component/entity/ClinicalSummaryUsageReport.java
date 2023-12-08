package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
