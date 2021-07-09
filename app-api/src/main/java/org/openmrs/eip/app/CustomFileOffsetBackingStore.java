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
	
	private static boolean disabled = false;
	
	private static final Object LOCK = new Object();
	
	public synchronized static void disable() {
		disabled = true;
		if (log.isDebugEnabled()) {
			log.debug("Disabled saving of offsets");
		}
	}
	
	public synchronized static boolean isDisabled() {
		return disabled;
	}
	
	/**
	 * @see FileOffsetBackingStore#save()
	 */
	@Override
	protected void save() {
		synchronized (LOCK) {
			log.error("In CustomFileOffsetBackingStore.save: " + Thread.currentThread() + " -> disabled: " + disabled);
			if (disabled) {
				log.info("Skipping saving of offset because an error was encountered while processing a source record");
				return;
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Saving binlog offset");
			}
			
			super.save();
		}
	}
	
}
