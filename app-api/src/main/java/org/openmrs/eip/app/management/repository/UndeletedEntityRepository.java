package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.UndeletedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UndeletedEntityRepository extends JpaRepository<UndeletedEntity, Long> {}
