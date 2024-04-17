package org.openmrs.eip.web.receiver;

import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
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
	
	public ReconcileController(ReceiverReconcileRepository reconcileRepo, ReceiverReconcileService reconcileService) {
		this.reconcileRepo = reconcileRepo;
		this.reconcileService = reconcileService;
	}
	
	@GetMapping
	public ReceiverReconciliation getReconciliation() {
		return reconcileRepo.getReconciliation();
	}
	
	@PostMapping
	public ReceiverReconciliation startReconciliation() {
		return reconcileService.addNewReconciliation();
	}
	
}
