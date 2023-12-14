package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.ClinicalSummaryUsageReport;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.ClinicalSummaryUsageReportModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ClinicalSummaryUsageReportService extends AbstractEntityService<ClinicalSummaryUsageReport, ClinicalSummaryUsageReportModel> {
	
	public ClinicalSummaryUsageReportService(final SyncEntityRepository<ClinicalSummaryUsageReport> repository,
	    final EntityToModelMapper<ClinicalSummaryUsageReport, ClinicalSummaryUsageReportModel> entityToModelMapper,
	    final ModelToEntityMapper<ClinicalSummaryUsageReportModel, ClinicalSummaryUsageReport> modelToEntityMapper) {
		
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.CLINICALSUMMARY_USAGE_REPORT;
	}
	
}
