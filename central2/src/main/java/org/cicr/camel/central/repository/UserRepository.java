package org.cicr.camel.central.repository;

import org.cicr.camel.central.entity.UserEty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEty, Integer> {

    UserEty findByUuid(final String uuid);
}
