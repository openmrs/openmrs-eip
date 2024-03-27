package org.openmrs.eip.fhir.spring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.openmrs.eip.Constants.PROP_WATCHED_TABLES;
import static org.openmrs.eip.fhir.Constants.URI_FHIR_ROUTER;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT_DESTINATIONS;

import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * We have some slightly gnarly code to convert the {@code eip.fhir.resources} property to some
 * properties for the watcher. Here we just ensure that this gets configured correctly if the
 * {@link OpenmrsFhirConfiguration} is loaded.
 */
@SpringBootTest
@ContextConfiguration(classes = OpenmrsFhirConfiguration.class)
@TestPropertySource(properties = { "eip.fhir.resources=Encounter", "openmrs.eip.log.level=DEBUG" })
public class OpenmrsFhirConfigurationTest {
	
	@Autowired
	Environment environment;
	
	@Test
	void shouldConfigureContextProperly() {
		assertThat(environment.getProperty(PROP_WATCHED_TABLES), equalTo(String.join(",", FhirResource.ENCOUNTER.tables())));
		assertThat(environment.getProperty(PROP_EVENT_DESTINATIONS), equalTo(URI_FHIR_ROUTER));
	}
}
