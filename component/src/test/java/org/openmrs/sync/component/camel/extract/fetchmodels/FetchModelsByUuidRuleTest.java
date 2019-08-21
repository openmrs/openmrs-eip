package org.openmrs.sync.component.camel.extract.fetchmodels;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class FetchModelsByUuidRuleTest {

    @Mock
    private EntityServiceFacade facade;

    private FetchModelsByUuidRule rule;

    private static final String UUID = "UUID";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        rule = new FetchModelsByUuidRule(facade);
    }

    @Test
    public void evaluate_should_return_true() {
        // Given
        ComponentParams params = ComponentParams.builder()
                .uuid(UUID)
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
                .id(1L)
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
                .uuid(UUID)
                .build();

        // When
        rule.getModels(TableToSyncEnum.PERSON, params);

        // Then
        verify(facade).getModel(TableToSyncEnum.PERSON, UUID);
    }

}
