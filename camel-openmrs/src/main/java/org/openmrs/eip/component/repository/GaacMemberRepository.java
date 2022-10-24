package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.GaacMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GaacMemberRepository extends SyncEntityRepository<GaacMember> {
	
	@Override
	@Query("select g from GaacMember g " + "where g.dateChanged is null and g.dateCreated >= :lastSyncDate "
	        + "or g.dateChanged >= :lastSyncDate")
	List<GaacMember> findModelsChangedAfterDate(@Param(value = "lastSyncDate") LocalDateTime lastSyncDate);
}
