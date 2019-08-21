package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.ConceptAttribute;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.common.model.sync.ConceptAttributeModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ConceptAttributeService extends AbstractEntityService<ConceptAttribute, ConceptAttributeModel> {

    public ConceptAttributeService(final SyncEntityRepository<ConceptAttribute> repository,
                                   final EntityToModelMapper<ConceptAttribute, ConceptAttributeModel> entityToModelMapper,
                                   final ModelToEntityMapper<ConceptAttributeModel, ConceptAttribute> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.CONCEPT_ATTRIBUTE;
    }
}
