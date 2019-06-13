package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ObservationLight;
import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ObservationContext;
import org.springframework.stereotype.Service;

@Service
public class ObservationLightService extends AbstractLightService<ObservationLight, ObservationContext> {

    private LightServiceNoContext<PersonLight> personService;

    private LightService<ConceptLight, ConceptContext> conceptService;

    public ObservationLightService(final OpenMrsRepository<ObservationLight> repository,
                                   final LightServiceNoContext<PersonLight> personService,
                                   final LightService<ConceptLight, ConceptContext> conceptService) {
        super(repository);
        this.personService = personService;
        this.conceptService = conceptService;
    }

    @Override
    protected ObservationLight getShadowEntity(final String uuid, final ObservationContext context) {
        ObservationLight observation = new ObservationLight();
        observation.setUuid(uuid);
        observation.setDateCreated(DEFAULT_DATE);
        observation.setCreator(DEFAULT_USER_ID);
        observation.setObsDatetime(DEFAULT_DATE);
        observation.setPerson(personService.getOrInit(context.getPersonUuid()));
        observation.setConcept(conceptService.getOrInit(context.getConceptUuid(), getConceptContext(context)));
        return observation;
    }

    private ConceptContext getConceptContext(final ObservationContext context) {
        return ConceptContext.builder()
                .conceptClassUuid(context.getConceptClassUuid())
                .conceptDatatypeUuid(context.getConceptDatatypeUuid())
                .build();
    }
}
