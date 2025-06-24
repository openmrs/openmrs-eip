package org.openmrs.eip.mysql.watcher;

import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.openmrs.eip.Utils;
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
	
	public static void disable() {
		disabled = true;
		if (log.isDebugEnabled()) {
			log.debug("Disabled saving of offsets");
		}
	}
	
	public static boolean isDisabled() {
		return disabled;
	}
	
	/**
	 * @see FileOffsetBackingStore#start()
	 */
	@Override
	public synchronized void start() {
		doStart();
		
		try {
			OffsetUtils.transformOffsetIfNecessary(data);
		}
		catch (Exception e) {
			log.error("An error occurred while transforming the existing debezium offset file data", e);
			Utils.shutdown();
		}
	}
	
	protected void doStart() {
		super.start();
	}
	
	/**
	 * @see FileOffsetBackingStore#save()
	 */
	@Override
	protected void save() {
		synchronized (CustomFileOffsetBackingStore.class) {
			if (disabled) {
				log.warn("Skipping saving of offset because an error was encountered while processing a source record");
				return;
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Saving binlog offset");
			}
			
			super.save();
		}
	}
	
}
