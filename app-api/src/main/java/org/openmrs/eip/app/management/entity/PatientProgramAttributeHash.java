package org.openmrs.eip.app.management.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "patient_program_attribute_hash")
public class PatientProgramAttributeHash extends BaseHashEntity {}
