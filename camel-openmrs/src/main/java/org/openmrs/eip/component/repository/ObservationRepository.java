package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.Observation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ObservationRepository extends SyncEntityRepository<Observation>, ObservationRepositoryCustom {
	
	@Override
	@Query("select o from Observation o " + "where o.dateCreated >= :lastSyncDate")
	List<Observation> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
	
	/**
	 * Checks if an obs is coded by the provided concept mapping, eg. 'CIEL:1234'
	 * 
	 * @param uuid the obs uuid
	 * @param conceptMapping the concept mapping
	 * @return 1 if true, 0 if false
	 */
	@Query(value = "SELECT count(o.obs_id) > 0 FROM obs o " + "LEFT JOIN concept c ON c.concept_id = o.concept_id "
	        + "LEFT JOIN concept_reference_map crm ON crm.concept_id = c.concept_id "
	        + "LEFT JOIN concept_reference_term crt ON crt.concept_reference_term_id = crm.concept_reference_term_id "
	        + "LEFT JOIN concept_map_type cmt ON cmt.concept_map_type_id = crm.concept_map_type_id "
	        + "LEFT JOIN concept_reference_source crs ON crs.concept_source_id = crt.concept_source_id "
	        + "WHERE CONCAT(crs.name,':',crt.code) = :conceptMapping " + "AND cmt.name = 'SAME-AS'"
	        + "AND o.uuid = :uuid", nativeQuery = true)
	int isObsLinkedToGivenConceptMappingMySQL(@Param("uuid") String uuid, @Param("conceptMapping") String conceptMapping);
	
}
