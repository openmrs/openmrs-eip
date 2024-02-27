package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.SyncConstants.OPENMRS_TX_MGR;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation.SenderReconcileStatus;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.management.service.SenderReconcileService;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service("senderReconcileService")
@Profile(SyncProfiles.SENDER)
public class SenderReconcileServiceImpl implements SenderReconcileService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SenderReconcileServiceImpl.class);
	
	private SenderReconcileRepository reconcileRepo;
	
	private SenderTableReconcileRepository tableReconcileRepo;
	
	public SenderReconcileServiceImpl(SenderReconcileRepository reconcileRepo,
	    SenderTableReconcileRepository tableReconcileRepo) {
		this.reconcileRepo = reconcileRepo;
		this.tableReconcileRepo = tableReconcileRepo;
	}
	
	@Override
	@Transactional(readOnly = true, transactionManager = OPENMRS_TX_MGR, isolation = Isolation.SERIALIZABLE)
	public List<SenderTableReconciliation> takeSnapshot() {
		LOG.info("Taking reconciliation snapshot");
		List<SenderTableReconciliation> l = AppUtils.getTablesToSync().stream().map(t -> takeTableSnapshot(t)).toList();
		LOG.info("Done taking reconciliation snapshot");
		return l;
	}
	
	private SenderTableReconciliation takeTableSnapshot(String table) {
		OpenmrsRepository<?> repo = SyncContext.getRepositoryBean(table);
		LocalDateTime snapshotDate = LocalDateTime.now();
		SenderTableReconciliation tableRec = tableReconcileRepo.getByTableNameIgnoreCase(table);
		if (tableRec == null) {
			tableRec = new SenderTableReconciliation();
			tableRec.setTableName(table);
			tableRec.setDateCreated(new Date());
		} else {
			tableRec.setStarted(false);
		}
		
		long count;
		Long endId = repo.getMaxId();
		if (endId == null) {
			//Table is empty, or all rows were deleted after a previous reconciliation, reset
			endId = 0L;
			count = 0;
		} else {
			//TODO Get count where id < endId
			count = repo.count();
		}
		
		tableRec.setRowCount(count);
		tableRec.setEndId(endId);
		tableRec.setLastProcessedId(0);
		tableRec.setSnapshotDate(snapshotDate);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Reconciliation to be done for {} rows in table {}, with id greater than {} up to {}",
			    tableRec.getRowCount(), table, tableRec.getLastProcessedId(), tableRec.getEndId());
		}
		
		return tableRec;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void saveSnapshot(SenderReconciliation reconciliation, List<SenderTableReconciliation> tableReconciliations) {
		if (LOG.isDebugEnabled()) {
			LOG.info("Saving reconciliation snapshot");
		}
		
		tableReconciliations.forEach(tableRec -> tableReconcileRepo.save(tableRec));
		reconciliation.setStatus(SenderReconcileStatus.PROCESSING);
		reconcileRepo.save(reconciliation);
	}
	
}
