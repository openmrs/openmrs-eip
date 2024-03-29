package org.openmrs.eip.app.receiver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
public class SyncHelperTest {
	
	@Mock
	private EntityLoader mockLoader;
	
	@Mock
	private ConflictService mockConflictService;
	
	private SyncHelper helper;
	
	@Before
	public void setup() {
		helper = new SyncHelper(mockLoader);
		Whitebox.setInternalState(helper, "conflictService", mockConflictService);
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
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		PersonModel model = new PersonModel();
		model.setUuid(uuid);
		syncModel.setModel(model);
		Mockito.when(mockConflictService.hasConflictItem(uuid, PersonModel.class.getName())).thenReturn(true);
		
		EIPException ex = Assert.assertThrows(EIPException.class, () -> helper.sync(syncModel, false));
		Assert.assertEquals("Cannot process the message because the entity has a conflict item in the queue",
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
		Mockito.when(mockConflictService.hasConflictItem(uuid, PersonModel.class.getName())).thenReturn(true);
		
		helper.sync(syncModel, true);
		
		Mockito.verify(mockLoader).process(syncModel);
	}
	
}
