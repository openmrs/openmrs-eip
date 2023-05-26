package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConflictRepository extends JpaRepository<ConflictQueueItem, Long> {}
