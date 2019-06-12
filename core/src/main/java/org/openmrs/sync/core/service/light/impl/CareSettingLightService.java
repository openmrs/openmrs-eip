package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.CareSettingLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareSettingLightService extends AbstractLightService<CareSettingLight> {

    public CareSettingLightService(final OpenMrsRepository<CareSettingLight> repository) {
        super(repository);
    }

    @Override
    protected CareSettingLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        CareSettingLight careSetting = new CareSettingLight();
        careSetting.setUuid(uuid);
        careSetting.setDateCreated(DEFAULT_DATE);
        careSetting.setCreator(DEFAULT_USER_ID);
        careSetting.setCareSettingType(DEFAULT_STRING);
        careSetting.setName(DEFAULT_STRING);
        return careSetting;
    }
}
