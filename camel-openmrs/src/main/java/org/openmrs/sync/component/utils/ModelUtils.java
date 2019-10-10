package org.openmrs.sync.component.utils;

import org.json.JSONObject;
import org.openmrs.sync.component.mapper.operations.DecomposedUuid;

import java.util.Optional;

public final class ModelUtils {

    private ModelUtils() {}

    public static Optional<DecomposedUuid> decomposeUuid(final String fullUuid) {
        if (fullUuid == null) {
            return Optional.empty();
        }
        int openingParenthesisIndex = fullUuid.indexOf('(');
        int closingParenthesisIndex = fullUuid.indexOf(')');
        String entityTypeName = fullUuid.substring(0, openingParenthesisIndex);
        String uuid = fullUuid.substring(openingParenthesisIndex + 1, closingParenthesisIndex);

        return Optional.of(new DecomposedUuid(entityTypeName, uuid));
    }

    public static String extractUuid(final String body,
                                     final String uuidName) {
        Optional<DecomposedUuid> decomposedUuid = decomposeUuid(new JSONObject(body).getJSONObject("model").getString(uuidName));

        return decomposedUuid.map(DecomposedUuid::getUuid).orElse(null);
    }
}
