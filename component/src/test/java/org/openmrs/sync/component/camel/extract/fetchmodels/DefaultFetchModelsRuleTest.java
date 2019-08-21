package org.openmrs.sync.component.camel.extract.fetchmodels;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;

public class DefaultFetchModelsRuleTest {

    @Mock
    private EntityServiceFacade facade;

    private DefaultFetchModelsRule rule;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        rule = new DefaultFetchModelsRule(facade);
    }

    @Test
    public void evaluate_should_return_false() {
        // Given
        ComponentParams params = ComponentParams.builder().build();

        // When
        boolean result = rule.evaluate(params);

        // Then
        assertFalse(result);
    }

    @Test
    public void getModels_should_return_empty_list() {
        // Given
        ComponentParams params = ComponentParams.builder().build();

        // When
        rule.getModels(TableToSyncEnum.PERSON, params);

        // Then
        verify(facade).getAllModels(TableToSyncEnum.PERSON);
    }
}
