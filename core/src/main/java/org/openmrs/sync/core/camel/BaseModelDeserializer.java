package org.openmrs.sync.core.camel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.openmrs.sync.core.model.BaseModel;

import java.io.IOException;

public class BaseModelDeserializer extends StdDeserializer<BaseModel> {

    public BaseModelDeserializer() {
        super(BaseModel.class);
    }

    @Override
    public BaseModel deserialize(final JsonParser p,
                                 final DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();

        Class<? extends BaseModel> type = ((TransferObject) p.getParsingContext().getParent().getCurrentValue()).getTableToSync().getModelClass();

        JsonNode node = codec.readTree(p);

        return codec.treeToValue(node, type);
    }
}
