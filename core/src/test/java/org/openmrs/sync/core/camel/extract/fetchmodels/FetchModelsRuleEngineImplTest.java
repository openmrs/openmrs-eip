package org.openmrs.sync.core.camel.extract.fetchmodels;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class FetchModelsRuleEngineImplTest {

    @Mock
    private FetchModelsRule rule1;

    @Mock
    private FetchModelsRule rule2;

    @Mock
    private DefaultFetchModelsRule defaultRule;

    private FetchModelsRuleEngineImpl ruleEngine;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        ruleEngine = new FetchModelsRuleEngineImpl(Arrays.asList(rule1, rule2), defaultRule);
    }

    @Test
    public void process_should_call_rule_1() {
        // Given
        ComponentParams params = ComponentParams.builder().build();
        when(rule1.evaluate(params)).thenReturn(true);
        when(rule2.evaluate(params)).thenReturn(false);

        // When
        ruleEngine.process(TableToSyncEnum.PERSON, params);

        // Then
        verify(rule1).getModels(TableToSyncEnum.PERSON, params);
        verify(rule2, never()).getModels(any(), any());
        verify(defaultRule, never()).getModels(any(), any());

    }

    @Test
    public void process_should_call_rule_2() {
        // Given
        ComponentParams params = ComponentParams.builder().build();
        when(rule1.evaluate(params)).thenReturn(false);
        when(rule2.evaluate(params)).thenReturn(true);

        // When
        ruleEngine.process(TableToSyncEnum.PERSON, params);

        // Then
        verify(rule1, never()).getModels(any(), any());
        verify(rule2).getModels(TableToSyncEnum.PERSON, params);
        verify(defaultRule, never()).getModels(any(), any());
    }

    @Test
    public void process_should_call_default_rule() {
        // Given
        ComponentParams params = ComponentParams.builder().build();
        when(rule1.evaluate(params)).thenReturn(false);
        when(rule2.evaluate(params)).thenReturn(false);

        // When
        List<BaseModel> result = ruleEngine.process(TableToSyncEnum.PERSON, params);

        // Then
        verify(rule1, never()).getModels(any(), any());
        verify(rule2, never()).getModels(any(), any());
        verify(defaultRule).getModels(TableToSyncEnum.PERSON, params);
    }
}
