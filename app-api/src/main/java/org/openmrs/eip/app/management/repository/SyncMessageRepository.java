package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.SyncMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncMessageRepository extends JpaRepository<SyncMessage, Long> {}
