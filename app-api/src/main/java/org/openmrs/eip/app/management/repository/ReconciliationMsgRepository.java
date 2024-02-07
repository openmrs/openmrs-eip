package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationMsgRepository extends JpaRepository<ReconciliationMessage, Long> {}
