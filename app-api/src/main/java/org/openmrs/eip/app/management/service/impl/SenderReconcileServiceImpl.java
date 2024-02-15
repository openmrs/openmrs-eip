package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.OPENMRS_TX_MGR;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.management.service.SenderReconcileService;
import org.openmrs.eip.app.sender.ReconcileSnapshot;
import org.openmrs.eip.app.sender.ReconcileSnapshot.TableSnapshot;
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
	
	private SenderTableReconcileRepository tableReconcileRepo;
	
	public SenderReconcileServiceImpl(SenderTableReconcileRepository tableReconcileRepo) {
		this.tableReconcileRepo = tableReconcileRepo;
	}
	
	@Override
	@Transactional(readOnly = true, transactionManager = OPENMRS_TX_MGR, isolation = Isolation.SERIALIZABLE)
	public ReconcileSnapshot takeSnapshot() {
		LOG.info("Taking reconciliation snapshot");
		List<TableSnapshot> snapshots = AppUtils.getTablesToSync().stream().map(t -> takeTableSnapshot(t)).toList();
		
		LOG.info("Done taking reconciliation snapshot");
		return new ReconcileSnapshot(snapshots);
	}
	
	private TableSnapshot takeTableSnapshot(String table) {
		OpenmrsRepository<?> repo = SyncContext.getRepositoryBean(table);
		LocalDateTime timeTaken = LocalDateTime.now();
		final long maxId = repo.getMaxId();
		final long count = repo.count();
		SenderTableReconciliation tableRec = tableReconcileRepo.getByTableName(table);
		long lastProcessedId = 0;
		if (tableRec != null) {
			lastProcessedId = tableRec.getLastProcessedId();
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Reconciliation to be done for {} rows in table {}, from row after that with id {} to {}", count,
			    table, lastProcessedId, maxId);
		}
		
		return new TableSnapshot(table, count, maxId, lastProcessedId, timeTaken);
	}
	
}
