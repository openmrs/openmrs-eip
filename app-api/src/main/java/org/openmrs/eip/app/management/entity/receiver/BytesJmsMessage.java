package org.openmrs.eip.app.management.entity.receiver;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "bytes_jms_msg")
public class BytesJmsMessage extends BaseJmsMessage<byte[]> {
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(columnDefinition = "mediumblob", nullable = false, updatable = false)
	@NotNull
	private byte[] body;
	
	@Override
	public byte[] getBody() {
		return body;
	}
	
	@Override
	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
