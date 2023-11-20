package org.openmrs.eip.component.management.hash.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "encounter_provider_hash")
public class EncounterProviderHash extends BaseHashEntity {}
