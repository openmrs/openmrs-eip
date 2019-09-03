package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.PersonName;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.PersonNameModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PersonNameService extends AbstractEntityService<PersonName, PersonNameModel> {

    public PersonNameService(final SyncEntityRepository<PersonName> repository,
                             final EntityToModelMapper<PersonName, PersonNameModel> entityToModelMapper,
                             final ModelToEntityMapper<PersonNameModel, PersonName> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PERSON_NAME;
    }
}
