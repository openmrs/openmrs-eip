package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.Concept;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.ConceptModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ConceptService extends AbstractEntityService<Concept, ConceptModel> {

    public ConceptService(final SyncEntityRepository<Concept> repository,
                          final EntityToModelMapper<Concept, ConceptModel> entityToModelMapper,
                          final ModelToEntityMapper<ConceptModel, Concept> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.CONCEPT;
    }
}
