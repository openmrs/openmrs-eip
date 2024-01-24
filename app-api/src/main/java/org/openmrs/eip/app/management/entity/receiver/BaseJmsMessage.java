/*
 * Copyright (C) Amiyul LLC - All Rights Reserved
 *
 * This source code is protected under international copyright law. All rights
 * reserved and protected by the copyright holder.
 *
 * This file is confidential and only available to authorized individuals with the
 * permission of the copyright holder. If you encounter this file and do not have
 * permission, please contact the copyright holder and delete this file.
 */
package org.openmrs.eip.app.management.entity.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "jms_msg")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseJmsMessage<T> extends AbstractEntity {
	
	public enum MessageType {
		
		SYNC,
		
		RECONCILIATION
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private Long id;
	
	@Column(name = "site_id", nullable = false, updatable = false)
	@NotBlank
	@Getter
	@Setter
	private String siteId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "msg_type", nullable = false, length = 50)
	@Getter
	@Setter
	@NotNull
	private MessageType type;
	
	/**
	 * Gets the body
	 *
	 * @return the body
	 */
	public abstract T getBody();
	
	/**
	 * Sets the body
	 *
	 * @param body the body to set
	 */
	public abstract void setBody(T body);
}
