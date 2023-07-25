package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<SiteInfo, Long> {}
