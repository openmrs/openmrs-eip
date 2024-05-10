package org.openmrs.eip.web.sender;

import java.util.List;

import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation.SenderReconcileStatus;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile(SyncProfiles.SENDER)
@RequestMapping(SenderRestConstants.PATH_SENDER_RECONCILE)
public class SenderReconcileController {
	
	private static final Logger LOG = LoggerFactory.getLogger(SenderReconcileController.class);
	
	private SenderReconcileRepository reconcileRepo;
	
	private SenderTableReconcileRepository tableRecRepo;
	
	public SenderReconcileController(SenderReconcileRepository reconcileRepo, SenderTableReconcileRepository tableRecRepo) {
		this.reconcileRepo = reconcileRepo;
		this.tableRecRepo = tableRecRepo;
	}
	
	@GetMapping
	public SenderReconciliation getReconciliation() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting active reconciliation");
		}
		
		return reconcileRepo.getReconciliation();
	}
	
	@GetMapping("/" + RestConstants.TABLE_RECONCILE)
	public List<SenderTableReconciliation> getIncompleteTableReconciliations() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting incomplete table reconciliations");
		}
		
		return tableRecRepo.getIncompleteReconciliations();
	}
	
	@GetMapping("/" + RestConstants.RECONCILE_HISTORY)
	public List<SenderReconciliation> getHistory() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting most recent reconciliation history");
		}
		
		return reconcileRepo.getTop3ByStatusOrderByDateCreatedDesc(SenderReconcileStatus.COMPLETED);
	}
	
}
