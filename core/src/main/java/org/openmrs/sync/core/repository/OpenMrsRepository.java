package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OpenMrsRepository<E extends BaseEntity> extends JpaRepository<E, Integer> {

    /**
     * find entity by uuid
     * @param uuid the uuid
     * @return entity
     */
    E findByUuid(String uuid);
}
