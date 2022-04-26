package org.openmrs.eip.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SnapshotSavePointStore {
	
	private static final Logger log = LoggerFactory.getLogger(SnapshotSavePointStore.class);
	
	private static File file;
	
	private static Properties props;
	
	private static boolean hasChanges = false;
	
	public SnapshotSavePointStore() {
		file = new File(SyncConstants.SAVEPOINT_FILE);
		
		log.info("Snapshot savepoint file -> " + file);
		
		props = new Properties();
		load();
	}
	
	private void load() {
		log.info("Loading the snapshot savepoint");
		
		try {
			if (file.exists()) {
				props.load(new FileInputStream(file));
			}
			
			if (MapUtils.isEmpty(props)) {
				log.info("No snapshot savepoint file found that was previously stored");
			}
			
			log.info("Loaded snapshot savepoint");
		}
		catch (IOException e) {
			throw new EIPException("Failed to read snapshot savepoint file", e);
		}
	}
	
	public Integer getSavedId(String tableName) {
		String value = props.getProperty(tableName);
		if (StringUtils.isBlank(value)) {
			return null;
		}
		
		return Integer.valueOf(value);
	}
	
	public void update(String tableName, String id) {
		props.put(tableName, id);
		hasChanges = true;
	}
	
	public boolean canSave() {
		return hasChanges;
	}
	
	public void save() {
		log.info("Saving the snapshot savepoint");
		
		try {
			props.store(new FileOutputStream(file), null);
			hasChanges = false;
			
			log.info("Successfully saved the snapshot savepoint");
		}
		catch (IOException e) {
			log.error("Failed to save the snapshot savepoint", e);
		}
	}
	
	public void discard() {
		log.info("Deleting the snapshot save point file");
		
		props.clear();
		hasChanges = false;
		
		if (!file.exists()) {
			log.info("No snapshot savepoint file found to delete");
			return;
		}
		
		try {
			FileUtils.forceDelete(file);
			
			log.info("Successfully deleted the snapshot savepoint file");
		}
		catch (IOException e) {
			log.error("Failed to delete the snapshot savepoint file", e);
		}
	}
	
}
