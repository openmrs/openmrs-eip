package org.openmrs.eip.app;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

public class CustomFileOffsetBackingStoreTest {
	
	@Spy
	private CustomFileOffsetBackingStore store;
	
	private File mockFile;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(CustomFileOffsetBackingStore.class, "paused", false);
		Whitebox.setInternalState(CustomFileOffsetBackingStore.class, "disabled", false);
		Whitebox.setInternalState(store, File.class, mockFile);
	}
	
	@Test
	public void save_shouldNotSaveOffsetsIfTheStoreIsPaused() {
		Whitebox.setInternalState(CustomFileOffsetBackingStore.class, "paused", true);
		
		store.save();
		
		Mockito.verify(store, Mockito.never()).doSave();
	}
	
	@Test
	public void save_shouldNotSaveOffsetsIfTheStoreIsDisabled() {
		Whitebox.setInternalState(CustomFileOffsetBackingStore.class, "paused", true);
		
		store.save();
		
		Mockito.verify(store, Mockito.never()).doSave();
	}
	
	@Test
	public void save_shouldSaveOffsetsIfTheStoreIsNotPausedAndIsNotDisabled() throws Exception {
		Mockito.doNothing().when(store).doSave();
		
		store.save();
		
		Mockito.verify(store).doSave();
	}
	
}
