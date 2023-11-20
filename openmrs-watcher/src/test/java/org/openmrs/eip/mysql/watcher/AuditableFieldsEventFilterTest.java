package org.openmrs.eip.mysql.watcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.COLUMN_CHANGED_BY;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.COLUMN_DATE_CHANGED;
import static org.powermock.reflect.Whitebox.getInternalState;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.openmrs.eip.DatabaseOperation;

public class AuditableFieldsEventFilterTest {
	
	public static final String COLUMN_FIRST_NAME = "first_name";
	
	public static final String COLUMN_CREATOR = "creator";
	
	public static final String COLUMN_DATE_CREATED = "date_created";
	
	public static final String TABLE_PERSON = "person";
	
	private AuditableEventFilter filter = new AuditableEventFilter(Collections.singletonList(TABLE_PERSON));
	
	private Event createEvent(Map<String, Object> prevState, Map<String, Object> newState) {
		Event e = WatcherTestUtils.createEvent(TABLE_PERSON, null, null, DatabaseOperation.u.name());
		e.setPreviousState(prevState);
		e.setCurrentState(newState);
		return e;
	}
	
	@Test
	public void shouldSetupTheCollectionOfFilteredTables() {
		final String visit = "visit";
		final String encounter = "ENCOUNTER";
		List<String> tables = Arrays.asList(visit, "", encounter, null, " ");
		AuditableEventFilter filter = new AuditableEventFilter(tables);
		Set<String> filteredTables = getInternalState(filter, Set.class);
		assertEquals(2, filteredTables.size());
		assertTrue(filteredTables.contains(visit));
		assertTrue(filteredTables.contains(encounter.toLowerCase()));
	}
	
	@Test
	public void accept_shouldReturnTrueIfThereAreNoFilteredTables() {
		filter = new AuditableEventFilter(Collections.emptyList());
		Event e = new Event();
		e.setOperation(DatabaseOperation.u.name());
		assertTrue(filter.accept(e, null));
	}
	
	@Test
	public void accept_shouldReturnTrueForANonFilteredTable() {
		Event e = new Event();
		e.setTableName("visit");
		e.setOperation(DatabaseOperation.u.name());
		assertTrue(filter.accept(e, null));
	}
	
	@Test
	public void accept_shouldReturnTrueIfTheRowHasModifiedColumnsThatAreStrings() {
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, "John");
		prevState.put(COLUMN_CHANGED_BY, null);
		prevState.put(COLUMN_DATE_CHANGED, null);
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, "Jahn");
		newState.put(COLUMN_CHANGED_BY, null);
		newState.put(COLUMN_DATE_CHANGED, null);
		assertTrue(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnTrueIfTheRowHasModifiedColumnsOfAPrimitiveWrapperType() {
		final Integer oldValue = 2;
		final Integer newValue = 3;
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, oldValue);
		prevState.put(COLUMN_CHANGED_BY, null);
		prevState.put(COLUMN_DATE_CHANGED, null);
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, newValue);
		newState.put(COLUMN_CHANGED_BY, null);
		newState.put(COLUMN_DATE_CHANGED, null);
		assertTrue(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnTrueForACreateEvent() {
		final String firstName = "John";
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, firstName);
		newState.put(COLUMN_CHANGED_BY, null);
		newState.put(COLUMN_DATE_CHANGED, null);
		Event e = createEvent(null, newState);
		e.setOperation(DatabaseOperation.c.name());
		assertTrue(filter.accept(e, null));
	}
	
	@Test
	public void accept_shouldReturnTrueForADeleteEvent() {
		final String firstName = "John";
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, firstName);
		prevState.put(COLUMN_CHANGED_BY, null);
		prevState.put(COLUMN_DATE_CHANGED, null);
		Event e = createEvent(prevState, null);
		e.setOperation(DatabaseOperation.d.name());
		assertTrue(filter.accept(e, null));
	}
	
	@Test
	public void accept_shouldReturnTrueForAReadEvent() {
		final String firstName = "John";
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, firstName);
		newState.put(COLUMN_CHANGED_BY, null);
		newState.put(COLUMN_DATE_CHANGED, null);
		Event e = createEvent(null, newState);
		e.setOperation(DatabaseOperation.r.name());
		assertTrue(filter.accept(e, null));
	}
	
	@Test
	public void accept_shouldReturnTrueForATableWitNoNonPrimaryKeyColumns() {
		assertTrue(filter.accept(createEvent(new HashMap(), new HashMap()), null));
	}
	
	@Test
	public void accept_shouldReturnTrueForATableWithNoChangedByColumn() {
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, null);
		prevState.put(COLUMN_DATE_CHANGED, null);
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, null);
		newState.put(COLUMN_DATE_CHANGED, null);
		assertTrue(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnTrueForATableWithNoDateChangedColumn() {
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, null);
		prevState.put(COLUMN_CHANGED_BY, null);
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, null);
		newState.put(COLUMN_CHANGED_BY, null);
		assertTrue(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnTrueIfANonAuditColumnIsChangedToNull() {
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, "John");
		prevState.put(COLUMN_CHANGED_BY, 1);
		prevState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:00");
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, null);
		newState.put(COLUMN_CHANGED_BY, 2);
		newState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:01");
		assertTrue(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnTrueIfANonAuditColumnIsChangedFromNull() {
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, null);
		prevState.put(COLUMN_CHANGED_BY, 1);
		prevState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:00");
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, "John");
		newState.put(COLUMN_CHANGED_BY, 2);
		newState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:01");
		assertTrue(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnFalseIfTheRowHasNoModifiedColumns() {
		final String firstName = "John";
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, firstName);
		prevState.put(COLUMN_CHANGED_BY, null);
		prevState.put(COLUMN_DATE_CHANGED, null);
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, firstName);
		newState.put(COLUMN_CHANGED_BY, null);
		newState.put(COLUMN_DATE_CHANGED, null);
		Event e = createEvent(prevState, newState);
		e.setTableName(TABLE_PERSON.toUpperCase());//Should be case insensitive
		assertFalse(filter.accept(e, null));
	}
	
	@Test
	public void accept_shouldReturnFalseIfOnlyChangeByColumnWasModified() {
		final String firstName = "John";
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, firstName);
		prevState.put(COLUMN_CHANGED_BY, 1);
		prevState.put(COLUMN_DATE_CHANGED, null);
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, firstName);
		newState.put(COLUMN_CHANGED_BY, 2);
		newState.put(COLUMN_DATE_CHANGED, null);
		assertFalse(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnFalseIfOnlyDateChangedColumnWasModified() {
		final String firstName = "John";
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, firstName);
		prevState.put(COLUMN_CHANGED_BY, null);
		prevState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:00");
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, firstName);
		newState.put(COLUMN_CHANGED_BY, null);
		newState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:01");
		assertFalse(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnFalseIfOnlyTheAuditColumnsWereModified() {
		final String firstName = "John";
		final Integer creator = 1;
		final Long dateCreated = System.currentTimeMillis();
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, firstName);
		prevState.put(COLUMN_CREATOR, creator);
		prevState.put(COLUMN_DATE_CREATED, dateCreated);
		prevState.put(COLUMN_CHANGED_BY, 1);
		prevState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:00");
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, firstName);
		newState.put(COLUMN_CREATOR, creator);
		newState.put(COLUMN_DATE_CREATED, dateCreated);
		newState.put(COLUMN_CHANGED_BY, 2);
		newState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:01");
		assertFalse(filter.accept(createEvent(prevState, newState), null));
	}
	
	@Test
	public void accept_shouldReturnFalseIfANonAuditColumnIsNullBeforeAndAfter() {
		Map prevState = new HashMap();
		prevState.put(COLUMN_FIRST_NAME, null);
		prevState.put(COLUMN_CHANGED_BY, 1);
		prevState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:00");
		Map newState = new HashMap();
		newState.put(COLUMN_FIRST_NAME, null);
		newState.put(COLUMN_CHANGED_BY, 2);
		newState.put(COLUMN_DATE_CHANGED, "2022-01-01 00:00:01");
		assertFalse(filter.accept(createEvent(prevState, newState), null));
	}
	
}
