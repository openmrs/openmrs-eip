package org.openmrs.sync.common.marshalling;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.openmrs.sync.common.model.sync.BaseModel;
import org.openmrs.sync.common.model.sync.SyncModel;

import java.io.IOException;

/**
 * Overrides deserialize method to get the model class from the parent TransferObject
 */
public class BaseModelDeserializer extends StdDeserializer<BaseModel> {

    public BaseModelDeserializer() {
        super(BaseModel.class);
    }

    @Override
    public BaseModel deserialize(final JsonParser p,
                                 final DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();

        Class<? extends BaseModel> type = ((SyncModel) p.getParsingContext().getParent().getCurrentValue()).getTableToSyncModelClass();

        JsonNode node = codec.readTree(p);

        return codec.treeToValue(node, type);
    }
}
