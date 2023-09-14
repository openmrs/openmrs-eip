package org.openmrs.eip.component.repository;

import java.util.List;

import org.openmrs.eip.component.entity.PersonAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonAttributeRepository extends SyncEntityRepository<PersonAttribute> {
	
	/**
	 * Gets the uuids of the searchable attributes for the person with the specified uuid
	 *
	 * @param personUuid the person uuid
	 * @return list of attribute uuids
	 */
	@Query("SELECT a.uuid FROM PersonAttribute a WHERE a.person.uuid = :personUuid AND a.personAttributeType.searchable = true")
	List<String> getPersonAttributeUuids(@Param("personUuid") String personUuid);
	
}
