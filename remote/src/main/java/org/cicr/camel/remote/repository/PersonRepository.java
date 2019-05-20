package org.cicr.camel.remote.repository;

import org.cicr.camel.remote.entity.PersonEty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEty, String> {

}
