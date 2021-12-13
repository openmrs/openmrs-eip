package org.openmrs.eip.app.management.entity;

public class SyncRequestModel {
	
	private String tableName;
	
	private String identifier;
	
	private String requestUuid;
	
	public SyncRequestModel() {
	}
	
	public SyncRequestModel(String tableName, String identifier, String requestUuid) {
		this.tableName = tableName;
		this.identifier = identifier;
		this.requestUuid = requestUuid;
	}
	
	/**
	 * Gets the tableName
	 *
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Sets the tableName
	 *
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Gets the identifier
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Sets the identifier
	 *
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Gets the requestUuid
	 *
	 * @return the requestUuid
	 */
	public String getRequestUuid() {
		return requestUuid;
	}
	
	/**
	 * Sets the requestUuid
	 *
	 * @param requestUuid the requestUuid to set
	 */
	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "tableName=" + tableName + ", identifier=" + identifier + ", requestUuid="
		        + requestUuid + "}";
	}
}
