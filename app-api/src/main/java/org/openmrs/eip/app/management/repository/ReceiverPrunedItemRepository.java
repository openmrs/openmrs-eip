package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.ReceiverPrunedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiverPrunedItemRepository extends JpaRepository<ReceiverPrunedItem, Long> {}
