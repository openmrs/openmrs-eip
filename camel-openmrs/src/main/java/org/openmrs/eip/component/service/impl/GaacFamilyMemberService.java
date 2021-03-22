package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.GaacFamilyMember;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.GaacFamilyMemberModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class GaacFamilyMemberService extends AbstractEntityService<GaacFamilyMember, GaacFamilyMemberModel> {

    public GaacFamilyMemberService(final SyncEntityRepository<GaacFamilyMember> repository,
                            final EntityToModelMapper<GaacFamilyMember, GaacFamilyMemberModel> entityToModelMapper,
                            final ModelToEntityMapper<GaacFamilyMemberModel, GaacFamilyMember> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.GAAC_FAMILY_MEMBER;
    }
}
