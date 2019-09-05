package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.PersonAddress;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.PersonAddressModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PersonAddressService extends AbstractEntityService<PersonAddress, PersonAddressModel> {

    public PersonAddressService(final SyncEntityRepository<PersonAddress> repository,
                                final EntityToModelMapper<PersonAddress, PersonAddressModel> entityToModelMapper,
                                final ModelToEntityMapper<PersonAddressModel, PersonAddress> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PERSON_ADDRESS;
    }
}
