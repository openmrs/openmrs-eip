package org.openmrs.eip.app.management;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class UpdateColumnDatatypeChangeSetTest {
	
	@Test
	public void shouldReturnFalseForANonSubclassTable() throws Exception {
		Set<String> tables = new UpdateColumnDatatypeChangeSet().getHashTableNames();
		Assert.assertEquals(28, tables.size());
		for (String tableName : tables) {
			Assert.assertTrue(tableName.endsWith("_hash"));
		}
	}
}
