package org.openmrs.sync.odoo.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OdooPropertiesTest {

    @Test
    public void setDbName_should_return_dbName() {
        // Given
        OdooProperties props = new OdooProperties();
        props.setDbName("dbName");

        // When
        String result = props.getDbName();

        // Then
        assertEquals("dbName", result);
    }

    @Test
    public void getPassword_should_return_password() {
        // Given
        OdooProperties props = new OdooProperties();
        props.setPassword("password");

        // When
        String result = props.getPassword();

        // Then
        assertEquals("password", result);
    }

    @Test
    public void getUsername_should_return_user_name() {
        // Given
        OdooProperties props = new OdooProperties();
        props.setUsername("username");

        // When
        String result = props.getUsername();

        // Then
        assertEquals("username", result);
    }

    @Test
    public void getUrl_should_return_password() {
        // Given
        OdooProperties props = new OdooProperties();
        props.setUrl("url");

        // When
        String result = props.getUrl();

        // Then
        assertEquals("url", result);
    }
}
