package org.openmrs.eip;

import static java.util.Arrays.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppContext.class)
public class UtilsTest {
	
	@Test
	public void getListOfTablesInHierarchy_shouldReturnSubclassAndSuperClassTables() {
		String tableName = "visit";
		List<String> tables = Utils.getListOfTablesInHierarchy(tableName);
		assertEquals(1, tables.size());
		assertTrue(tables.contains(tableName));
		
		tableName = "patient";
		tables = Utils.getListOfTablesInHierarchy(tableName);
		assertEquals(2, tables.size());
		assertTrue(tables.contains(tableName));
		assertTrue(tables.contains("person"));
		
		tableName = "person";
		tables = Utils.getListOfTablesInHierarchy(tableName);
		assertEquals(2, tables.size());
		assertTrue(tables.contains(tableName));
		assertTrue(tables.contains("patient"));
		
		tableName = "orders";
		tables = Utils.getListOfTablesInHierarchy(tableName);
		assertEquals(4, tables.size());
		assertTrue(tables.contains(tableName));
		assertTrue(tables.contains("test_order"));
		assertTrue(tables.contains("drug_order"));
		assertTrue(tables.contains("referral_order"));
		
		tableName = "test_order";
		tables = Utils.getListOfTablesInHierarchy(tableName);
		assertEquals(2, tables.size());
		assertTrue(tables.contains(tableName));
		assertTrue(tables.contains("orders"));
		
		tableName = "drug_order";
		tables = Utils.getListOfTablesInHierarchy(tableName);
		assertEquals(2, tables.size());
		assertTrue(tables.contains(tableName));
		assertTrue(tables.contains("orders"));
		
		tableName = "REFERRAL_ORDER";
		tables = Utils.getListOfTablesInHierarchy(tableName);
		assertEquals(2, tables.size());
		assertTrue(tables.contains(tableName));
		assertTrue(tables.contains("orders"));
	}
	
	@Test
	public void getTablesInHierarchy_shouldReturnCommaSeparatedListOfSubclassAndSuperClassTables() {
		String tableName = "visit";
		List<String> tables = stream(Utils.getTablesInHierarchy(tableName).split(",")).collect(Collectors.toList());
		assertEquals(1, tables.size());
		assertTrue(tables.contains("'" + tableName + "'"));
		
		tableName = "patient";
		tables = stream(Utils.getTablesInHierarchy(tableName).split(",")).collect(Collectors.toList());
		assertEquals(2, tables.size());
		assertTrue(tables.contains("'" + tableName + "'"));
		assertTrue(tables.contains("'person'"));
		
		tableName = "person";
		tables = stream(Utils.getTablesInHierarchy(tableName).split(",")).collect(Collectors.toList());
		assertEquals(2, tables.size());
		assertTrue(tables.contains("'" + tableName + "'"));
		assertTrue(tables.contains("'patient'"));
		
		tableName = "orders";
		tables = stream(Utils.getTablesInHierarchy(tableName).split(",")).collect(Collectors.toList());
		assertEquals(4, tables.size());
		assertTrue(tables.contains("'" + tableName + "'"));
		assertTrue(tables.contains("'test_order'"));
		assertTrue(tables.contains("'drug_order'"));
		assertTrue(tables.contains("'referral_order'"));
		
		tableName = "test_order";
		tables = stream(Utils.getTablesInHierarchy(tableName).split(",")).collect(Collectors.toList());
		assertEquals(2, tables.size());
		assertTrue(tables.contains("'" + tableName + "'"));
		assertTrue(tables.contains("'orders'"));
		
		tableName = "drug_order";
		tables = stream(Utils.getTablesInHierarchy(tableName).split(",")).collect(Collectors.toList());
		assertEquals(2, tables.size());
		assertTrue(tables.contains("'" + tableName + "'"));
		assertTrue(tables.contains("'orders'"));
		
		tableName = "REFERRAL_ORDER";
		tables = stream(Utils.getTablesInHierarchy(tableName).split(",")).collect(Collectors.toList());
		assertEquals(2, tables.size());
		assertTrue(tables.contains("'" + tableName + "'"));
		assertTrue(tables.contains("'orders'"));
	}
	
	@Test
	public void getWatchedTables_shouldReturnTheWatchedTableNames() {
		PowerMockito.mockStatic(AppContext.class);
		Environment mockEnv = Mockito.mock(Environment.class);
		Mockito.when(AppContext.getBean(Environment.class)).thenReturn(mockEnv);
		Mockito.when(mockEnv.getProperty(Constants.PROP_WATCHED_TABLES)).thenReturn("person,patient,visit");
		List<String> watchedTables = Utils.getWatchedTables();
		assertEquals(3, watchedTables.size());
		assertTrue(watchedTables.contains("person"));
		assertTrue(watchedTables.contains("patient"));
		assertTrue(watchedTables.contains("visit"));
	}
	
	@Test
	public void isOrderTable_shouldReturnTrueForAnOrderSubclass() {
		assertTrue(Utils.isOrderTable("orders"));
		assertTrue(Utils.isOrderTable("drug_order"));
		assertTrue(Utils.isOrderTable("test_order"));
		assertTrue(Utils.isOrderTable("REFERRAL_ORDER"));
	}
	
	@Test
	public void isOrderTable_shouldReturnFalseANonOrderSubclass() {
		assertFalse(Utils.isOrderTable("patient"));
	}
	
}
