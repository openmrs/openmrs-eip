package org.openmrs.sync.component.service.facade;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.component.entity.BaseEntity;
import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component("entityServiceFacade")
public class EntityServiceFacade {

    private List<AbstractEntityService<? extends BaseEntity, ? extends BaseModel>> services;

    public EntityServiceFacade(final List<AbstractEntityService<? extends BaseEntity, ? extends BaseModel>> services) {
        this.services = services;
    }

    /**
     * get all models of type in parameter after the last sync date
     *
     * @param tableToSyncEnum the type of entities to get
     * @param <M>
     * @return the entities
     */
    public <M extends BaseModel> List<M> getAllModels(final TableToSyncEnum tableToSyncEnum) {
        return (List<M>) getService(tableToSyncEnum).getAllModels();
    }

    /**
     * get all models of type in parameter after the last sync date
     *
     * @param tableToSyncEnum the type of entities to get
     * @param lastSyncDate    the last sync date
     * @param <M>
     * @return the entities
     */
    public <M extends BaseModel> List<M> getModelsAfterDate(final TableToSyncEnum tableToSyncEnum, final LocalDateTime lastSyncDate) {
        return (List<M>) getService(tableToSyncEnum).getModels(lastSyncDate);
    }

    /**
     * get model of type in parameter with the given uuid
     *
     * @param tableToSyncEnum the type of entities to get
     * @param uuid            the uuid
     * @param <M>
     * @return the entity
     */
    public <M extends BaseModel> M getModel(final TableToSyncEnum tableToSyncEnum, final String uuid) {
        return (M) getService(tableToSyncEnum).getModel(uuid);
    }

    /**
     * get model of type in parameter with the given uuid
     *
     * @param tableToSyncEnum the type of entities to get
     * @param id              the id
     * @param <M>
     * @return the entity
     */
    public <M extends BaseModel> M getModel(final TableToSyncEnum tableToSyncEnum, final Long id) {
        return (M) getService(tableToSyncEnum).getModel(id);
    }

    /**
     * save the model of type in parameter
     *
     * @param tableToSync the type of model to save
     * @param model       the model to save
     * @param <M>
     */
    public <M extends BaseModel> void saveModel(final TableToSyncEnum tableToSync,
                                                final M model) {
        getService(tableToSync).save(model);
    }

    /**
     * Deletes an entity from the database
     *
     * @param tableToSync
     * @param uuid        the uuid of the entity
     */
    public void delete(final TableToSyncEnum tableToSync, final String uuid) {
        getService(tableToSync).delete(uuid);
    }

    private <E extends BaseEntity, M extends BaseModel> AbstractEntityService<E, M> getService(final TableToSyncEnum tableToSync) {
        return services.stream()
                .filter(service -> service.getTableToSync().equals(tableToSync))
                .map(service -> (AbstractEntityService<E, M>) service)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity " + tableToSync.name()));
    }
}
