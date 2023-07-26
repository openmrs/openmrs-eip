package org.openmrs.eip.component.utils;

import static java.time.ZoneId.systemDefault;
import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_CLASS;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_UUID;
import static org.openmrs.eip.component.Constants.QUERY_GET_HASH;
import static org.openmrs.eip.component.Constants.QUERY_SAVE_HASH;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class HashUtilsTest {
	
	private final String EXPECTED_HASH = "05558ccafade5c5194e6849f87dfad95";
	
	private final String CREATOR = UserLight.class.getName() + "(1cc6880e-4d46-11e4-9138-a6c5e4d20fb8)";
	
	private final String GENDER = "F";
	
	private final String UUID = "818b4ee6-8d68-4849-975d-80ab98016677";
	
	@Mock
	private ProducerTemplate mockTemplate;
	
	@Test
	public void computeHash_shouldReturnTheMd5HashOfTheEntityPayload() {
		PersonModel model = new PersonModel();
		model.setGender(GENDER);
		model.setCreatorUuid(CREATOR);
		model.setUuid(UUID);
		
		assertEquals(EXPECTED_HASH, HashUtils.computeHash(model));
	}
	
	@Test
	public void computeHash_shouldReturnTheMd5HashOfTheEntityPayloadIgnoringWhitespacesInValues() {
		PersonModel model = new PersonModel();
		model.setGender(GENDER);
		model.setCreatorUuid(CREATOR);
		model.setUuid(UUID);
		model.setVoidReason(" ");
		
		assertEquals(EXPECTED_HASH, HashUtils.computeHash(model));
	}
	
	@Test
	public void getDatetimePropertyNames_shouldReturnTheListOfAllDatetimePropertyNamesOnTheModelClass() {
		Set<String> dateProps = HashUtils.getDatetimePropertyNames(PersonModel.class);
		assertEquals(3, dateProps.size());
		assertTrue(dateProps.contains("dateCreated"));
		assertTrue(dateProps.contains("dateVoided"));
		assertTrue(dateProps.contains("dateChanged"));
		
		dateProps = HashUtils.getDatetimePropertyNames(VisitModel.class);
		assertEquals(5, dateProps.size());
		assertTrue(dateProps.contains("dateCreated"));
		assertTrue(dateProps.contains("dateVoided"));
		assertTrue(dateProps.contains("dateChanged"));
		assertTrue(dateProps.contains("dateStarted"));
		assertTrue(dateProps.contains("dateStopped"));
	}
	
	@Test
	public void computeHash_shouldNormalizeDatetimeFieldsToMillisecondsSinceTheEpoch() {
		PersonModel model = new PersonModel();
		model.setGender(GENDER);
		model.setCreatorUuid(CREATOR);
		model.setUuid(UUID);
		LocalDateTime dateVoided = parse("2021-10-06T08:00:00-02:00", ISO_OFFSET_DATE_TIME)
		        .withZoneSameInstant(systemDefault()).toLocalDateTime();
		model.setDateVoided(dateVoided);
		
		assertEquals("93c36578eb50437b9a856a57e12c05cc", HashUtils.computeHash(model));
	}
	
	@Test
	public void saveHash_shouldSaveTheEntityHash() {
		PersonHash h = new PersonHash();
		
		HashUtils.saveHash(h, mockTemplate, false);
		
		verify(mockTemplate).sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, h.getClass().getSimpleName()), h);
	}
	
	@Test
	public void saveHash_shouldGracefullyHandleExceptionIfEntityHashAlreadyExists() {
		final String className = PersonHash.class.getSimpleName();
		final String uuid = "test-uuid";
		PersonHash newHash = new PersonHash();
		final String newHashString = "new-hash";
		newHash.setIdentifier(uuid);
		newHash.setHash(newHashString);
		newHash.setDateCreated(LocalDateTime.now());
		ConstraintViolationException cause = new ConstraintViolationException("test", null, "constraint-name");
		CamelExecutionException e = new CamelExecutionException("test", null, new PersistenceException(cause));
		doThrow(e).when(mockTemplate).sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, className), newHash);
		PersonHash existingHash = new PersonHash();
		existingHash.setHash("old-hash");
		Assert.assertNull(existingHash.getDateChanged());
		when(mockTemplate.requestBody(QUERY_GET_HASH.replace(PLACEHOLDER_CLASS, className).replace(PLACEHOLDER_UUID, uuid),
		    null, List.class)).thenReturn(Collections.singletonList(existingHash));
		
		HashUtils.saveHash(newHash, mockTemplate, true);
		
		assertEquals(newHashString, existingHash.getHash());
		assertEquals(newHash.getDateCreated(), existingHash.getDateChanged());
		verify(mockTemplate).sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, className), existingHash);
	}
	
	@Test
	public void saveHash_shouldFailForAConstraintViolationAndNoExistingHashIsFound() {
		final String className = PersonHash.class.getSimpleName();
		final String uuid = "test-uuid";
		PersonHash newHash = new PersonHash();
		newHash.setIdentifier(uuid);
		ConstraintViolationException cause = new ConstraintViolationException("test", null, "constraint-name");
		CamelExecutionException e = new CamelExecutionException("test", null, new PersistenceException(cause));
		doThrow(e).when(mockTemplate).sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, className), newHash);
		
		Throwable t = assertThrows(CamelExecutionException.class, () -> HashUtils.saveHash(newHash, mockTemplate, true));
		
		assertEquals(e.getMessage(), t.getMessage());
		verify(mockTemplate).sendBody(eq(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, className)), any());
	}
	
	@Test
	public void saveHash_shouldFailForADuplicateHash() {
		final String className = PersonHash.class.getSimpleName();
		final String uuid = "test-uuid";
		PersonHash h = new PersonHash();
		h.setIdentifier(uuid);
		
		ConstraintViolationException cause = new ConstraintViolationException("test", null, "constraint-name");
		CamelExecutionException e = new CamelExecutionException("test", null, new PersistenceException(cause));
		doThrow(e).when(mockTemplate).sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, className), h);
		
		Throwable t = assertThrows(CamelExecutionException.class, () -> HashUtils.saveHash(h, mockTemplate, false));
		
		assertEquals(e.getMessage(), t.getMessage());
		verify(mockTemplate, never()).requestBody(
		    QUERY_GET_HASH.replace(PLACEHOLDER_CLASS, className).replace(PLACEHOLDER_UUID, uuid), null, List.class);
	}
	
}
