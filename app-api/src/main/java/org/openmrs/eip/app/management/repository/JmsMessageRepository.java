package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JmsMessageRepository extends JpaRepository<JmsMessage, Long> {
	
	boolean existsByMessageId(String messageId);
	
}
