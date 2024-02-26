package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.MissingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissingEntityRepository extends JpaRepository<MissingEntity, Long> {}
