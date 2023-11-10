package org.openmrs.eip.app.management.entity.sender;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "sender_pruned_archive")
public class SenderPrunedArchive extends BaseSenderArchive {
	
	private static final long serialVersionUID = 1L;
	
	public SenderPrunedArchive() {
	}
	
	public SenderPrunedArchive(SenderSyncArchive archive) {
		BeanUtils.copyProperties(archive, this, "id", "dateCreated");
		setDateCreated(new Date());
	}
	
}
