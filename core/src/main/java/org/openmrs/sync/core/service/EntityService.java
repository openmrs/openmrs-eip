package org.openmrs.sync.core.service;

import org.openmrs.sync.core.model.BaseModel;

import java.time.LocalDateTime;
import java.util.List;

public interface EntityService<M extends BaseModel> {

    /**
     * Saves an entity
     * @param entity the entity
     * @return BaseModel
     */
    M save(M entity);

    /**
     * getAll models for the entity
     * @return a list of BaseModel
     * @param lastSyncDate
     */
    List<M> getModels(final LocalDateTime lastSyncDate);
}
