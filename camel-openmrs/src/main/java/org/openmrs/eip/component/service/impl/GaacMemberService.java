package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.GaacMember;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.GaacMemberModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class GaacMemberService extends AbstractEntityService<GaacMember, GaacMemberModel> {
	
	public GaacMemberService(final SyncEntityRepository<GaacMember> repository,
	    final EntityToModelMapper<GaacMember, GaacMemberModel> entityToModelMapper,
	    final ModelToEntityMapper<GaacMemberModel, GaacMember> modelToEntityMapper) {
		
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.GAAC_MEMBER;
	}
	
}
