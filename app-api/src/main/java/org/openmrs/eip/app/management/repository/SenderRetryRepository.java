package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderRetryRepository extends JpaRepository<SenderRetryQueueItem, Long> {}
