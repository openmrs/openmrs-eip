package org.openmrs.eip;

import static java.util.Arrays.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mockStatic;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
@PrepareForTest(AppContext.class)
public class UtilsTest {
	
	Environment mockEnv = Mockito.mock(Environment.class);
	
	@BeforeAll
	public static void setup() {
		mockStatic(AppContext.class);
	}
	
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
		Mockito.when(AppContext.getBean(Environment.class)).thenReturn(mockEnv);
		Mockito.when(mockEnv.getProperty(Constants.PROP_WATCHED_TABLES)).thenReturn("person,patient,visit");
		
		List<String> watchedTables = Utils.getWatchedTables();
		
		assertEquals(3, watchedTables.size());
		assertTrue(watchedTables.contains("person"));
		assertTrue(watchedTables.contains("patient"));
		assertTrue(watchedTables.contains("visit"));
	}
	
	@Test
	public void getWatchedTables_shouldThrowEIPExceptionWhenWatchedTablesIsEmpty() {
		Mockito.when(AppContext.getBean(Environment.class)).thenReturn(mockEnv);
		Mockito.when(mockEnv.getProperty(Constants.PROP_WATCHED_TABLES)).thenReturn("");
		
		try {
			Utils.getWatchedTables();
		}
		catch (EIPException e) {
			assertEquals("The property " + Constants.PROP_WATCHED_TABLES
			        + " must be set to a comma-separated list of table names to watch",
			    e.getMessage());
		}
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
