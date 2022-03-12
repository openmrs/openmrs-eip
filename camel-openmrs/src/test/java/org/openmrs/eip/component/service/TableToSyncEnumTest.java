package org.openmrs.eip.component.service;

import static org.junit.Assert.assertEquals;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.eip.component.MockedModel;
import org.openmrs.eip.component.entity.MockedEntity;
import org.openmrs.eip.component.entity.Person;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;

public class TableToSyncEnumTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void getTableToSyncEnum_should_return_enum() {
		// Given
		String nameString = "person";
		
		// When
		TableToSyncEnum result = TableToSyncEnum.getTableToSyncEnum(nameString);
		
		// Then
		assertEquals(TableToSyncEnum.PERSON, result);
	}
	
	@Test
	public void getTableToSyncEnum_with_model_class_should_return_enum() {
		// Given
		Class<PersonModel> personModelClass = PersonModel.class;
		
		// When
		TableToSyncEnum result = TableToSyncEnum.getTableToSyncEnum(personModelClass);
		
		// Then
		assertEquals(TableToSyncEnum.PERSON, result);
	}
	
	@Test(expected = EIPException.class)
	public void getTableToSyncEnum_with_model_class_should_throw_exception() {
		// Given
		Class<MockedModel> personModelClass = MockedModel.class;
		
		// When
		TableToSyncEnum result = TableToSyncEnum.getTableToSyncEnum(personModelClass);
		
		// Then
	}
	
	@Test
	public void getModelClass_should_return_model_class() {
		// Given
		Person person = new Person();
		
		// When
		Class result = TableToSyncEnum.getModelClass(person);
		
		// Then
		assertEquals(PersonModel.class, result);
	}
	
	@Test(expected = EIPException.class)
	public void getModelClass_should_throw_exception() {
		// Given
		MockedEntity mockedEntity = new MockedEntity(1L, "uuid");
		
		// When
		TableToSyncEnum.getModelClass(mockedEntity);
		
		// Then
	}
	
	@Test
	public void getTableToSyncEnumByModelClassName_shouldReturnTheMatchingEnum() {
		assertEquals(TableToSyncEnum.PERSON,
		    TableToSyncEnum.getTableToSyncEnumByModelClassName(PersonModel.class.getName()));
		assertEquals(TableToSyncEnum.PATIENT,
		    TableToSyncEnum.getTableToSyncEnumByModelClassName(PatientModel.class.getName()));
	}
	
	@Test
	public void getTableToSyncEnumByModelClassName_shouldFailIfNoMatchIsFound() {
		expectedException.expect(EIPException.class);
		expectedException
		        .expectMessage(CoreMatchers.equalTo("No enum found for model class name " + Person.class.getName()));
		TableToSyncEnum.getTableToSyncEnumByModelClassName(Person.class.getName());
	}
	
}
