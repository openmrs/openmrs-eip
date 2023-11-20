package org.openmrs.eip.component.management.hash.entity;

import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_hash")
public class UserHash extends BaseHashEntity {}
