package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.Allergy;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.AllergyModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class AllergyService extends AbstractEntityService<Allergy, AllergyModel> {

    public AllergyService(final SyncEntityRepository<Allergy> repository,
                          final EntityToModelMapper<Allergy, AllergyModel> entityToModelMapper,
                          final ModelToEntityMapper<AllergyModel, Allergy> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.ALLERGY;
    }
}
