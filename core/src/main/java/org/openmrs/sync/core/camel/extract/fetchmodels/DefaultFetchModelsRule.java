package org.openmrs.sync.core.camel.extract.fetchmodels;

import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultFetchModelsRule implements FetchModelsRule {

    private EntityServiceFacade entityServiceFacade;

    public DefaultFetchModelsRule(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public boolean evaluate(final ComponentParams params) {
        return false;
    }

    @Override
    public List<BaseModel> getModels(final TableToSyncEnum tableToSync,
                                     final ComponentParams params) {
        return entityServiceFacade.getAllModels(tableToSync);
    }
}
