package org.openmrs.sync.core.entity;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuditableEntityTest {

    private static final LocalDateTime DATE1 = LocalDateTime.of(2019, 5, 1, 0, 0);
    private static final LocalDateTime DATE2 = LocalDateTime.of(2019, 6, 1, 0, 0);

    @Test
    public void date_created_to_test_before_date_created_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateCreated(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateCreated(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_changed_to_test_before_date_created_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateCreated(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateChanged(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_voided_to_test_before_date_created_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateCreated(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateVoided(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_created_to_test_before_date_changed_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateChanged(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateCreated(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_changed_to_test_before_date_changed_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateChanged(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateChanged(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_voided_to_test_before_date_changed_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateVoided(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateChanged(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_created_to_test_before_date_voided_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateCreated(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateVoided(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_changed_to_test_before_date_voided_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateChanged(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateVoided(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_voided_to_test_before_date_voided_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateVoided(DATE1);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateVoided(DATE2);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertFalse(result);
    }

    @Test
    public void date_created_to_test_after_date_created_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateCreated(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateChanged(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }

    @Test
    public void date_changed_to_test_after_date_created_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateChanged(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateCreated(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }

    @Test
    public void date_voided_to_test_after_date_created_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateVoided(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateCreated(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }

    @Test
    public void date_created_to_test_after_date_changed_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateCreated(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateChanged(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }

    @Test
    public void date_changed_to_test_after_date_changed_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateChanged(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateChanged(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }

    @Test
    public void date_voided_to_test_after_date_changed_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateVoided(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateChanged(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }

    @Test
    public void date_created_to_test_after_date_voided_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateCreated(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateVoided(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }

    @Test
    public void date_changed_to_test_after_date_voided_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateChanged(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateVoided(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }

    @Test
    public void date_voided_to_test_after_date_voided_in_param() {
        // Given
        MockedEntity entityToTest = new MockedEntity(1L, "uuid");
        entityToTest.setDateVoided(DATE2);
        MockedEntity entityInParam = new MockedEntity(1L, "uuid");
        entityInParam.setDateVoided(DATE1);

        // When
        boolean result = entityToTest.wasModifiedAfter(entityInParam);

        // Then
        assertTrue(result);
    }
}
