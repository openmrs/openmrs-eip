package org.openmrs.sync.core.camel.extract.fetchmodels;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class FetchModelsByIdRuleTest {

    @Mock
    private EntityServiceFacade facade;

    private FetchModelsByIdRule rule;

    private static final Long ID = 1L;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        rule = new FetchModelsByIdRule(facade);
    }

    @Test
    public void evaluate_should_return_true() {
        // Given
        ComponentParams params = ComponentParams.builder()
                .id(ID)
                .build();

        // When
        boolean result = rule.evaluate(params);

        // Then
        assertTrue(result);
    }

    @Test
    public void evaluate_should_return_false() {
        // Given
        ComponentParams params = ComponentParams.builder()
                .uuid("UUID")
                .build();

        // When
        boolean result = rule.evaluate(params);

        // Then
        assertFalse(result);
    }

    @Test
    public void getModels_should_call_facade() {
        // Given
        ComponentParams params = ComponentParams.builder()
                .id(ID)
                .build();

        // When
        rule.getModels(TableToSyncEnum.PERSON, params);

        // Then
        verify(facade).getModel(TableToSyncEnum.PERSON, ID);
    }

}
