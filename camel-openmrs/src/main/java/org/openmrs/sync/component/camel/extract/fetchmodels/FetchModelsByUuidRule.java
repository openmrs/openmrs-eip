package org.openmrs.sync.component.camel.extract.fetchmodels;

import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FetchModelsByUuidRule implements FetchModelsRule {

    private EntityServiceFacade entityServiceFacade;

    public FetchModelsByUuidRule(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public boolean evaluate(final ComponentParams params) {
        return params.getUuid() != null;
    }

    @Override
    public List<BaseModel> getModels(final TableToSyncEnum tableToSync,
                                     final ComponentParams params) {
        return Collections.singletonList(entityServiceFacade.getModel(tableToSync, params.getUuid()));
    }
}
