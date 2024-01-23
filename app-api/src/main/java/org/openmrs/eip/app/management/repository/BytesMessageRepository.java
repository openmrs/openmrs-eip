package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.BytesJmsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BytesMessageRepository extends JpaRepository<BytesJmsMessage, Long> {}
