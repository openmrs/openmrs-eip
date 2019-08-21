package org.openmrs.sync.component.camel.extract.fetchmodels;

import org.openmrs.sync.common.model.sync.BaseModel;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FetchModelsByLastSyncDateRule implements FetchModelsRule {

    private EntityServiceFacade entityServiceFacade;

    public FetchModelsByLastSyncDateRule(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public boolean evaluate(final ComponentParams params) {
        return params.getLastSyncDate() != null;
    }

    @Override
    public List<BaseModel> getModels(final TableToSyncEnum tableToSync,
                                     final ComponentParams params) {
        return entityServiceFacade.getModelsAfterDate(tableToSync, params.getLastSyncDate());
    }
}
