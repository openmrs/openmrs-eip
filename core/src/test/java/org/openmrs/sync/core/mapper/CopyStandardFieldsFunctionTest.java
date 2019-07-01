package org.openmrs.sync.core.mapper;

import lombok.EqualsAndHashCode;
import org.junit.Test;
import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.MockedModel;

import static org.junit.Assert.assertEquals;

public class CopyStandardFieldsFunctionTest {

    private CopyStandardFieldsFunction function = new CopyStandardFieldsFunction();

    @Test
    public void apply_should_populate_model() {
        // Given
        MockedModel model = new MockedModel("uuid");
        Context context = getContext(model);

        // When
        function.apply(context);

        // Then
        assertEquals(expectedModel(), model);
    }

    @Test(expected = OpenMrsSyncException.class)
    public void apply_should_throw_exception() {
        // Given
        WrongMockedModel model = new WrongMockedModel("uuid");
        Context context = getContext(model);

        // When
        function.apply(context);

        // Then
    }

    @EqualsAndHashCode(callSuper = true)
    private class WrongMockedModel extends MockedModel {

        public WrongMockedModel(final String uuid) {
            super(uuid);
        }

        @Override
        public void setField2(final String field2) {
            throw new UnsupportedOperationException();
        }
    }

    private Context getContext(final MockedModel model) {
        MockedEntity entity = new MockedEntity(1L, "uuid");
        entity.setField1("field1");
        entity.setField2("field2");

        return Context.builder()
                .entity(entity)
                .model(model)
                .build();
    }

    public MockedModel expectedModel() {
        MockedModel expectedModel = new MockedModel("uuid");
        expectedModel.setField1("field1");
        expectedModel.setField2("field2");
        return expectedModel;
    }
}
