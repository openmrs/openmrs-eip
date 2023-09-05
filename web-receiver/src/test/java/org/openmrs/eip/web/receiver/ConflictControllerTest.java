package org.openmrs.eip.web.receiver;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.web.RestConstants.PATH_RECEIVER_CONFLICT_DIFF;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.impl.PatientService;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@Sql(scripts = {
        "classpath:mgt_site_info.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
public class ConflictControllerTest extends BaseReceiverWebTest {
	
	@Autowired
	private ConflictRepository conflictRepo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Autowired
	private PatientService patientService;
	
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
		result.andDo(MockMvcResultHandlers.print());
		result.andExpect(jsonPath("currentState", notNullValue()));
		result.andExpect(jsonPath("newState", notNullValue()));
		result.andExpect(jsonPath("additions", empty()));
		result.andExpect(jsonPath("modifications.length()", equalTo(1)));
		result.andExpect(jsonPath("modifications", Matchers.contains("gender")));
		result.andExpect(jsonPath("removals", empty()));
		
	}
	
}
