package org.openmrs.eip.component.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class ObservationRepositoryImpl {
	
	private ObservationRepository obsRepository;
	
	public ObservationRepositoryImpl(@Lazy final ObservationRepository obsRepository) {
		this.obsRepository = obsRepository;
	}
	
}
