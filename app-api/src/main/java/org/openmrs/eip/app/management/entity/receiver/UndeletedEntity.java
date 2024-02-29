package org.openmrs.eip.app.management.entity.receiver;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "undeleted_entity")
public class UndeletedEntity extends BaseUnSyncedEntity {}
