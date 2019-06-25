package org.openmrs.sync.core.service.facade;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EntityServiceFacade {

    private List<AbstractEntityService<? extends BaseEntity, ? extends BaseModel>> services;

    public EntityServiceFacade(final List<AbstractEntityService<? extends BaseEntity, ? extends BaseModel>> services) {
        this.services = services;
    }

    /**
     * get all models of type in parameter after the last sync date
     * @param tableToSyncEnum the type of entities to get
     * @param lastSyncDate the last sync date
     * @param <M>
     * @return the entities
     */
    public <M extends BaseModel> List<M> getModels(final TableToSyncEnum tableToSyncEnum, final LocalDateTime lastSyncDate) {
        return (List<M>) getService(tableToSyncEnum).getModels(lastSyncDate);
    }

    /**
     * save the model of type in parameter
     * @param tableToSync the type of model to save
     * @param model the model to save
     * @param <M>
     */
    public <M extends BaseModel> void saveModel(final TableToSyncEnum tableToSync,
                                                final M model) {
        getService(tableToSync).save(model);
    }

    private <E extends BaseEntity, M extends BaseModel> AbstractEntityService<E, M> getService(final TableToSyncEnum tableToSync) {
        return services.stream()
                .filter(service -> service.getTableToSync().equals(tableToSync))
                .map(service -> (AbstractEntityService<E, M>) service)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity " + tableToSync.name()));
    }
}
