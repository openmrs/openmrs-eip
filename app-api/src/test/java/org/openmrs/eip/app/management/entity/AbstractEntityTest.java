package org.openmrs.eip.app.management.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

public class AbstractEntityTest {
	
	class MockEntity extends AbstractEntity {}
	
	class OtherEntity extends AbstractEntity {}
	
	@Test
	public void equals_shouldReturnFalseIfOtherObjectIsNull() {
		assertFalse(new MockEntity().equals(null));
	}
	
	@Test
	public void equals_shouldReturnFalseIfThisObjectHasNoIdAndTheOtherDoes() {
		AbstractEntity obj = new MockEntity();
		assertNull(obj.getId());
		AbstractEntity other = new MockEntity();
		other.setId(2L);
		assertFalse(obj.equals(other));
	}
	
	@Test
	public void equals_shouldReturnFalseIfThisObjectHasAnIdAndTheOtherDoesNot() {
		AbstractEntity obj = new MockEntity();
		obj.setId(1L);
		AbstractEntity other = new MockEntity();
		assertNull(other.getId());
		assertFalse(obj.equals(other));
	}
	
	@Test
	public void equals_shouldReturnFalseIfOtherObjectHasTheSameIdButOfDifferentType() {
		final Long id = 7L;
		AbstractEntity obj = new MockEntity();
		obj.setId(id);
		AbstractEntity other = new OtherEntity();
		other.setId(id);
		assertFalse(obj.equals(other));
	}
	
	@Test
	public void equals_shouldReturnFalseForObjectsOfTheSameTypeButDifferentIds() {
		AbstractEntity obj = new MockEntity();
		obj.setId(1L);
		AbstractEntity other = new MockEntity();
		other.setId(2L);
		assertFalse(obj.equals(other));
	}
	
	@Test
	public void equals_shouldReturnFalseForDifferentObjectsAndBothHaveNullIds() {
		AbstractEntity obj = new MockEntity();
		assertNull(obj.getId());
		AbstractEntity other = new MockEntity();
		assertNull(other.getId());
		assertFalse(obj.equals(other));
	}
	
	@Test
	public void equals_shouldReturnTrueForSameObjectsAndBothHaveNullIds() {
		AbstractEntity obj = new MockEntity();
		assertNull(obj.getId());
		assertTrue(obj.equals(obj));
	}
	
	@Test
	public void equals_shouldReturnTheIdHashCode() {
		final Long id = 7L;
		AbstractEntity obj = new MockEntity();
		obj.setId(id);
		Assert.assertEquals(id.hashCode(), obj.hashCode());
	}
	
	@Test
	public void equals_shouldReturnTheObjectHashCodeIfIdIsNull() {
		AbstractEntity obj = new MockEntity();
		assertNull(obj.getId());
		Assert.assertEquals(Objects.hashCode(obj), obj.hashCode());
	}
	
}
