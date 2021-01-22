package org.openmrs.eip.app;

import org.openmrs.eip.component.entity.BaseEntity;
import org.openmrs.eip.component.model.BaseModel;

/**
 * Encapsulates information about a monitored database table
 */
public class TableMetadata {
	
	private Class<? extends BaseEntity> entityClass;
	
	private Class<? extends BaseModel> modelClass;
	
	boolean isSubClassTable;
	
	/**
	 * Gets the entityClass
	 *
	 * @return the entityClass
	 */
	public Class<? extends BaseEntity> getEntityClass() {
		return entityClass;
	}
	
	/**
	 * Sets the entityClass
	 *
	 * @param entityClass the entityClass to set
	 */
	public void setEntityClass(Class<? extends BaseEntity> entityClass) {
		this.entityClass = entityClass;
	}
	
	/**
	 * Gets the modelClass
	 *
	 * @return the modelClass
	 */
	public Class<? extends BaseModel> getModelClass() {
		return modelClass;
	}
	
	/**
	 * Sets the modelClass
	 *
	 * @param modelClass the modelClass to set
	 */
	public void setModelClass(Class<? extends BaseModel> modelClass) {
		this.modelClass = modelClass;
	}
	
	/**
	 * Gets the isSubClassTable
	 *
	 * @return the isSubClassTable
	 */
	public boolean isSubClassTable() {
		return isSubClassTable;
	}
	
	/**
	 * Sets the subClassTable
	 *
	 * @param subClassTable the subClassTable to set
	 */
	public void setSubClassTable(boolean subClassTable) {
		isSubClassTable = subClassTable;
	}
	
}
