package org.openmrs.sync.core.service.facade;

import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableNameEnum;
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
     * @param tableNameEnum the type of entities to get
     * @param lastSyncDate the last sync date
     * @param <M>
     * @return the entities
     */
    public <M extends BaseModel> List<M> getModels(final TableNameEnum tableNameEnum, final LocalDateTime lastSyncDate) {
        return (List<M>) getService(tableNameEnum).getModels(lastSyncDate);
    }

    /**
     * save the model of type in parameter
     * @param tableNameEnum the type of model to save
     * @param model the model to save
     * @param <M>
     */
    public <M extends BaseModel> void saveModel(final TableNameEnum tableNameEnum,
                                                final M model) {
        getService(tableNameEnum).save(model);
    }

    private <E extends BaseEntity, M extends BaseModel> AbstractEntityService<E, M> getService(final TableNameEnum tableName) {
        return services.stream()
                .filter(service -> service.getTableName() == tableName)
                .map(service -> (AbstractEntityService<E, M>) service)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity " + tableName.name()));
    }
}
