package org.openmrs.eip.app.management.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users_hash")
public class UserHash extends BaseHashEntity {}
