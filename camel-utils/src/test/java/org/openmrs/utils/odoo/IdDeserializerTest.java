package org.openmrs.utils.odoo;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class IdDeserializerTest {

    @Mock
    private JsonParser parser;

    private IdDeserializer idDeserializer = new IdDeserializer();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deserialize_should_return_first_value() throws IOException {
        // Given
        when(parser.readValueAs(Object.class)).thenReturn(new ArrayList<>(Arrays.asList(1, 2)));

        // When
        Integer result = idDeserializer.deserialize(parser, null);

        // Then
        assertEquals(1, result.intValue());
    }

    @Test
    public void deserialize_should_return_null() throws IOException {
        // Given
        when(parser.readValueAs(Object.class)).thenReturn(false);

        // When
        Object result = idDeserializer.deserialize(parser, null);

        // Then
        assertNull(result);
    }
}
