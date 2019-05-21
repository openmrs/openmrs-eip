package org.cicr.sync.core.repository;

import org.cicr.sync.core.entity.ConceptEty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository extends JpaRepository<ConceptEty, Integer> {

    ConceptEty findByUuid(final String uuid);
}
