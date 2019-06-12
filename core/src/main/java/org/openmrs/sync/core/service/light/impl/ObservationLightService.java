package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ObservationLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObservationLightService extends AbstractLightService<ObservationLight> {

    private PersonLightService personService;

    private ConceptLightService conceptService;

    public ObservationLightService(final OpenMrsRepository<ObservationLight> repository,
                                   final PersonLightService personService,
                                   final ConceptLightService conceptService) {
        super(repository);
        this.personService = personService;
        this.conceptService = conceptService;
    }

    @Override
    protected ObservationLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        ObservationLight observation = new ObservationLight();
        observation.setUuid(uuid);
        observation.setDateCreated(DEFAULT_DATE);
        observation.setCreator(DEFAULT_USER_ID);
        observation.setObsDatetime(DEFAULT_DATE);
        observation.setPerson(personService.getOrInit(AttributeHelper.getPersonUuid(uuids)));
        observation.setConcept(conceptService.getOrInit(AttributeHelper.getConceptUuid(uuids)));
        return observation;
    }
}
