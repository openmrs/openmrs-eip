package org.openmrs.eip.app;

import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link FileOffsetBackingStore} that only saves the offset if no exception was encountered
 * while processing a source record read by debezium from the MySQL binlog to ensure no binlog entry
 * goes unprocessed.
 */
public class CustomFileOffsetBackingStore extends FileOffsetBackingStore {
	
	protected static final Logger log = LoggerFactory.getLogger(CustomFileOffsetBackingStore.class);
	
	private static Boolean canSave = false;
	
	@Override
	protected void save() {
		if (!canSave) {
			log.info("Skipping saving of latest offset");
			return;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Saving binlog position");
		}
		
		super.save();
	}
	
}
