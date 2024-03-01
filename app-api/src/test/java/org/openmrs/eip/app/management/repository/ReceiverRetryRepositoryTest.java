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
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverRetryRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverRetryRepository repo;
	
	@Test
	public void countByIdentifierAndModelClassNameIn_shouldGetTheCountOfMatchingRetryItems() {
		assertEquals(3, repo.countByIdentifierAndModelClassNameIn("uuid-1",
		    asList(PersonModel.class.getName(), PatientModel.class.getName())));
	}
	
	@Test
	public void countByIdentifierAndModelClassNameIn_shouldReturnZeroIfTheIdentifierHasNoMatch() {
		assertEquals(0, repo.countByIdentifierAndModelClassNameIn("some-uuid",
		    asList(PersonModel.class.getName(), PatientModel.class.getName())));
	}
	
	@Test
	public void countByIdentifierAndModelClassNameIn_shouldReturnZeroIfTheClassNamesHaveNoMatch() {
		assertEquals(0, repo.countByIdentifierAndModelClassNameIn("uuid-1", asList(VisitModel.class.getName())));
	}
	
	@Test
	public void existsByIdentifierAndModelClassNameInAndOperationIn_shouldReturnTrueIfAMatchExists() {
		final String uuid = "uuid-1";
		List<SyncOperation> ops = of(SyncOperation.c);
		assertTrue(repo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, of(PersonModel.class.getName()), ops));
	}
	
	@Test
	public void existsByIdentifierAndModelClassNameInAndOperationIn_shouldReturnTrueIfNoMatchExists() {
		final String uuid = "uuid-1";
		List<SyncOperation> ops = of(SyncOperation.c);
		assertTrue(repo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, of(PersonModel.class.getName()), ops));
		assertFalse(repo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, of(VisitModel.class.getName()), ops));
		ops = of(SyncOperation.d);
		assertFalse(repo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, of(PersonModel.class.getName()), ops));
	}
	
}
