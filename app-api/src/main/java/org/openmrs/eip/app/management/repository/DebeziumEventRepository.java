package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebeziumEventRepository extends JpaRepository<DebeziumEvent, Long> {}
