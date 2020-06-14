package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.entity.PersonName;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
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
