package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends SyncEntityRepository<User> {
	
	@Override
	@Query("select u from User u " + "where u.dateChanged is null and u.dateCreated >= :lastSyncDate "
	        + "or u.dateChanged >= :lastSyncDate")
	List<User> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
	
	@Override
	@Cacheable(cacheNames = "userAll")
	List<User> findAll();
}
