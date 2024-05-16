/**
 * This module is used for the individual FHIR Resource routes.
 * <p/>
 * Our assumption is that each route will receive all messages basically raw from the OpenMRS
 * Watcher and are responsible for converting them to FHIR resources, which are then sent
 */
package org.openmrs.eip.fhir.routes.resources;
