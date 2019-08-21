package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.PersonAttribute;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.common.model.sync.AttributeModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
