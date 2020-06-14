package org.openmrs.eip.component.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReceiverEncryptionPropertiesTest {

    @Test
    public void getKeysFolderPath_should_return_keys_folder_path() {
        // Given
        ReceiverEncryptionProperties props = new ReceiverEncryptionProperties();
        props.setKeysFolderPath("/path");

        // When
        String result = props.getKeysFolderPath();

        // Then
        assertEquals("/path", result);
    }

    @Test
    public void getPassword_should_return_password() {
        // Given
        ReceiverEncryptionProperties props = new ReceiverEncryptionProperties();
        props.setPassword("password");

        // When
        String result = props.getPassword();

        // Then
        assertEquals("password", result);
    }
}
