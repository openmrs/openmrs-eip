package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncedMessageRepository extends JpaRepository<SyncedMessage, Long> {}
