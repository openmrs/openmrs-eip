package org.openmrs.eip.web.receiver;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.IGNORE_NEW;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.MERGE;
import static org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision.SYNC_NEW;
import static org.openmrs.eip.web.RestConstants.PATH_RECEIVER_CONFLICT_DIFF;
import static org.openmrs.eip.web.RestConstants.PATH_RECEIVER_CONFLICT_RESOLVE;
import static org.openmrs.eip.web.RestConstants.RES_RECEIVER_CONFLICT;
import static org.openmrs.eip.web.RestConstants.RES_RECEIVER_CONFLICT_BY_ID;
import static org.openmrs.eip.web.receiver.ConflictController.FIELD_DECISION;
import static org.openmrs.eip.web.receiver.ConflictController.FIELD_PROPS_TO_SYNC;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Holder;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.receiver.ConflictResolution;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.impl.PatientService;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
public class ConflictControllerTest extends BaseReceiverWebTest {
	
	@Autowired
	private ConflictRepository conflictRepo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private ConflictController controller;
	
	private ConflictService mockService;
	
	@Before
	public void setup() {
		mockService = Mockito.mock(ConflictService.class);
		Whitebox.setInternalState(controller, ConflictService.class, mockService);
	}
	
	@Test
	public void getAll_shouldGetAllItemsInTheConflictQueue() throws Exception {
		MockHttpServletRequestBuilder builder = get(RES_RECEIVER_CONFLICT);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(5)));
		result.andExpect(jsonPath("items.length()", equalTo(5)));
	}
	
	@Test
	public void shouldGetTheConflictItemMatchingTheSpecifiedId() throws Exception {
		MockHttpServletRequestBuilder builder = get(RES_RECEIVER_CONFLICT_BY_ID, 2L);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("messageUuid", equalTo("2cfd940e-32dc-491f-8038-a8f3afe3e36d")));
	}
	
	@Test
	public void getDiff_shouldGenerateAndReturnTheDiff() throws Exception {
		final String msgUuid = "message-uuid";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String newGender = "F";
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setMessageUuid(msgUuid);
		conflict.setModelClassName(PatientModel.class.getName());
		conflict.setIdentifier(uuid);
		conflict.setOperation(SyncOperation.u);
		PatientModel newModel = patientService.getModel(uuid);
		newModel.setGender(newGender);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid(msgUuid);
		SyncModel syncModel = SyncModel.builder().tableToSyncModelClass(PatientModel.class).model(newModel)
		        .metadata(metadata).build();
		conflict.setEntityPayload(JsonUtils.marshall(syncModel));
		conflict.setDateReceived(new Date());
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSite(siteRepo.getOne(1L));
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSnapshot(false);
		conflict.setDateCreated(new Date());
		conflictRepo.save(conflict);
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_CONFLICT_DIFF, conflict.getId());
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("currentState", notNullValue()));
		result.andExpect(jsonPath("newState", notNullValue()));
		result.andExpect(jsonPath("additions", empty()));
		result.andExpect(jsonPath("modifications.length()", equalTo(1)));
		result.andExpect(jsonPath("modifications", Matchers.contains("gender")));
		result.andExpect(jsonPath("removals", empty()));
		
	}
	
	@Test
	public void resolve_shouldProcessAConflictResolutionRequestWithDecisionSetToIgnore() throws Exception {
		ConflictQueueItem conflict = conflictRepo.getOne(1L);
		Holder<ConflictResolution> holder = new Holder<>();
		doAnswer(invocation -> {
			holder.value = invocation.getArgument(0);
			return null;
		}).when(mockService).resolve(any(ConflictResolution.class));
		MockHttpServletRequestBuilder builder = post(PATH_RECEIVER_CONFLICT_RESOLVE, conflict.getId());
		builder.contentType(MediaType.APPLICATION_JSON);
		builder.content(JsonUtils.marshall(singletonMap(FIELD_DECISION, IGNORE_NEW)));
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		ConflictResolution resolution = holder.value;
		assertEquals(conflict, resolution.getConflict());
		assertEquals(IGNORE_NEW, resolution.getDecision());
		assertTrue(resolution.getPropertiesToSync().isEmpty());
	}
	
	@Test
	public void resolve_shouldProcessAConflictResolutionRequestWithDecisionSetToSyncNew() throws Exception {
		ConflictQueueItem conflict = conflictRepo.getOne(1L);
		Holder<ConflictResolution> holder = new Holder<>();
		doAnswer(invocation -> {
			holder.value = invocation.getArgument(0);
			return null;
		}).when(mockService).resolve(any(ConflictResolution.class));
		MockHttpServletRequestBuilder builder = post(PATH_RECEIVER_CONFLICT_RESOLVE, conflict.getId());
		builder.contentType(MediaType.APPLICATION_JSON);
		builder.content(JsonUtils.marshall(singletonMap(FIELD_DECISION, SYNC_NEW)));
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		ConflictResolution resolution = holder.value;
		assertEquals(conflict, resolution.getConflict());
		assertEquals(SYNC_NEW, resolution.getDecision());
		assertTrue(resolution.getPropertiesToSync().isEmpty());
	}
	
	@Test
	public void resolve_shouldProcessAConflictResolutionRequestWithDecisionSetToMerge() throws Exception {
		ConflictQueueItem conflict = conflictRepo.getOne(1L);
		Holder<ConflictResolution> holder = new Holder<>();
		doAnswer(invocation -> {
			holder.value = invocation.getArgument(0);
			return null;
		}).when(mockService).resolve(any(ConflictResolution.class));
		MockHttpServletRequestBuilder builder = post(PATH_RECEIVER_CONFLICT_RESOLVE, conflict.getId());
		builder.contentType(MediaType.APPLICATION_JSON);
		final String prop1 = "propertyName1";
		final String prop2 = "propertyName2";
		Map<String, Object> data = new HashMap<>();
		data.put(FIELD_DECISION, MERGE);
		data.put(FIELD_PROPS_TO_SYNC, Arrays.asList(prop1, prop2));
		builder.content(JsonUtils.marshall(data));
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		ConflictResolution resolution = holder.value;
		assertEquals(conflict, resolution.getConflict());
		assertEquals(MERGE, resolution.getDecision());
		assertEquals(2, resolution.getPropertiesToSync().size());
		assertTrue(resolution.getPropertiesToSync().contains(prop1));
		assertTrue(resolution.getPropertiesToSync().contains(prop2));
	}
	
}
