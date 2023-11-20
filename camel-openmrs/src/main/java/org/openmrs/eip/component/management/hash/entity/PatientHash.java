package org.openmrs.eip.component.management.hash.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "patient_hash")
public class PatientHash extends BaseHashEntity {}
