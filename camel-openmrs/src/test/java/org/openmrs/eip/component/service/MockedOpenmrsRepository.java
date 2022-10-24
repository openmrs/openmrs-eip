package org.openmrs.eip.component.service;

import org.openmrs.eip.component.entity.MockedEntity;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MockedOpenmrsRepository extends SyncEntityRepository<MockedEntity> {
	
}
