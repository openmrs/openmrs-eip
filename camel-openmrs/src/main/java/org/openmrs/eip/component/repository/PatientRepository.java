package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientRepository extends SyncEntityRepository<Patient>, PatientRepositoryCustom {
	
	@Override
	@Query("select p from Patient p " + "where p.dateChanged is null and p.dateCreated >= :lastSyncDate "
	        + "or p.dateChanged >= :lastSyncDate")
	List<Patient> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
	
	/**
	 * Checks if a patient is in the given workflow state concept mapping in a SAME-AS type
	 * 
	 * @param uuid the patient uuid
	 * @param workflowStateConceptMapping list of workflow state mappings
	 * @return 1 if true, 0 if false
	 */
	@Query(value = "SELECT count(p.patient_id) > 0 FROM patient p " + "LEFT JOIN person pe ON pe.person_id = p.patient_id "
	        + "LEFT JOIN patient_program pp ON pp.patient_id = p.patient_id "
	        + "LEFT JOIN patient_state ps ON ps.patient_program_id = pp.patient_program_id "
	        + "LEFT JOIN program_workflow_state pws ON pws.program_workflow_state_id = ps.state "
	        + "LEFT JOIN concept c ON c.concept_id = pws.concept_id "
	        + "LEFT JOIN concept_reference_map crm ON crm.concept_id = c.concept_id "
	        + "LEFT JOIN concept_reference_term crt ON crt.concept_reference_term_id = crm.concept_reference_term_id "
	        + "LEFT JOIN concept_map_type cmt ON cmt.concept_map_type_id = crm.concept_map_type_id "
	        + "LEFT JOIN concept_reference_source crs ON crs.concept_source_id = crt.concept_source_id "
	        + "WHERE CONCAT(crs.name,':',crt.code) IN (:workflowStateConceptMappings) " + "AND cmt.name = 'SAME-AS'"
	        + "AND pe.uuid = :uuid", nativeQuery = true)
	int isPatientInGivenWorkflowStateMySQL(@Param("uuid") String uuid,
	                                       @Param("workflowStateConceptMappings") List<String> workflowStateConceptMapping);
}
