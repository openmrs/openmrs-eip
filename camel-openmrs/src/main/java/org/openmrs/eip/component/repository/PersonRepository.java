package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.Person;
import org.springframework.data.jpa.repository.Query;

public interface PersonRepository extends SyncEntityRepository<Person> {
	
	@Query(value = "SELECT uuid FROM person WHERE person_id = :personId", nativeQuery = true)
	String getUuid(Long personId);
	
}
