package org.openmrs.sync.component.camel.fetchmodels;

import org.openmrs.sync.component.camel.ProducerParams;
import org.openmrs.sync.component.model.BaseModel;
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
    public boolean evaluate(final ProducerParams params) {
        return params.getLastSyncDate() != null;
    }

    @Override
    public List<BaseModel> getModels(final ProducerParams params) {
        return entityServiceFacade.getModelsAfterDate(params.getTableToSync(), params.getLastSyncDate());
    }
}
