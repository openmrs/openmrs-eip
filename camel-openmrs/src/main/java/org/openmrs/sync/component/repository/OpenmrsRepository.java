package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OpenmrsRepository<E extends BaseEntity> extends JpaRepository<E, Long> {

    /**
     * find entity by uuid
     * @param uuid the uuid
     * @return an entity
     */
    E findByUuid(String uuid);
}
