package org.openmrs.sync.core.service;

import org.openmrs.sync.core.model.OpenMrsModel;

import java.time.LocalDateTime;
import java.util.List;

public interface EntityService<M extends OpenMrsModel> {

    /**
     * Saves an entity
     * @param entity the entity
     * @return OpenMrsModel
     */
    M save(M entity);

    /**
     * getAll models for the entity
     * @return a list of OpenMrsModel
     * @param lastSyncDate
     */
    List<M> getModels(final LocalDateTime lastSyncDate);
}
