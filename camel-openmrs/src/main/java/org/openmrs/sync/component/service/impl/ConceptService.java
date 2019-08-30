package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.Concept;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.ConceptModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
