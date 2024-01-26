package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiverSyncRequestRepository extends JpaRepository<ReceiverSyncRequest, Long> {
	
	ReceiverSyncRequest findByRequestUuid(String requestUuid);
	
}
