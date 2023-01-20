package org.openmrs.eip.component.utils;

import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.component.model.DrugOrderModel;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.TestOrderModel;
import org.openmrs.eip.component.model.VisitModel;
import org.powermock.reflect.Whitebox;

public class UtilsTest {
	
	@Before
	public void setup() {
		Whitebox.setInternalState(Utils.class, "TABLE_HIERARCHY_MAP", new ConcurrentHashMap());
		Whitebox.setInternalState(Utils.class, "CLASS_HIERARCHY_MAP", new ConcurrentHashMap());
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
	
	@Test
	public void getListOfModelClassHierarchy_shouldReturnSubclassAndSuperClassNames() {
		String className = VisitModel.class.getName();
		List<String> classes = Utils.getListOfModelClassHierarchy(className);
		assertEquals(1, classes.size());
		assertTrue(classes.contains(className));
		
		className = PatientModel.class.getName();
		classes = Utils.getListOfModelClassHierarchy(className);
		assertEquals(2, classes.size());
		assertTrue(classes.contains(className));
		assertTrue(classes.contains(PersonModel.class.getName()));
		
		className = PersonModel.class.getName();
		classes = Utils.getListOfModelClassHierarchy(className);
		assertEquals(2, classes.size());
		assertTrue(classes.contains(className));
		assertTrue(classes.contains(PatientModel.class.getName()));
		
		className = OrderModel.class.getName();
		classes = Utils.getListOfModelClassHierarchy(className);
		assertEquals(3, classes.size());
		assertTrue(classes.contains(className));
		assertTrue(classes.contains(TestOrderModel.class.getName()));
		assertTrue(classes.contains(DrugOrderModel.class.getName()));
		
		className = TestOrderModel.class.getName();
		classes = Utils.getListOfModelClassHierarchy(className);
		assertEquals(2, classes.size());
		assertTrue(classes.contains(className));
		assertTrue(classes.contains(OrderModel.class.getName()));
		
		className = DrugOrderModel.class.getName();
		classes = Utils.getListOfModelClassHierarchy(className);
		assertEquals(2, classes.size());
		assertTrue(classes.contains(className));
		assertTrue(classes.contains(OrderModel.class.getName()));
	}
	
	@Test
	public void getModelClassesInHierarchy_shouldReturnCommaSeparatedListOfSubclassAndSuperClassNames() {
		String className = VisitModel.class.getName();
		List<String> classes = stream(Utils.getModelClassesInHierarchy(className).split(",")).collect(Collectors.toList());
		assertEquals(1, classes.size());
		assertTrue(classes.contains("'" + className + "'"));
		
		className = PatientModel.class.getName();
		classes = stream(Utils.getModelClassesInHierarchy(className).split(",")).collect(Collectors.toList());
		assertEquals(2, classes.size());
		assertTrue(classes.contains("'" + className + "'"));
		assertTrue(classes.contains("'" + PersonModel.class.getName() + "'"));
		
		className = PersonModel.class.getName();
		classes = stream(Utils.getModelClassesInHierarchy(className).split(",")).collect(Collectors.toList());
		assertEquals(2, classes.size());
		assertTrue(classes.contains("'" + className + "'"));
		assertTrue(classes.contains("'" + PatientModel.class.getName() + "'"));
		
		className = OrderModel.class.getName();
		classes = stream(Utils.getModelClassesInHierarchy(className).split(",")).collect(Collectors.toList());
		assertEquals(3, classes.size());
		assertTrue(classes.contains("'" + className + "'"));
		assertTrue(classes.contains("'" + TestOrderModel.class.getName() + "'"));
		assertTrue(classes.contains("'" + DrugOrderModel.class.getName() + "'"));
		
		className = TestOrderModel.class.getName();
		classes = stream(Utils.getModelClassesInHierarchy(className).split(",")).collect(Collectors.toList());
		assertEquals(2, classes.size());
		assertTrue(classes.contains("'" + className + "'"));
		assertTrue(classes.contains("'" + OrderModel.class.getName() + "'"));
		
		className = DrugOrderModel.class.getName();
		classes = stream(Utils.getModelClassesInHierarchy(className).split(",")).collect(Collectors.toList());
		assertEquals(2, classes.size());
		assertTrue(classes.contains("'" + className + "'"));
		assertTrue(classes.contains("'" + OrderModel.class.getName() + "'"));
	}
	
	@Test
	public void isSubclassTable_shouldReturnTrueForASubclassTable() {
		assertTrue(Utils.isSubclassTable("patient"));
		assertTrue(Utils.isSubclassTable("drug_order"));
		assertTrue(Utils.isSubclassTable("test_order"));
	}
	
	@Test
	public void isSubclassTable_shouldReturnFalseForANonSubclassTable() {
		assertFalse(Utils.isSubclassTable("person"));
		assertFalse(Utils.isSubclassTable("orders"));
	}
	
}
