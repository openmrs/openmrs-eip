package org.openmrs.sync.core.service.facade;

import org.openmrs.sync.core.camel.TableNameEnum;
import org.openmrs.sync.core.entity.OpenMrsEty;
import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityServiceFacade {

    private List<AbstractEntityService<? extends OpenMrsEty, ? extends OpenMrsModel>> services;

    public EntityServiceFacade(final List<AbstractEntityService<? extends OpenMrsEty, ? extends OpenMrsModel>> services) {
        this.services = services;
    }

    public <M extends OpenMrsModel> List<M> getModels(final TableNameEnum tableNameEnum) {
        return (List<M>) getService(tableNameEnum).getModels();
    }

    public <M extends OpenMrsModel> void saveModel(final TableNameEnum tableNameEnum,
                                                   final M model) {
        getService(tableNameEnum).save(model);
    }

    private <E extends OpenMrsEty, M extends OpenMrsModel> AbstractEntityService<E, M> getService(final TableNameEnum entityName) {
        return services.stream()
                .filter(service -> service.getEntityName() == entityName)
                .map(service -> (AbstractEntityService<E, M>) service)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity"));
    }
}
