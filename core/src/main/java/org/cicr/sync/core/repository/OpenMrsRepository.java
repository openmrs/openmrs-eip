package org.cicr.sync.core.repository;

import org.cicr.sync.core.entity.OpenMrsEty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OpenMrsRepository<E extends OpenMrsEty> extends JpaRepository<E, Integer> {

    E findByUuid(String uuid);
}
