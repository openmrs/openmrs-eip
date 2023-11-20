package org.openmrs.eip.app.management.entity.receiver;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "receiver_pruned_item")
public class ReceiverPrunedItem extends BaseReceiverArchive {
	
	private static final long serialVersionUID = 1L;
	
	public ReceiverPrunedItem() {
	}
	
	public ReceiverPrunedItem(ReceiverSyncArchive archive) {
		BeanUtils.copyProperties(archive, this, "id", "dateCreated");
		setDateCreated(new Date());
	}
	
}
