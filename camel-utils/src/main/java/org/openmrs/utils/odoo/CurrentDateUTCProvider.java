package org.openmrs.utils.odoo;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component("currentDateUTCProvider")
public class CurrentDateUTCProvider {

    public String getDateAsString() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return now.format(formatter);
    }
}
