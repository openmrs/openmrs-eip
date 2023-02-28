package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType.CACHE_EVICT;
import static org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType.SEARCH_INDEX_UPDATE;
import static org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType.SEND_RESPONSE;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionStatus;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.EncounterModel;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql", "classpath:mgt_receiver_synced_msg.sql",
        "classpath:mgt_receiver_post_sync_action.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverUtilsIntegrationTest extends BaseReceiverTest {
	
	@Autowired
	private SyncedMessageRepository syncedMsgRepo;
	
	@Autowired
	private PostSyncActionRepository syncActionRepo;
	
	private SyncedMessage createMessage(Class<? extends BaseModel> modelClass) {
		SyncedMessage syncedMsg = new SyncedMessage();
		syncedMsg.setMessageUuid("some-msg-uuid");
		syncedMsg.setIdentifier("some-uuid");
		syncedMsg.setModelClassName(modelClass.getName());
		syncedMsg.setSnapshot(false);
		syncedMsg.setOperation(SyncOperation.c);
		syncedMsg.setDateSentBySender(LocalDateTime.now());
		syncedMsg.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		syncedMsg.setEntityPayload("{}");
		syncedMsg.setItemized(false);
		syncedMsg.setDateCreated(new Date());
		syncedMsg = syncedMsgRepo.save(syncedMsg);
		assertTrue(syncedMsg.getActions().isEmpty());
		
		return syncedMsg;
	}
	
	private void checkPostSyncAction(PostSyncAction action, SyncedMessage syncedMsg, PostSyncActionType actionType,
	                                 Date dateItemized) {
		
		assertEquals(syncedMsg, action.getMessage());
		assertEquals(actionType, action.getActionType());
		assertEquals(PostSyncActionStatus.NEW, action.getStatus());
		assertTrue(action.getDateCreated().equals(dateItemized) || action.getDateCreated().after(dateItemized));
		assertNull(action.getDateProcessed());
		assertNull(action.getStatusMessage());
	}
	
	@Test
	public void generatePostSyncActions_shouldGenerateAllActionsForAPerson() {
		SyncedMessage syncedMsg = createMessage(PersonModel.class);
		Date dateItemized = new Date();
		
		ReceiverUtils.generatePostSyncActions(syncedMsg);
		
		Collection<PostSyncAction> actions = syncedMsg.getActions();
		assertTrue(syncedMsg.isItemized());
		assertEquals(3, actions.size());
		Iterator<PostSyncAction> iterator = actions.iterator();
		checkPostSyncAction(iterator.next(), syncedMsg, SEND_RESPONSE, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, CACHE_EVICT, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, SEARCH_INDEX_UPDATE, dateItemized);
	}
	
	@Test
	public void generatePostSyncActions_shouldGenerateAllActionsForAPatient() {
		SyncedMessage syncedMsg = createMessage(PatientModel.class);
		Date dateItemized = new Date();
		
		ReceiverUtils.generatePostSyncActions(syncedMsg);
		
		Collection<PostSyncAction> actions = syncedMsg.getActions();
		assertTrue(syncedMsg.isItemized());
		assertEquals(3, actions.size());
		Iterator<PostSyncAction> iterator = actions.iterator();
		checkPostSyncAction(iterator.next(), syncedMsg, SEND_RESPONSE, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, CACHE_EVICT, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, SEARCH_INDEX_UPDATE, dateItemized);
	}
	
	@Test
	public void generatePostSyncActions_shouldGenerateAllActionsForAPersonName() {
		SyncedMessage syncedMsg = createMessage(PersonNameModel.class);
		Date dateItemized = new Date();
		
		ReceiverUtils.generatePostSyncActions(syncedMsg);
		
		Collection<PostSyncAction> actions = syncedMsg.getActions();
		assertTrue(syncedMsg.isItemized());
		assertEquals(3, actions.size());
		Iterator<PostSyncAction> iterator = actions.iterator();
		checkPostSyncAction(iterator.next(), syncedMsg, SEND_RESPONSE, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, CACHE_EVICT, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, SEARCH_INDEX_UPDATE, dateItemized);
	}
	
	@Test
	public void generatePostSyncActions_shouldGenerateAllActionsForAPersonAttribute() {
		SyncedMessage syncedMsg = createMessage(PersonAttributeModel.class);
		Date dateItemized = new Date();
		
		ReceiverUtils.generatePostSyncActions(syncedMsg);
		
		Collection<PostSyncAction> actions = syncedMsg.getActions();
		assertTrue(syncedMsg.isItemized());
		assertEquals(3, actions.size());
		Iterator<PostSyncAction> iterator = actions.iterator();
		checkPostSyncAction(iterator.next(), syncedMsg, SEND_RESPONSE, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, CACHE_EVICT, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, SEARCH_INDEX_UPDATE, dateItemized);
	}
	
	@Test
	public void generatePostSyncActions_shouldGenerateResponseAndSearchIndexUpdateActionsForAPatientIdentifier() {
		SyncedMessage syncedMsg = createMessage(PatientIdentifierModel.class);
		Date dateItemized = new Date();
		
		ReceiverUtils.generatePostSyncActions(syncedMsg);
		
		Collection<PostSyncAction> actions = syncedMsg.getActions();
		assertTrue(syncedMsg.isItemized());
		assertEquals(2, actions.size());
		Iterator<PostSyncAction> iterator = actions.iterator();
		checkPostSyncAction(iterator.next(), syncedMsg, SEND_RESPONSE, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, SEARCH_INDEX_UPDATE, dateItemized);
	}
	
	@Test
	public void generatePostSyncActions_shouldGenerateResponseAndCacheEvictActionsForAPersonAddress() {
		SyncedMessage syncedMsg = createMessage(PersonAddressModel.class);
		Date dateItemized = new Date();
		ReceiverUtils.generatePostSyncActions(syncedMsg);
		
		Collection<PostSyncAction> actions = syncedMsg.getActions();
		assertTrue(syncedMsg.isItemized());
		assertEquals(2, actions.size());
		Iterator<PostSyncAction> iterator = actions.iterator();
		checkPostSyncAction(iterator.next(), syncedMsg, SEND_RESPONSE, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, CACHE_EVICT, dateItemized);
	}
	
	@Test
	public void generatePostSyncActions_shouldGenerateResponseAndCacheEvictActionsForAUser() {
		SyncedMessage syncedMsg = createMessage(UserModel.class);
		Date dateItemized = new Date();
		ReceiverUtils.generatePostSyncActions(syncedMsg);
		
		Collection<PostSyncAction> actions = syncedMsg.getActions();
		assertTrue(syncedMsg.isItemized());
		assertEquals(2, actions.size());
		Iterator<PostSyncAction> iterator = actions.iterator();
		checkPostSyncAction(iterator.next(), syncedMsg, SEND_RESPONSE, dateItemized);
		checkPostSyncAction(iterator.next(), syncedMsg, CACHE_EVICT, dateItemized);
	}
	
	@Test
	public void generatePostSyncActions_shouldGenerateOnlyResponseActionsForNonCachedAndIndexedType() {
		SyncedMessage syncedMsg = createMessage(EncounterModel.class);
		Date dateItemized = new Date();
		ReceiverUtils.generatePostSyncActions(syncedMsg);
		
		Collection<PostSyncAction> actions = syncedMsg.getActions();
		assertTrue(syncedMsg.isItemized());
		assertEquals(1, actions.size());
		Iterator<PostSyncAction> iterator = actions.iterator();
		checkPostSyncAction(iterator.next(), syncedMsg, SEND_RESPONSE, dateItemized);
	}
	
	@Test
	public void updatePostSyncActionStatuses_shouldUpdateTheStatusesOfTheActionsToCompleted() {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		Pageable page = PageRequest.of(0, 10);
		List<PostSyncAction> actions = syncActionRepo.getBatchOfPendingResponseActions(site, page);
		assertEquals(2, actions.size());
		assertEquals(PostSyncActionStatus.FAILURE, actions.get(1).getStatus());
		assertNotNull(actions.get(1).getStatusMessage());
		
		actions = ReceiverUtils.updatePostSyncActionStatuses(actions, true, null);
		
		assertTrue(syncActionRepo.getBatchOfPendingResponseActions(site, page).isEmpty());
		for (PostSyncAction a : actions) {
			assertTrue(a.isCompleted());
			assertNull(a.getStatusMessage());
		}
	}
	
	@Test
	public void updatePostSyncActionStatuses_shouldUpdateTheStatusesOfTheActionsToFailed() {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		Pageable page = PageRequest.of(0, 10);
		List<PostSyncAction> actions = syncActionRepo.getBatchOfPendingResponseActions(site, page);
		assertFalse(actions.isEmpty());
		int originalCount = actions.size();
		final String errorMsg = "test error";
		
		actions = ReceiverUtils.updatePostSyncActionStatuses(actions, false, errorMsg);
		
		assertEquals(originalCount, syncActionRepo.getBatchOfPendingResponseActions(site, page).size());
		for (PostSyncAction a : actions) {
			assertEquals(PostSyncActionStatus.FAILURE, a.getStatus());
			assertEquals(errorMsg, a.getStatusMessage());
		}
	}
	
	@Test
	public void updatePostSyncActionStatuses_shouldRetainTheStatusMessageForFailuresIfNoNewOneIsProvided() {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		Pageable page = PageRequest.of(0, 10);
		List<PostSyncAction> actions = syncActionRepo.getBatchOfPendingResponseActions(site, page);
		assertFalse(actions.isEmpty());
		int originalCount = actions.size();
		assertEquals(2, actions.size());
		assertEquals(PostSyncActionStatus.FAILURE, actions.get(1).getStatus());
		final String originalErrorMsg = actions.get(1).getStatusMessage();
		
		actions = ReceiverUtils.updatePostSyncActionStatuses(actions, false, null);
		
		assertEquals(originalCount, syncActionRepo.getBatchOfPendingResponseActions(site, page).size());
		assertEquals(PostSyncActionStatus.FAILURE, actions.get(0).getStatus());
		assertNull(actions.get(0).getStatusMessage());
		assertEquals(PostSyncActionStatus.FAILURE, actions.get(1).getStatus());
		assertEquals(originalErrorMsg, actions.get(1).getStatusMessage());
	}
	
}
