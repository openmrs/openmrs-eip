package org.openmrs.eip.app.management.entity.receiver;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "receiver_sync_archive")
public class ReceiverSyncArchive extends BaseReceiverArchive {
	
	public static final long serialVersionUID = 1;
	
	public ReceiverSyncArchive() {
	}
	
	public ReceiverSyncArchive(SyncedMessage processedMessage) {
		BeanUtils.copyProperties(processedMessage, this, "id", "dateCreated");
	}
	
	public ReceiverSyncArchive(ReceiverRetryQueueItem retry) {
		BeanUtils.copyProperties(retry, this, "id", "dateCreated");
	}
	
	public ReceiverSyncArchive(ConflictQueueItem conflict) {
		BeanUtils.copyProperties(conflict, this, "id", "dateCreated");
	}
	
}
