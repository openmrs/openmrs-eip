package org.openmrs.eip.component.service;

import java.util.List;

import org.openmrs.eip.component.model.BaseModel;

public interface EntityService<M extends BaseModel> {
	
	/**
	 * Saves an entity
	 *
	 * @param entity the entity
	 * @return BaseModel
	 */
	M save(M entity);
	
	/**
	 * get all models for the entity
	 *
	 * @return a list of BaseModel
	 */
	List<M> getAllModels();
	
	/**
	 * get model with the given uuid
	 *
	 * @param uuid
	 * @return a BaseModel
	 */
	M getModel(final String uuid);
	
	/**
	 * get model with the given uuid
	 *
	 * @param id
	 * @return a BaseModel
	 */
	M getModel(final Long id);
	
	/**
	 * Deletes the entity from the database that matches the specified uuid
	 *
	 * @param uuid the uuid of the entity to delete
	 */
	void delete(String uuid);
	
}
