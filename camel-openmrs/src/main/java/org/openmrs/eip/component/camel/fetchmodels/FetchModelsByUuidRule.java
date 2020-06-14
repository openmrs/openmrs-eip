package org.openmrs.eip.component.camel.fetchmodels;

import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.camel.ProducerParams;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
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
    public boolean evaluate(final ProducerParams params) {
        return params.getUuid() != null;
    }

    @Override
    public List<BaseModel> getModels(final ProducerParams params) {
        return Collections.singletonList(entityServiceFacade.getModel(params.getTableToSync(), params.getUuid()));
    }
}
