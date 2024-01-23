package org.openmrs.eip.app.management.entity.receiver;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "text_jms_msg")
public class TextJmsMessage extends BaseJmsMessage<String> {
	
	@Column(columnDefinition = "text", nullable = false, updatable = false)
	@NotBlank
	private String body;
	
	@Override
	public String getBody() {
		return body;
	}
	
	@Override
	public void setBody(String body) {
		this.body = body;
	}
	
}
