package org.cicr.sync.remote.repository;

import org.cicr.sync.core.entity.PersonEty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEty, String> {

}
