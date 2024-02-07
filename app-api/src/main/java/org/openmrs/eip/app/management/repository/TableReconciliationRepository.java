package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.entity.receiver.TableReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableReconciliationRepository extends JpaRepository<TableReconciliation, Long> {
	
	TableReconciliation getBySiteReconciliationAndTableName(SiteReconciliation siteReconciliation, String tableName);
	
}
