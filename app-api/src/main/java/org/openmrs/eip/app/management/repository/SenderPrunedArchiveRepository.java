package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.sender.SenderPrunedArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderPrunedArchiveRepository extends JpaRepository<SenderPrunedArchive, Long> {}
