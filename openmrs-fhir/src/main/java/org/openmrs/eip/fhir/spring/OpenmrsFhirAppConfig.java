package org.openmrs.eip.fhir.spring;

import org.openmrs.eip.app.config.AppConfig;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot root configuration for a Spring Boot app that uses the openmrs-fhir component. Just
 * combines the base AppConfig with FHIR-specific configuration.
 */
@Import({ AppConfig.class, OpenmrsFhirConfiguration.class })
public class OpenmrsFhirAppConfig {}
