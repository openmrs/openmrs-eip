package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.PersonAttribute;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PersonAttributeService extends AbstractEntityService<PersonAttribute, AttributeModel> {

    public PersonAttributeService(final SyncEntityRepository<PersonAttribute> repository,
                                  final EntityToModelMapper<PersonAttribute, AttributeModel> entityToModelMapper,
                                  final ModelToEntityMapper<AttributeModel, PersonAttribute> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PERSON_ATTRIBUTE;
    }
}
