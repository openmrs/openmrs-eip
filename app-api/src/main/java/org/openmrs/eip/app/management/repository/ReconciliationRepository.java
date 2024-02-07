package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.Reconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long> {}
