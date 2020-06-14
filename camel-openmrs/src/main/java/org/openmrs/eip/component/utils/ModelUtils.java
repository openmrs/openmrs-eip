package org.openmrs.eip.component.utils;

import org.json.JSONObject;
import org.openmrs.eip.component.mapper.operations.DecomposedUuid;

import java.util.Optional;

public final class ModelUtils {

    private ModelUtils() {}

    /**
     * Takes a uuid as a parameter formatted as follows: org.openmrs.package.classname(uuid) and returns
     * an Optional of DecomposedUuid as a result
     * @param fullUuid the uuid as a string
     * @return a decomposedUuid
     */
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

    /**
     * Extracts the uuid from the JSON body located at the given property name
     * @param body JSON body message
     * @param uuidPropertyName the field name of the uuid
     * @return the uuid
     */
    public static String extractUuid(final String body,
                                     final String uuidPropertyName) {
        Optional<DecomposedUuid> decomposedUuid = decomposeUuid(new JSONObject(body).getJSONObject("model").getString(uuidPropertyName));

        return decomposedUuid.map(DecomposedUuid::getUuid).orElse(null);
    }
}
