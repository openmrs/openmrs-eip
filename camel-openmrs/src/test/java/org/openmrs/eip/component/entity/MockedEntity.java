package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MockedEntity extends BaseChangeableDataEntity {
	
	private String field1;
	
	private String field2;
	
	private MockedLightEntity linkedEntity;
	
	public MockedEntity(final Long id, final String uuid) {
		this.setId(id);
		this.setUuid(uuid);
	}
}
