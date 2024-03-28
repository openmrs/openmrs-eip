package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.SyncModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("syncHelper")
@Profile(SyncProfiles.RECEIVER)
public class SyncHelper {
	
	private EntityLoader loader;
	
	private ConflictService conflictService;
	
	public SyncHelper(EntityLoader loader, ConflictService conflictService) {
		this.loader = loader;
		this.conflictService = conflictService;
	}
	
	public void sync(SyncModel syncModel, boolean isConflictItem) {
		String uuid = syncModel.getModel().getUuid();
		boolean hasConflict = conflictService.hasConflictItem(uuid, syncModel.getTableToSyncModelClass().getName());
		if (hasConflict && !isConflictItem) {
			throw new EIPException("Cannot process the message because the entity has a conflict item in the queue");
		}
		
		loader.process(syncModel);
	}
	
}
