package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Concept;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.ConceptModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
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
