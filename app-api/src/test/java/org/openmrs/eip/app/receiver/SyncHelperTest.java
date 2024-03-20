package org.openmrs.eip.app.receiver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.Utils;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class SyncHelperTest {
	
	@Mock
	private EntityLoader mockLoader;
	
	@Mock
	private ConflictRepository mockConflictRepo;
	
	private SyncHelper helper;
	
	@Before
	public void setup() {
		helper = new SyncHelper(mockLoader, mockConflictRepo);
	}
	
	@Test
	public void sync_shouldLoadTheEntity() {
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		syncModel.setModel(new PersonModel());
		
		helper.sync(syncModel, false);
		
		Mockito.verify(mockLoader).process(syncModel);
	}
	
	@Test
	public void sync_shouldFailIfAnEntityHasItemsInTheConflictQueue() {
		final String uuid = "uuid";
		final long conflictCount = 1;
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		PersonModel model = new PersonModel();
		model.setUuid(uuid);
		syncModel.setModel(model);
		Mockito.when(mockConflictRepo.countByIdentifierAndModelClassNameIn(uuid,
		    Utils.getListOfModelClassHierarchy(PersonModel.class.getName()))).thenReturn(conflictCount);
		
		EIPException ex = Assert.assertThrows(EIPException.class, () -> helper.sync(syncModel, false));
		Assert.assertEquals(
		    "Cannot process the message because the entity has " + conflictCount + " items in the conflict queue",
		    ex.getMessage());
	}
	
	@Test
	public void sync_shouldPassIfTheFoundConflictWhileSyncingAConflictItem() {
		final String uuid = "uuid";
		final long conflictCount = 1;
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		PersonModel model = new PersonModel();
		model.setUuid(uuid);
		syncModel.setModel(model);
		Mockito.when(mockConflictRepo.countByIdentifierAndModelClassNameIn(uuid,
		    Utils.getListOfModelClassHierarchy(PersonModel.class.getName()))).thenReturn(conflictCount);
		
		helper.sync(syncModel, true);
		
		Mockito.verify(mockLoader).process(syncModel);
	}
	
}
