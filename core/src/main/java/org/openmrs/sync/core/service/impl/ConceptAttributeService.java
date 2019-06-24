package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.ConceptAttribute;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.ConceptAttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.springframework.stereotype.Service;

@Service
public class ConceptAttributeService extends AbstractEntityService<ConceptAttribute, ConceptAttributeModel> {

    public ConceptAttributeService(final SyncEntityRepository<ConceptAttribute> repository,
                                   final EntityMapper<ConceptAttribute, ConceptAttributeModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.CONCEPT_ATTRIBUTE;
    }
}
