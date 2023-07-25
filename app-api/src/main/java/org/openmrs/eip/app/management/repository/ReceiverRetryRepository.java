package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiverRetryRepository extends JpaRepository<ReceiverRetryQueueItem, Long> {}
