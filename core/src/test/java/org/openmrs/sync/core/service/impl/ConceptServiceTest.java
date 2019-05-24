package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.ConceptEty;
import org.openmrs.sync.core.repository.ConceptRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class ConceptServiceTest {

    @Mock
    private ConceptRepository repository;

    private ConceptService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConceptService(repository);
    }

    @Test
    public void getFakeEntity() {
        assertEquals(getExpectedConcept(), service.getFakeEntity("UUID"));
    }

    private ConceptEty getExpectedConcept() {
        ConceptEty expected = new ConceptEty();
        expected.setUuid("UUID");
        expected.setClassId(1);
        expected.setDatatypeId(1);
        expected.setCreator(1);
        expected.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        return expected;
    }
}
