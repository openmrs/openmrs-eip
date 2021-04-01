package org.openmrs.utils.odoo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@NoRepositoryBean
public interface ErpWorkOrderStateRepository {

    //@Override
    //@Query("SELECT w FROM ErpWorkOrderState w WHERE w.dateCreated >= :lastSyncDate AND w.voided = 0 ORDER BY w.dateCreated ASC")
    //List<ErpWorkOrderState> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);

}
