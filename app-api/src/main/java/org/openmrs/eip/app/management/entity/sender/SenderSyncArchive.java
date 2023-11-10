package org.openmrs.eip.app.management.entity.sender;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "sender_sync_archive")
public class SenderSyncArchive extends BaseSenderArchive {
	
	private static final long serialVersionUID = 1L;
	
	public SenderSyncArchive() {
	}
	
	public SenderSyncArchive(SenderSyncMessage syncMessage) {
		BeanUtils.copyProperties(syncMessage, this, "id", "dateCreated");
	}
	
}
