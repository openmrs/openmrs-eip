package org.openmrs.sync.component.camel.fetchmodels;

import org.openmrs.sync.component.camel.ProducerParams;
import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FetchModelsByIdRule implements FetchModelsRule {

    private EntityServiceFacade entityServiceFacade;

    public FetchModelsByIdRule(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public boolean evaluate(final ProducerParams params) {
        return params.getId() != null;
    }

    @Override
    public List<BaseModel> getModels(final ProducerParams params) {
        return Collections.singletonList(entityServiceFacade.getModel(params.getTableToSync(), params.getId()));
    }
}
