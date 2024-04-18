package org.openmrs.eip.web.receiver;

import java.util.Map;

import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
import org.openmrs.eip.app.management.repository.SiteReconciliationRepository;
import org.openmrs.eip.app.management.service.ReceiverReconcileService;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.web.RestConstants;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile(SyncProfiles.RECEIVER)
@RequestMapping(RestConstants.PATH_RECEIVER_RECONCILE)
public class ReconcileController {
	
	private ReceiverReconcileRepository reconcileRepo;
	
	private ReceiverReconcileService reconcileService;
	
	private SiteReconciliationRepository siteRecRepo;
	
	public ReconcileController(ReceiverReconcileRepository reconcileRepo, ReceiverReconcileService reconcileService,
	    SiteReconciliationRepository siteRecRepo) {
		this.reconcileRepo = reconcileRepo;
		this.reconcileService = reconcileService;
		this.siteRecRepo = siteRecRepo;
	}
	
	@GetMapping
	public ReceiverReconciliation getReconciliation() {
		return reconcileRepo.getReconciliation();
	}
	
	@PostMapping
	public ReceiverReconciliation startReconciliation() {
		return reconcileService.addNewReconciliation();
	}
	
	@GetMapping("/" + RestConstants.PROGRESS)
	public Map<String, Long> getProgress() {
		long completedSiteCount = siteRecRepo.countByDateCompletedNotNull();
		long totalCount = siteRecRepo.count();
		return Map.of("completedSiteCount", completedSiteCount, "totalCount", totalCount);
	}
	
}
