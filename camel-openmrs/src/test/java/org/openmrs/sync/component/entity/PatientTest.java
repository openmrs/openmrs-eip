package org.openmrs.sync.component.entity;

import org.junit.Test;
import org.openmrs.sync.component.entity.light.UserLight;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatientTest {

    @Test
    public void creator() {
        // Given
        Patient patient = new Patient();
        UserLight creator = new UserLight();

        // When
        patient.setCreator(creator);

        // Then
        assertEquals(creator, patient.getPatientCreator());
        assertEquals(creator, patient.getCreator());
    }

    @Test
    public void dateCreated() {
        // Given
        Patient patient = new Patient();
        LocalDateTime dateCreated = LocalDateTime.now();

        // When
        patient.setDateCreated(dateCreated);

        // Then
        assertEquals(dateCreated, patient.getPatientDateCreated());
        assertEquals(dateCreated, patient.getDateCreated());
    }

    @Test
    public void changedBy() {
        // Given
        Patient patient = new Patient();
        UserLight changedBy = new UserLight();

        // When
        patient.setChangedBy(changedBy);

        // Then
        assertEquals(changedBy, patient.getPatientChangedBy());
        assertEquals(changedBy, patient.getChangedBy());
    }

    @Test
    public void dateChanged() {
        // Given
        Patient patient = new Patient();
        LocalDateTime dateChanged = LocalDateTime.now();

        // When
        patient.setDateChanged(dateChanged);

        // Then
        assertEquals(dateChanged, patient.getPatientDateChanged());
        assertEquals(dateChanged, patient.getDateChanged());
    }

    @Test
    public void voided() {
        // Given
        Patient patient = new Patient();
        boolean voided = true;

        // When
        patient.setVoided(voided);

        // Then
        assertTrue(patient.isPatientVoided());
        assertTrue(patient.isVoided());
    }

    @Test
    public void voidedBy() {
        // Given
        Patient patient = new Patient();
        UserLight voidedBy = new UserLight();

        // When
        patient.setVoidedBy(voidedBy);

        // Then
        assertEquals(voidedBy, patient.getPatientVoidedBy());
        assertEquals(voidedBy, patient.getVoidedBy());
    }

    @Test
    public void dateVoided() {
        // Given
        Patient patient = new Patient();
        LocalDateTime dateVoided = LocalDateTime.now();

        // When
        patient.setDateVoided(dateVoided);

        // Then
        assertEquals(dateVoided, patient.getPatientDateVoided());
        assertEquals(dateVoided, patient.getDateVoided());
    }

    @Test
    public void voidReason() {
        // Given
        Patient patient = new Patient();
        String voidReason = "reason";

        // When
        patient.setVoidReason(voidReason);

        // Then
        assertEquals(voidReason, patient.getPatientVoidReason());
        assertEquals(voidReason, patient.getVoidReason());
    }
}
