package org.openmrs.eip.app.management.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "encounter_provider_hash")
public class EncounterProviderHash extends BaseHashEntity {}
