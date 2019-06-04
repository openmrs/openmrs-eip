package org.openmrs.sync.core.service.facade;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.EntityNameEnum;
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
     * @param entityNameEnum the type of entities to get
     * @param lastSyncDate the last sync date
     * @param <M>
     * @return the entities
     */
    public <M extends BaseModel> List<M> getModels(final EntityNameEnum entityNameEnum, final LocalDateTime lastSyncDate) {
        return (List<M>) getService(entityNameEnum).getModels(lastSyncDate);
    }

    /**
     * save the model of type in parameter
     * @param entityNameEnum the type of model to save
     * @param model the model to save
     * @param <M>
     */
    public <M extends BaseModel> void saveModel(final EntityNameEnum entityNameEnum,
                                                final M model) {
        getService(entityNameEnum).save(model);
    }

    private <E extends BaseEntity, M extends BaseModel> AbstractEntityService<E, M> getService(final EntityNameEnum entityName) {
        return services.stream()
                .filter(service -> service.getEntityName() == entityName)
                .map(service -> (AbstractEntityService<E, M>) service)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity " + entityName.name()));
    }
}
