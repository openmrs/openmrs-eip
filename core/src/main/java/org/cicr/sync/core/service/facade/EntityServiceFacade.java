package org.cicr.sync.core.service.facade;

import org.cicr.sync.core.camel.EntityNameEnum;
import org.cicr.sync.core.entity.OpenMrsEty;
import org.cicr.sync.core.model.OpenMrsModel;
import org.cicr.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityServiceFacade {

    private List<AbstractEntityService<? extends OpenMrsEty, ? extends OpenMrsModel>> services;

    public EntityServiceFacade(final List<AbstractEntityService<? extends OpenMrsEty, ? extends OpenMrsModel>> services) {
        this.services = services;
    }

    public List<? extends OpenMrsModel> getModels(final EntityNameEnum entityNameEnum) {
        return getService(entityNameEnum).getModels();
    }

    public <M extends OpenMrsModel> void saveModel(final EntityNameEnum entityNameEnum,
                                                   final M model) {
        getService(entityNameEnum).save(model);
    }

    private <E extends OpenMrsEty, M extends OpenMrsModel> AbstractEntityService<E, M> getService(final EntityNameEnum entityName) {
        return services.stream()
                .filter(service -> service.getEntityName() == (entityName))
                .map(service -> (AbstractEntityService<E, M>) service)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity"));
    }
}
