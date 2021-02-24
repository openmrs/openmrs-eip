package org.openmrs.eip.component.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncModel {
	
	private Class<? extends BaseModel> tableToSyncModelClass;
	
	private BaseModel model;
	
	private SyncMetadata metadata;
	
	/**
	 * Gets the tableToSyncModelClass
	 *
	 * @return the tableToSyncModelClass
	 */
	public Class<? extends BaseModel> getTableToSyncModelClass() {
		return tableToSyncModelClass;
	}
	
	/**
	 * Sets the tableToSyncModelClass
	 *
	 * @param tableToSyncModelClass the tableToSyncModelClass to set
	 */
	public void setTableToSyncModelClass(Class<? extends BaseModel> tableToSyncModelClass) {
		this.tableToSyncModelClass = tableToSyncModelClass;
	}
	
	/**
	 * Gets the model
	 *
	 * @return the model
	 */
	public BaseModel getModel() {
		return model;
	}
	
	/**
	 * Sets the model
	 *
	 * @param model the model to set
	 */
	public void setModel(BaseModel model) {
		this.model = model;
	}
	
	/**
	 * Gets the metadata
	 *
	 * @return the metadata
	 */
	public SyncMetadata getMetadata() {
		return metadata;
	}
	
	/**
	 * Sets the metadata
	 *
	 * @param metadata the metadata to set
	 */
	public void setMetadata(SyncMetadata metadata) {
		this.metadata = metadata;
	}
	
}
