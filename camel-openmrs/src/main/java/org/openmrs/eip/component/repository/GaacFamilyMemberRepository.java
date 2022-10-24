package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.GaacFamilyMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GaacFamilyMemberRepository extends SyncEntityRepository<GaacFamilyMember> {
	
	@Override
	@Query("select g from GaacFamilyMember g " + "where g.dateChanged is null and g.dateCreated >= :lastSyncDate "
	        + "or g.dateChanged >= :lastSyncDate")
	List<GaacFamilyMember> findModelsChangedAfterDate(@Param(value = "lastSyncDate") LocalDateTime lastSyncDate);
}
