package org.openmrs.eip;

import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

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
		assertEquals(3, tables.size());
		assertTrue(tables.contains(tableName));
		assertTrue(tables.contains("test_order"));
		assertTrue(tables.contains("drug_order"));
		
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
		assertEquals(3, tables.size());
		assertTrue(tables.contains("'" + tableName + "'"));
		assertTrue(tables.contains("'test_order'"));
		assertTrue(tables.contains("'drug_order'"));
		
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
	}
	
}
