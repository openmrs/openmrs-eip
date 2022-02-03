package org.openmrs.eip.app.management.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "patient_state_hash")
public class PatientStateHash extends BaseHashEntity {}
