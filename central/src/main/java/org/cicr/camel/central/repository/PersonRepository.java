package org.cicr.camel.central.repository;

import org.cicr.camel.central.entity.PersonEty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEty, Integer> {
}
