package org.cicr.sync.core.service.facade;

import org.cicr.sync.core.camel.EntityNameEnum;
import org.cicr.sync.core.entity.OpenMrsEty;
import org.cicr.sync.core.model.OpenMrsModel;
import org.cicr.sync.core.service.LoadEntityService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadEntityServiceFacade {

    private List<LoadEntityService<? extends OpenMrsEty, ? extends OpenMrsModel>> services;

    public LoadEntityServiceFacade(final List<LoadEntityService<? extends OpenMrsEty, ? extends OpenMrsModel>> services) {
        this.services = services;
    }

    public List<? extends OpenMrsModel> getModels(final EntityNameEnum entityNameEnum) {
        return getService(entityNameEnum).getModels();
    }

    private LoadEntityService<? extends OpenMrsEty, ? extends OpenMrsModel> getService(final EntityNameEnum entityName) {
        return services.stream()
                .filter(service -> service.getEntityName() == (entityName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity"));
    }
}
