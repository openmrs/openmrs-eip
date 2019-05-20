package org.cicr.camel.central.repository;

import org.cicr.camel.central.entity.ConceptEty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository extends JpaRepository<ConceptEty, Integer> {

    ConceptEty findByUuid(final String uuid);
}
