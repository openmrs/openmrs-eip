package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.TextJmsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextMessageRepository extends JpaRepository<TextJmsMessage, Long> {}
