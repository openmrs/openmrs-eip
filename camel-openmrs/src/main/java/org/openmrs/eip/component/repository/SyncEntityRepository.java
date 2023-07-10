package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.BaseEntity;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SyncEntityRepository<E extends BaseEntity> extends OpenmrsRepository<E> {}
