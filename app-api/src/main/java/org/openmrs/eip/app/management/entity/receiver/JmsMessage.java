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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "jms_msg")
public class JmsMessage extends AbstractEntity {
	
	public enum MessageType {
		
		SYNC,
		
		RECONCILE
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private Long id;
	
	@Column(name = "site_id", updatable = false)
	@Getter
	@Setter
	private String siteId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "msg_type", nullable = false, length = 50)
	@Getter
	@Setter
	@NotNull
	private MessageType type;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(columnDefinition = "mediumblob", nullable = false, updatable = false)
	@NotNull
	@Getter
	@Setter
	private byte[] body;
	
	@Column(name = "msg_id", length = 38, updatable = false)
	@Getter
	@Setter
	private String messageId;
	
}
