package org.openmrs.eip.component.repository;

import java.util.List;

import org.openmrs.eip.component.entity.PatientIdentifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientIdentifierRepository extends SyncEntityRepository<PatientIdentifier> {
	
	/**
	 * Gets the uuids of the identifiers for the patient with the specified uuid
	 *
	 * @param patientUuid the patient uuid
	 * @return list of identifier uuids
	 */
	@Query("SELECT i.uuid FROM PatientIdentifier i WHERE i.patient.uuid = :patientUuid")
	List<String> getPatientIdentifierUuids(@Param("patientUuid") String patientUuid);
	
}
