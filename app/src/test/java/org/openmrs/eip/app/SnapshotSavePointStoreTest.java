package org.openmrs.eip.app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class SnapshotSavePointStoreTest {
	
	@Mock
	private File mockFile;
	
	@Mock
	private FileInputStream mockInputStream;
	
	@Mock
	private FileOutputStream mockOutputStream;
	
	@Mock
	private Properties mockProperties;
	
	private SnapshotSavePointStore store;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(FileUtils.class);
		setInternalState(SnapshotSavePointStore.class, boolean.class, false);
		store = new SnapshotSavePointStore();
		setInternalState(SnapshotSavePointStore.class, File.class, mockFile);
		setInternalState(SnapshotSavePointStore.class, Properties.class, mockProperties);
		when(FileUtils.openInputStream(mockFile)).thenReturn(mockInputStream);
		when(FileUtils.openOutputStream(mockFile)).thenReturn(mockOutputStream);
	}
	
	@Test
	public void load_shouldLoadTheStoreContentsFromTheFileIfItExists() throws Exception {
		when(mockFile.exists()).thenReturn(true);
		
		store.load();
		
		verify(mockProperties).load(mockInputStream);
	}
	
	@Test
	public void load_shouldNotLoadTheStoreContentsIfTheFileDoesNotExist() throws Exception {
		when(mockFile.exists()).thenReturn(false);
		
		store.load();
		
		verify(mockProperties, never()).load(any(InputStream.class));
	}
	
	@Test
	public void getSavedRowId_shouldReturnTheSavedRowIdForTheSpecifiedTable() {
		final String table = "person";
		final Integer expected = 2;
		when(mockProperties.getProperty(table)).thenReturn(expected.toString());
		Assert.assertEquals(expected, store.getSavedRowId(table));
	}
	
	@Test
	public void getSavedRowId_shouldReturnNullIfNoMatchIsFound() {
		final String table = "person";
		when(mockProperties.getProperty(table)).thenReturn(null);
		assertNull(store.getSavedRowId(table));
	}
	
	@Test
	public void getSavedRowId_shouldReturnNullIfTheValueIsBlank() {
		final String table = "person";
		when(mockProperties.getProperty(table)).thenReturn("");
		assertNull(store.getSavedRowId(table));
	}
	
	@Test
	public void getSavedRowId_shouldReturnNullIfTheValueIsWhitespace() {
		final String table = "person";
		when(mockProperties.getProperty(table)).thenReturn(" ");
		assertNull(store.getSavedRowId(table));
	}
	
	@Test
	public void update_shouldSetTheRowIdForTheSpecifiedTable() {
		assertFalse(store.canSave());
		final String table = "patient";
		final String id = "3";
		
		store.update(table, id, false);
		
		verify(mockProperties).put(table, id);
		assertTrue(store.canSave());
		
	}
	
	@Test
	public void save_shouldWriteTheStoreContentsToTheFile() throws Exception {
		setInternalState(SnapshotSavePointStore.class, boolean.class, true);
		assertTrue(store.canSave());
		
		store.save();
		
		verify(mockProperties).store(mockOutputStream, null);
		assertFalse(store.canSave());
	}
	
	@Test
	public void discard_shouldClearTheStoreInMemoryContents() throws Exception {
		setInternalState(SnapshotSavePointStore.class, boolean.class, true);
		assertTrue(store.canSave());
		when(mockFile.exists()).thenReturn(false);
		
		store.discard();
		
		assertFalse(store.canSave());
		PowerMockito.verifyStatic(FileUtils.class, never());
		FileUtils.forceDelete(mockFile);
	}
	
	@Test
	public void discard_shouldClearTheStoreInMemoryContentsAndDeleteTheFileIfItExists() throws Exception {
		setInternalState(SnapshotSavePointStore.class, boolean.class, true);
		assertTrue(store.canSave());
		when(mockFile.exists()).thenReturn(true);
		
		store.discard();
		
		assertFalse(store.canSave());
		PowerMockito.verifyStatic(FileUtils.class);
		FileUtils.forceDelete(mockFile);
	}
	
}
