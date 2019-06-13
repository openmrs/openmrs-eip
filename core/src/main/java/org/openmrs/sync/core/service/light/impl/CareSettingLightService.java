package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.CareSettingLight;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.stereotype.Service;

@Service
public class CareSettingLightService extends AbstractLightServiceNoContext<CareSettingLight> {

    public CareSettingLightService(final OpenMrsRepository<CareSettingLight> repository) {
        super(repository);
    }

    @Override
    protected CareSettingLight getShadowEntity(final String uuid) {
        CareSettingLight careSetting = new CareSettingLight();
        careSetting.setUuid(uuid);
        careSetting.setDateCreated(DEFAULT_DATE);
        careSetting.setCreator(DEFAULT_USER_ID);
        careSetting.setCareSettingType(DEFAULT_STRING);
        careSetting.setName(DEFAULT_STRING);
        return careSetting;
    }
}
