package org.openmrs.sync.core.utils;

import com.google.common.base.CaseFormat;

public final class StringUtils {

    private StringUtils() {}

    public static String fromCamelCaseToSnakeCase(String stringToConvert) {
        if (stringToConvert == null) {
            return null;
        }
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, stringToConvert);
    }
}
