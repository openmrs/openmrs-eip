package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Concept;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.ConceptModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ConceptService extends AbstractEntityService<Concept, ConceptModel> {

    public ConceptService(final SyncEntityRepository<Concept> repository,
                          final EntityMapper<Concept, ConceptModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.CONCEPT;
    }
}
