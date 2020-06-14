package org.openmrs.eip.component.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class ObservationRepositoryImpl implements ObservationRepositoryCustom {

    private ObservationRepository obsRepository;

    public ObservationRepositoryImpl(@Lazy final ObservationRepository obsRepository) {
        this.obsRepository = obsRepository;
    }

    @Override
    public boolean isObsLinkedToGivenConceptMapping(final String uuid, final String conceptMapping) {
        int isObsLinkedToGivenConceptMapping = this.obsRepository.isObsLinkedToGivenConceptMappingMySQL(uuid, conceptMapping);

        return isObsLinkedToGivenConceptMapping == 1;
    }
}
