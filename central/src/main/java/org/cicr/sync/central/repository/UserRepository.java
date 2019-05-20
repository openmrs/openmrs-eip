package org.cicr.sync.central.repository;

import org.cicr.sync.core.entity.UserEty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEty, Integer> {

    UserEty findByUuid(final String uuid);
}
