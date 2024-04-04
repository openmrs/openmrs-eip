package org.openmrs.eip.app.management.repository;

import static java.util.Arrays.asList;
import static java.util.List.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SyncMessageRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private SyncMessageRepository repo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Test
	public void countByIdentifierAndModelClassNameIn_shouldGetTheCountOfMatchingSyncItems() {
		assertEquals(2, repo.countByIdentifierAndModelClassNameIn("4bfd940e-32dc-491f-8038-a8f3afe3e36c",
		    asList(PersonModel.class.getName(), PatientModel.class.getName())));
	}
	
	@Test
	public void countByIdentifierAndModelClassNameIn_shouldReturnZeroIfTheIdentifierHasNoMatch() {
		assertEquals(0, repo.countByIdentifierAndModelClassNameIn("some-id", asList(PersonModel.class.getName())));
	}
	
	@Test
	public void countByIdentifierAndModelClassNameIn_shouldReturnZeroIfTheClassNamesHaveNoMatch() {
		assertEquals(0, repo.countByIdentifierAndModelClassNameIn("4bfd940e-32dc-491f-8038-a8f3afe3e36c",
		    asList(VisitModel.class.getName())));
	}
	
	@Test
	public void getSyncMessageBySiteOrderByDateReceived_shouldGetSiteMessagesOrderedByDateCreated() {
		SiteInfo site = siteRepo.getReferenceById(1L);
		
		List<SyncMessage> syncMessages = repo.getSyncMessageBySiteOrderByDateReceived(site, PageRequest.of(0, 1000));
		
		assertEquals(3, syncMessages.size());
		assertEquals(3l, syncMessages.get(0).getId().longValue());
		assertEquals(1l, syncMessages.get(1).getId().longValue());
		assertEquals(2l, syncMessages.get(2).getId().longValue());
	}
	
	@Test
	public void getSyncMessageBySiteOrderByDateReceived_shouldGetAPageOfSiteMessagesOrderedByDateCreated() {
		final int pageSize = 2;
		SiteInfo site = siteRepo.getReferenceById(1L);
		
		List<SyncMessage> syncMessages = repo.getSyncMessageBySiteOrderByDateReceived(site, PageRequest.of(0, pageSize));
		
		assertEquals(pageSize, syncMessages.size());
		assertEquals(3l, syncMessages.get(0).getId().longValue());
		assertEquals(1l, syncMessages.get(1).getId().longValue());
	}
	
	@Test
	public void existsByIdentifierAndModelClassNameInAndOperationIn_shouldReturnTrueIfAMatchExists() {
		final String uuid = "1bfd940e-32dc-491f-8038-a8f3afe3e36c";
		List<SyncOperation> ops = of(SyncOperation.c);
		assertTrue(repo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, of(PersonModel.class.getName()), ops));
	}
	
	@Test
	public void existsByIdentifierAndModelClassNameInAndOperationIn_shouldReturnTrueIfNoMatchExists() {
		final String uuid = "1bfd940e-32dc-491f-8038-a8f3afe3e36c";
		List<SyncOperation> ops = of(SyncOperation.c);
		assertTrue(repo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, of(PersonModel.class.getName()), ops));
		assertFalse(repo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, of(VisitModel.class.getName()), ops));
		ops = of(SyncOperation.d);
		assertFalse(repo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, of(PersonModel.class.getName()), ops));
	}
	
}
