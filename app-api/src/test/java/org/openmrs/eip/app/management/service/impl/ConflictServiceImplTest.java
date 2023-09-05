package org.openmrs.eip.app.management.service.impl;

import static java.time.LocalDateTime.of;
import static java.time.Month.AUGUST;
import static java.util.Collections.singleton;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.IGNORE_NEW;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.MERGE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_ENTITY_ID;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_IS_CONFLICT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MODEL_CLASS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.FIELD_RETIRED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.FIELD_VOIDED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.MERGE_EXCLUDE_FIELDS;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.ws.Holder;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ConflictResolution;
import org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.BaseDataModel;
import org.openmrs.eip.component.model.BaseMetadataModel;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.ObservationModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.ProviderModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, CamelUtils.class })
public class ConflictServiceImplTest {
	
	private ConflictServiceImpl service;
	
	@Mock
	private ReceiverService mockReceiverService;
	
	@Mock
	private EntityServiceFacade mockServiceFacade;
	
	@Mock
	private ExtendedCamelContext mockContext;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(CamelUtils.class);
		PowerMockito.mockStatic(SyncContext.class);
		service = new ConflictServiceImpl(null, null, null, mockReceiverService, mockContext, mockServiceFacade, null);
		when(SyncContext.getBean(ConflictService.class)).thenReturn(service);
	}
	
	@Test
	public void resolve_shouldFailIfConflictIsNull() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> service.resolve(new ConflictResolution(null, IGNORE_NEW)));
		assertEquals("Conflict is required", thrown.getMessage());
	}
	
	@Test
	public void resolve_shouldFailIfResolutionIsNull() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> service.resolve(new ConflictResolution(new ConflictQueueItem(), null)));
		assertEquals("Resolution is required", thrown.getMessage());
	}
	
	@Test
	public void resolve_shouldMoveTheItemToTheArchivesIfDecisionIsSetToIgnoreNew() throws Exception {
		service = Mockito.spy(service);
		ConflictQueueItem conflict = new ConflictQueueItem();
		Holder<Boolean> holder = new Holder();
		Mockito.doAnswer(invocation -> {
			holder.value = true;
			return null;
		}).when(service).moveToArchiveQueue(conflict);
		
		service.resolve(new ConflictResolution(conflict, IGNORE_NEW));
		
		assertTrue(holder.value);
	}
	
	@Test
	public void resolve_shouldUpdateTheHashAndMoveTheItemToTheRetryQueueIfDecisionIsSetToSyncNew() throws Exception {
		final String modelClassName = PersonModel.class.getName();
		final String uuid = "person-uuid";
		service = Mockito.spy(service);
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setModelClassName(modelClassName);
		conflict.setIdentifier(uuid);
		Holder<Boolean> holder = new Holder();
		Mockito.doAnswer(invocation -> {
			holder.value = true;
			return null;
		}).when(service).moveToRetryQueue(conflict, "Moved from conflict queue after conflict resolution");
		
		service.resolve(new ConflictResolution(conflict, ResolutionDecision.SYNC_NEW));
		
		Mockito.verify(mockReceiverService).updateHash(modelClassName, uuid);
		assertTrue(holder.value);
	}
	
	@Test
	public void resolve_shouldFailForAMergeResolutionAndNoSyncedPropertiesSpecified() {
		Throwable thrown = Assert.assertThrows(EIPException.class,
		    () -> service.resolve(new ConflictResolution(new ConflictQueueItem(), MERGE)));
		assertEquals("No properties to sync specified for merge resolution decision", thrown.getMessage());
	}
	
	@Test
	public void resolve_shouldFailForAMergeResolutionAndSyncedPropertiesContainsAnyDisallowedProperty() {
		ConflictResolution resolution = new ConflictResolution(new ConflictQueueItem(), MERGE);
		resolution.addPropertyToSync("dateChanged");
		Throwable thrown = Assert.assertThrows(EIPException.class, () -> service.resolve(resolution));
		assertEquals("Found invalid properties for a merge conflict resolution, please exclude: " + MERGE_EXCLUDE_FIELDS,
		    thrown.getMessage());
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldSkipIfVoidedFieldIsNotSynced() {
		BaseDataModel dbModel = Mockito.mock(BaseDataModel.class);
		BaseDataModel newModel = Mockito.mock(BaseDataModel.class);
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, Collections.emptySet());
		
		verifyNoInteractions(dbModel);
		verifyNoInteractions(newModel);
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldSkipIfRetiredFieldIsNotSynced() {
		BaseMetadataModel dbModel = Mockito.mock(BaseMetadataModel.class);
		BaseMetadataModel newModel = Mockito.mock(BaseMetadataModel.class);
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, Collections.emptySet());
		
		verifyNoInteractions(dbModel);
		verifyNoInteractions(newModel);
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldSkipForAModelThatIsNeitherDataNorMetadata() {
		BaseModel dbModel = Mockito.mock(BaseModel.class);
		BaseModel newModel = Mockito.mock(BaseModel.class);
		Set<String> propsToSync = new HashSet<>();
		propsToSync.add(FIELD_VOIDED);
		propsToSync.add(FIELD_RETIRED);
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, propsToSync);
		
		verifyNoInteractions(dbModel);
		verifyNoInteractions(newModel);
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldReplaceDbFieldsIfTheNewStateIsVoidedAndDbStateIsNot() {
		final String newUser = "User(user-uuid)";
		final LocalDateTime newDate = LocalDateTime.now();
		final String newReason = "test";
		final String newPatientUser = "User(patient-user-uuid)";
		final LocalDateTime newPatientDate = LocalDateTime.now();
		final String newPatientReason = "patient-test";
		PatientModel dbModel = new PatientModel();
		PatientModel newModel = new PatientModel();
		newModel.setVoided(true);
		newModel.setVoidedByUuid(newUser);
		newModel.setDateVoided(newDate);
		newModel.setVoidReason(newReason);
		newModel.setPatientVoided(true);
		newModel.setPatientVoidedByUuid(newPatientUser);
		newModel.setPatientDateVoided(newPatientDate);
		newModel.setPatientVoidReason(newPatientReason);
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, singleton(FIELD_VOIDED));
		
		assertEquals(newUser, dbModel.getVoidedByUuid());
		assertEquals(newDate, dbModel.getDateVoided());
		assertEquals(newReason, dbModel.getVoidReason());
		assertEquals(newPatientUser, dbModel.getPatientVoidedByUuid());
		assertEquals(newPatientDate, dbModel.getPatientDateVoided());
		assertEquals(newPatientReason, dbModel.getPatientVoidReason());
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldNotReplaceVoidFieldsWithEmptyData() {
		final String dbUser = "db-User(user-uuid)";
		final String dbReason = "db-test";
		final String dbPatientUser = "db-User(patient-user-uuid)";
		final String dbPatientReason = "patient-db-test";
		PatientModel dbModel = new PatientModel();
		dbModel.setVoidedByUuid(dbUser);
		dbModel.setVoidReason(dbReason);
		dbModel.setPatientVoidedByUuid(dbPatientUser);
		dbModel.setPatientVoidReason(dbPatientReason);
		PatientModel newModel = new PatientModel();
		newModel.setVoided(true);
		newModel.setVoidReason(" ");
		newModel.setPatientVoided(true);
		newModel.setPatientVoidReason(" ");
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, singleton(FIELD_VOIDED));
		
		assertEquals(dbUser, dbModel.getVoidedByUuid());
		assertEquals(dbReason, dbModel.getVoidReason());
		assertEquals(dbPatientUser, dbModel.getPatientVoidedByUuid());
		assertEquals(dbPatientReason, dbModel.getPatientVoidReason());
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldSkipIfTheNewStateIsVoidedButDateVoidedIsBeforeThatFromTheDb() {
		final String dbUser = "db-User(user-uuid)";
		final LocalDateTime dbDate = of(2023, AUGUST, 23, 00, 00, 01);
		final String dbReason = "db-test";
		final String dbPatientUser = "db-User(patient-user-uuid)";
		final LocalDateTime dbPatientDate = of(2023, AUGUST, 24, 00, 00, 01);
		final String dbPatientReason = "patient-db-test";
		PatientModel dbModel = new PatientModel();
		dbModel.setVoidedByUuid(dbUser);
		dbModel.setDateVoided(dbDate);
		dbModel.setVoidReason(dbReason);
		dbModel.setPatientVoidedByUuid(dbPatientUser);
		dbModel.setPatientDateVoided(dbPatientDate);
		dbModel.setPatientVoidReason(dbPatientReason);
		PatientModel newModel = new PatientModel();
		newModel.setVoided(true);
		newModel.setVoidedByUuid("User(user-uuid)");
		newModel.setDateVoided(of(2023, AUGUST, 23, 00, 00, 00));
		newModel.setVoidReason("test");
		newModel.setPatientVoided(true);
		newModel.setPatientVoidedByUuid("User(patient-user-uuid)");
		newModel.setPatientDateVoided(of(2023, AUGUST, 24, 00, 00, 00));
		newModel.setPatientVoidReason("patient-test");
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, singleton(FIELD_VOIDED));
		
		assertEquals(dbUser, dbModel.getVoidedByUuid());
		assertEquals(dbDate, dbModel.getDateVoided());
		assertEquals(dbReason, dbModel.getVoidReason());
		assertEquals(dbPatientUser, dbModel.getPatientVoidedByUuid());
		assertEquals(dbPatientDate, dbModel.getPatientDateVoided());
		assertEquals(dbPatientReason, dbModel.getPatientVoidReason());
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldClearDbFieldsIfTheNewStateIsNotVoided() {
		PatientModel dbModel = new PatientModel();
		dbModel.setVoidedByUuid("db-User(user-uuid)");
		dbModel.setDateVoided(of(2023, AUGUST, 23, 00, 00, 00));
		dbModel.setVoidReason("db-test");
		dbModel.setPatientVoidedByUuid("db-User(user-uuid)");
		dbModel.setPatientDateVoided(of(2023, AUGUST, 23, 00, 00, 00));
		dbModel.setPatientVoidReason("db-test");
		PatientModel newModel = new PatientModel();
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, singleton(FIELD_VOIDED));
		
		assertNull(dbModel.getVoidedByUuid());
		assertNull(dbModel.getDateVoided());
		assertNull(dbModel.getVoidReason());
		assertNull(dbModel.getPatientVoidedByUuid());
		assertNull(dbModel.getPatientDateVoided());
		assertNull(dbModel.getPatientVoidReason());
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldReplaceDbFieldsIfTheNewStateIsRetiredAndDbStateIsNot() {
		final String newUser = "User(user-uuid)";
		final LocalDateTime newDate = LocalDateTime.now();
		final String newReason = "test";
		ProviderModel dbModel = new ProviderModel();
		ProviderModel newModel = new ProviderModel();
		newModel.setRetired(true);
		newModel.setRetiredByUuid(newUser);
		newModel.setDateRetired(newDate);
		newModel.setRetireReason(newReason);
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, singleton(FIELD_RETIRED));
		
		assertEquals(newUser, dbModel.getRetiredByUuid());
		assertEquals(newDate, dbModel.getDateRetired());
		assertEquals(newReason, dbModel.getRetireReason());
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldNotReplaceRetireFieldsWithEmptyData() {
		final String dbUser = "db-User(user-uuid)";
		final String dbReason = "db-test";
		ProviderModel dbModel = new ProviderModel();
		dbModel.setRetiredByUuid(dbUser);
		dbModel.setRetireReason(dbReason);
		ProviderModel newModel = new ProviderModel();
		newModel.setRetired(true);
		newModel.setRetireReason(" ");
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, singleton(FIELD_RETIRED));
		
		assertEquals(dbUser, dbModel.getRetiredByUuid());
		assertEquals(dbReason, dbModel.getRetireReason());
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldSkipIfTheNewStateIsRetiredButDateRetiredIsBeforeThatFromTheDb() {
		final String dbUser = "db-User(user-uuid)";
		final LocalDateTime dbDate = of(2023, AUGUST, 23, 00, 00, 01);
		final String dbReason = "db-test";
		ProviderModel dbModel = new ProviderModel();
		dbModel.setRetiredByUuid(dbUser);
		dbModel.setDateRetired(dbDate);
		dbModel.setRetireReason(dbReason);
		ProviderModel newModel = new ProviderModel();
		newModel.setRetired(true);
		newModel.setRetiredByUuid("User(user-uuid)");
		newModel.setDateRetired(of(2023, AUGUST, 23, 00, 00, 00));
		newModel.setRetireReason("test");
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, singleton(FIELD_RETIRED));
		
		assertEquals(dbUser, dbModel.getRetiredByUuid());
		assertEquals(dbDate, dbModel.getDateRetired());
		assertEquals(dbReason, dbModel.getRetireReason());
	}
	
	@Test
	public void mergeVoidOrRetireProperties_shouldClearDbFieldsIfTheNewStateIsNotRetired() {
		ProviderModel dbModel = new ProviderModel();
		dbModel.setRetiredByUuid("db-User(user-uuid)");
		dbModel.setDateRetired(of(2023, AUGUST, 23, 00, 00, 00));
		dbModel.setRetireReason("db-test");
		ProviderModel newModel = new ProviderModel();
		
		service.mergeVoidOrRetireProperties(dbModel, newModel, singleton(FIELD_RETIRED));
		
		assertNull(dbModel.getRetiredByUuid());
		assertNull(dbModel.getDateRetired());
		assertNull(dbModel.getRetireReason());
	}
	
	@Test
	public void mergeAuditProperties_shouldSkipForAModelThatIsNotAuditable() {
		ObservationModel dbModel = Mockito.mock(ObservationModel.class);
		ObservationModel newModel = Mockito.mock(ObservationModel.class);
		
		service.mergeAuditProperties(dbModel, newModel);
		
		verifyNoInteractions(dbModel);
		verifyNoInteractions(newModel);
	}
	
	@Test
	public void mergeAuditProperties_shouldReplaceDbFieldsWithTheNewStateForData() {
		final String newUser = "User(user-uuid)";
		final LocalDateTime newDate = LocalDateTime.now();
		final String newPatientUser = "User(patient-user-uuid)";
		final LocalDateTime newPatientDate = LocalDateTime.now();
		PatientModel dbModel = new PatientModel();
		PatientModel newModel = new PatientModel();
		newModel.setChangedByUuid(newUser);
		newModel.setDateChanged(newDate);
		newModel.setPatientChangedByUuid(newPatientUser);
		newModel.setPatientDateChanged(newPatientDate);
		
		service.mergeAuditProperties(dbModel, newModel);
		
		assertEquals(newUser, dbModel.getChangedByUuid());
		assertEquals(newDate, dbModel.getDateChanged());
		assertEquals(newPatientUser, dbModel.getPatientChangedByUuid());
		assertEquals(newPatientDate, dbModel.getPatientDateChanged());
	}
	
	@Test
	public void mergeAuditProperties_shouldSkipIfTheNewDateChangedIsBeforeThatFromTheDbForData() {
		final String dbUser = "db-User(user-uuid)";
		final LocalDateTime dbDate = of(2023, AUGUST, 23, 00, 00, 01);
		final String dbPatientUser = "User(patient-user-uuid)";
		final LocalDateTime dbPatientDate = of(2023, AUGUST, 24, 00, 00, 01);
		PatientModel dbModel = new PatientModel();
		dbModel.setChangedByUuid(dbUser);
		dbModel.setDateChanged(dbDate);
		dbModel.setPatientChangedByUuid(dbPatientUser);
		dbModel.setPatientDateChanged(dbPatientDate);
		PatientModel newModel = new PatientModel();
		newModel.setChangedByUuid("User(user-uuid)");
		newModel.setDateChanged(of(2023, AUGUST, 23, 00, 00, 00));
		newModel.setPatientChangedByUuid("User(user-uuid)");
		newModel.setPatientDateChanged(of(2023, AUGUST, 24, 00, 00, 00));
		
		service.mergeAuditProperties(dbModel, newModel);
		
		assertEquals(dbUser, dbModel.getChangedByUuid());
		assertEquals(dbDate, dbModel.getDateChanged());
		assertEquals(dbPatientUser, dbModel.getPatientChangedByUuid());
		assertEquals(dbPatientDate, dbModel.getPatientDateChanged());
	}
	
	@Test
	public void mergeAuditProperties_shouldNotReplaceChangedByWithEmptyDataForData() {
		final String dbUser = "db-User(patient-user-uuid)";
		final String dbPatientUser = "db-User(user-uuid)";
		PatientModel dbModel = new PatientModel();
		dbModel.setChangedByUuid(dbUser);
		dbModel.setPatientChangedByUuid(dbPatientUser);
		PatientModel newModel = new PatientModel();
		newModel.setChangedByUuid(" ");
		newModel.setPatientChangedByUuid(" ");
		
		service.mergeAuditProperties(dbModel, newModel);
		
		assertEquals(dbUser, dbModel.getChangedByUuid());
		assertEquals(dbPatientUser, dbModel.getPatientChangedByUuid());
	}
	
	@Test
	public void mergeAuditProperties_shouldReplaceDbFieldsWithTheNewStateMetadata() {
		final String newUser = "User(user-uuid)";
		final LocalDateTime newDate = LocalDateTime.now();
		ProviderModel dbModel = new ProviderModel();
		ProviderModel newModel = new ProviderModel();
		newModel.setChangedByUuid(newUser);
		newModel.setDateChanged(newDate);
		
		service.mergeAuditProperties(dbModel, newModel);
		
		assertEquals(newUser, dbModel.getChangedByUuid());
		assertEquals(newDate, dbModel.getDateChanged());
	}
	
	@Test
	public void mergeAuditProperties_shouldSkipIfTheNewDateChangedIsBeforeThatFromTheDbForMetadata() {
		final String dbUser = "db-User(user-uuid)";
		final LocalDateTime dbDate = of(2023, AUGUST, 23, 00, 00, 01);
		ProviderModel dbModel = new ProviderModel();
		dbModel.setChangedByUuid(dbUser);
		dbModel.setDateChanged(dbDate);
		ProviderModel newModel = new ProviderModel();
		newModel.setChangedByUuid("User(user-uuid)");
		newModel.setDateChanged(of(2023, AUGUST, 23, 00, 00, 00));
		
		service.mergeAuditProperties(dbModel, newModel);
		
		assertEquals(dbUser, dbModel.getChangedByUuid());
		assertEquals(dbDate, dbModel.getDateChanged());
	}
	
	@Test
	public void mergeAuditProperties_shouldNotReplaceChangedByWithEmptyDataForMetadata() {
		final String dbUser = "db-User(user-uuid)";
		ProviderModel dbModel = new ProviderModel();
		dbModel.setChangedByUuid(dbUser);
		ProviderModel newModel = new ProviderModel();
		newModel.setChangedByUuid(" ");
		
		service.mergeAuditProperties(dbModel, newModel);
		
		assertEquals(dbUser, dbModel.getChangedByUuid());
	}
	
	@Test
	public void resolve_shouldSyncTheMergedStateBasedOnTheSpecifiedPropertiesToSync() throws Exception {
		final String modelClassName = PersonModel.class.getName();
		final String uuid = "person-uuid";
		final String newGender = "F";
		final LocalDate newBirthDate = LocalDate.of(2023, AUGUST, 1);
		final String newVoidedBy = "User(void-user-uuid)";
		final LocalDateTime newDateVoided = of(2023, AUGUST, 23, 00, 00, 00);
		final String newVoidReason = "test";
		final String newChangedBy = "User(change-user-uuid)";
		final LocalDateTime newDateChanged = of(2023, AUGUST, 24, 00, 00, 00);
		PersonModel newModel = new PersonModel();
		newModel.setUuid(uuid);
		newModel.setGender(newGender);
		newModel.setBirthdate(newBirthDate);
		newModel.setVoided(true);
		newModel.setVoidedByUuid(newVoidedBy);
		newModel.setDateVoided(newDateVoided);
		newModel.setVoidReason(newVoidReason);
		newModel.setChangedByUuid(newChangedBy);
		newModel.setDateChanged(newDateChanged);
		SyncModel syncModel = SyncModel.builder().tableToSyncModelClass(PersonModel.class).model(newModel).build();
		PersonModel originalDbModel = new PersonModel();
		PersonModel dbModel = (PersonModel) BeanUtils.cloneBean(originalDbModel);
		when(mockServiceFacade.getModel(TableToSyncEnum.PERSON, uuid)).thenReturn(dbModel);
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setIdentifier(uuid);
		conflict.setModelClassName(modelClassName);
		conflict.setEntityPayload(JsonUtils.marshall(syncModel));
		ConflictResolution resolution = new ConflictResolution(conflict, MERGE);
		resolution.addPropertyToSync("gender");
		resolution.addPropertyToSync("birthdate");
		resolution.addPropertyToSync(FIELD_VOIDED);
		Holder<Exchange> exchangeHolder = new Holder<>();
		when(CamelUtils.send(eq(ReceiverConstants.URI_INBOUND_DB_SYNC), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			exchangeHolder.value = exchange;
			exchange.setProperty(ReceiverConstants.EX_PROP_MSG_PROCESSED, true);
			return exchange;
		});
		service = spy(service);
		doNothing().when(service).moveToSyncedQueue(conflict);
		when(SyncContext.getBean(ConflictService.class)).thenReturn(service);
		
		service.resolve(resolution);
		
		verify(mockReceiverService).updateHash(modelClassName, uuid);
		verify(service).moveToSyncedQueue(conflict);
		Exchange exchange = exchangeHolder.value;
		assertEquals(mockContext, exchange.getContext());
		assertEquals(modelClassName, exchange.getProperty(EX_PROP_MODEL_CLASS));
		assertEquals(uuid, exchange.getProperty(EX_PROP_ENTITY_ID));
		assertTrue(exchange.getProperty(EX_PROP_IS_CONFLICT, Boolean.class));
		SyncModel processedSyncModel = exchange.getIn().getBody(SyncModel.class);
		assertEquals(PersonModel.class, processedSyncModel.getTableToSyncModelClass());
		assertEquals(dbModel, processedSyncModel.getModel());
		assertEquals(newGender, dbModel.getGender());
		assertEquals(newBirthDate, dbModel.getBirthdate());
		assertTrue(dbModel.isVoided());
		assertEquals(newVoidedBy, dbModel.getVoidedByUuid());
		assertEquals(newDateVoided, dbModel.getDateVoided());
		assertEquals(newVoidReason, dbModel.getVoidReason());
		assertEquals(newChangedBy, dbModel.getChangedByUuid());
		assertEquals(newDateChanged, dbModel.getDateChanged());
		for (PropertyDescriptor d : PropertyUtils.getPropertyDescriptors(PersonModel.class)) {
			if (!resolution.getPropertiesToSync().contains(d.getName()) && !MERGE_EXCLUDE_FIELDS.contains(d.getName())) {
				String getter = d.getReadMethod().getName();
				assertEquals(invokeMethod(originalDbModel, getter), invokeMethod(dbModel, getter));
			}
		}
	}
	
	@Test
	public void resolve_shouldFailIfTheSyncOutcomeIsUnknownForAMergeResolution() throws Exception {
		final String modelClassName = PersonModel.class.getName();
		final String uuid = "person-uuid";
		PersonModel newModel = new PersonModel();
		newModel.setUuid(uuid);
		SyncModel syncModel = SyncModel.builder().tableToSyncModelClass(PersonModel.class).model(newModel).build();
		PersonModel originalDbModel = new PersonModel();
		PersonModel dbModel = (PersonModel) BeanUtils.cloneBean(originalDbModel);
		when(mockServiceFacade.getModel(TableToSyncEnum.PERSON, uuid)).thenReturn(dbModel);
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setIdentifier(uuid);
		conflict.setModelClassName(modelClassName);
		conflict.setEntityPayload(JsonUtils.marshall(syncModel));
		ConflictResolution resolution = new ConflictResolution(conflict, MERGE);
		resolution.addPropertyToSync("gender");
		
		Throwable thrown = Assert.assertThrows(EIPException.class, () -> service.resolve(resolution));
		assertEquals("Something went wrong while syncing item with uuid: " + conflict.getMessageUuid(), thrown.getMessage());
	}
	
}
