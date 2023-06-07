package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderRetryRepository extends JpaRepository<SenderRetryQueueItem, Long> {}
