package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.eip.app.route.TestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Ignore
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql({ "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverUtilsIntegrationTest extends BaseReceiverTest {
	
	private static final String PERSON_UUID = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
	
	@Test
	public void getPersonNameUuids_shouldReturnTheUuidsOfTheNamesOfThePersonWithTheSpecifiedUuid() {
		List<String> nameUuids = ReceiverUtils.getPersonNameUuids(PERSON_UUID);
		
		assertEquals(2, nameUuids.size());
		assertTrue(nameUuids.contains("1bfd940e-32dc-491f-8038-a8f3afe3e35a"));
		assertTrue(nameUuids.contains("2bfd940e-32dc-491f-8038-a8f3afe3e35a"));
	}
	
	@Test
	public void getPatientIdentifierUuids_shouldReturnTheUuidsOfTheIdentifiersOfThePatientWithTheSpecifiedUuid() {
		List<String> nameUuids = ReceiverUtils.getPatientIdentifierUuids(PERSON_UUID);
		
		assertEquals(2, nameUuids.size());
		assertTrue(nameUuids.contains("1cfd940e-32dc-491f-8038-a8f3afe3e35c"));
		assertTrue(nameUuids.contains("2cfd940e-32dc-491f-8038-a8f3afe3e35c"));
	}
	
	@Test
	public void getPersonAttributeUuids_shouldReturnTheUuidsOfTheSearchableAttributesOfThePersonWithTheSpecifiedUuid() {
		List<String> attributeUuids = ReceiverUtils.getPersonAttributeUuids(PERSON_UUID);
		
		assertEquals(2, attributeUuids.size());
		assertTrue(attributeUuids.contains("2efd940e-32dc-491f-8038-a8f3afe3e35f"));
		assertTrue(attributeUuids.contains("4efd940e-32dc-491f-8038-a8f3afe3e35f"));
	}
	
	@Test
	public void updateColumn_shouldUpdateTheColumnValueInTheDatabase() {
		final Long id = 1L;
		final String tableName = "site_info";
		final String columnName = "sync_disabled";
		Assert.assertEquals(false, TestUtils.getRowById(tableName, id).get(columnName));
		
		ReceiverUtils.updateColumn(tableName, columnName, id, true);
		
		Assert.assertEquals(true, TestUtils.getRowById(tableName, id).get(columnName));
	}
	
}
