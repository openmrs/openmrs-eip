package org.openmrs.eip.app;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SnapshotSavePointStore {
	
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
				log.info("Loading saved snapshot savepoint");
				
				props.load(FileUtils.openInputStream(file));
				
				log.info("Done loading saved snapshot savepoint");
			}
			
			if (MapUtils.isEmpty(props)) {
				log.info("No saved snapshot savepoint file found");
			}
			
			log.info("Done initializing saved snapshot savepoint store");
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
	
	void update(Map<String, Integer> tableIdMap) {
		props.putAll(tableIdMap.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> e.getValue().toString())));
		
		log.info("Writing the snapshot savepoint to disk");
		
		try {
			props.store(FileUtils.openOutputStream(file), null);
			
			log.info("Successfully written the snapshot savepoint to disk");
		}
		catch (IOException e) {
			throw new EIPException("Failed to write the snapshot savepoint to disk", e);
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
			throw new EIPException("Failed to delete the snapshot savepoint file", e);
		}
	}
	
}
