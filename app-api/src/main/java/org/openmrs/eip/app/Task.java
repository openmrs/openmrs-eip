package org.openmrs.eip.app;

/**
 * Super interface for tasks
 */
public interface Task extends Runnable {
	
	/**
	 * Called to determine if tasks should be run or skipped
	 * 
	 * @return true to skip running otherwise false
	 */
	boolean skip();
	
}
