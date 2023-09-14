package org.openmrs.eip.component.repository;

import java.util.List;

import org.openmrs.eip.component.entity.PersonName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonNameRepository extends SyncEntityRepository<PersonName> {
	
	/**
	 * Gets the uuids of the names for the person with the specified uuid
	 *
	 * @param personUuid the person uuid
	 * @return list of person name uuids
	 */
	@Query("SELECT n.uuid FROM PersonName n WHERE n.person.uuid = :personUuid")
	List<String> getPersonNameUuids(@Param("personUuid") String personUuid);
	
}
