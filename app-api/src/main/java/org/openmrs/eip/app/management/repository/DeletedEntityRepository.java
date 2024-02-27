package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.sender.DeletedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedEntityRepository extends JpaRepository<DeletedEntity, Long> {}
