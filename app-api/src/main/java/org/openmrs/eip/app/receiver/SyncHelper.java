package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.Utils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("syncHelper")
@Profile(SyncProfiles.RECEIVER)
public class SyncHelper {
	
	private EntityLoader loader;
	
	private ConflictRepository conflictRepo;
	
	public SyncHelper(EntityLoader loader, ConflictRepository conflictRepo) {
		this.loader = loader;
		this.conflictRepo = conflictRepo;
	}
	
	public void sync(SyncModel syncModel, boolean isConflictItem) {
		List<String> modelClasses = Utils.getListOfModelClassHierarchy(syncModel.getTableToSyncModelClass().getName());
		String uuid = syncModel.getModel().getUuid();
		long conflictCount = conflictRepo.countByIdentifierAndModelClassNameIn(uuid, modelClasses);
		if (conflictCount > 0 && !isConflictItem) {
			throw new EIPException(
			        "Cannot process the message because the entity has " + conflictCount + " items in the conflict queue");
		}
		
		loader.process(syncModel);
	}
	
}
