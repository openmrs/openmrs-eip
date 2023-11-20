package org.openmrs.eip.component.management.hash.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "patient_identifier_hash")
public class PatientIdentifierHash extends BaseHashEntity {}
