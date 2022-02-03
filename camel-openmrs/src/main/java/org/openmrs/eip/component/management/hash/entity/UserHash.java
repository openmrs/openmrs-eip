package org.openmrs.eip.component.management.hash.entity;

import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users_hash")
public class UserHash extends BaseHashEntity {}
