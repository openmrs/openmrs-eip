package org.openmrs.eip.app;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SnapshotSavePointStore {
	
	private static final Logger log = LoggerFactory.getLogger(SnapshotSavePointStore.class);
	
	private static File file;
	
	private static Properties props;
	
	SnapshotSavePointStore() {
		file = new File(SyncConstants.SAVEPOINT_FILE);
		
		log.info("Snapshot savepoint file -> " + file);
		
		props = new Properties();
	}
	
	void init() {
		log.info("Initializing snapshot savepoint store");
		
		try {
			if (file.exists()) {
				log.info("Loading the snapshot savepoint");

				props.load(FileUtils.openInputStream(file));

				log.info("Done loading snapshot savepoint");
			}
			
			if (MapUtils.isEmpty(props)) {
				log.info("No snapshot savepoint file found that was previously stored");
			}
			
			log.info("Done initializing snapshot savepoint store");
		}
		catch (IOException e) {
			throw new EIPException("Failed to read snapshot savepoint file", e);
		}
	}
	
	Integer getSavedRowId(String tableName) {
		String value = props.getProperty(tableName);
		if (StringUtils.isBlank(value)) {
			return null;
		}
		
		return Integer.valueOf(value);
	}
	
	void update(String tableName, String id) {
		props.put(tableName, id);
	}
	
	void save() {
		log.info("Saving the snapshot savepoint");
		
		try {
			props.store(FileUtils.openOutputStream(file), null);
			
			log.info("Successfully saved the snapshot savepoint");
		}
		catch (IOException e) {
			log.error("Failed to save the snapshot savepoint", e);
		}
	}
	
	void discard() {
		log.info("Deleting the snapshot savepoint file");
		
		props.clear();
		
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
