package org.openmrs.eip.component.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SenderEncryptionPropertiesTest {

    @Test
    public void getKeysFolderPath_should_return_keys_folder_path() {
        // Given
        SenderEncryptionProperties props = new SenderEncryptionProperties();
        props.setKeysFolderPath("/path");

        // When
        String result = props.getKeysFolderPath();

        // Then
        assertEquals("/path", result);
    }

    @Test
    public void getUserId_should_return_user_id() {
        // Given
        SenderEncryptionProperties props = new SenderEncryptionProperties();
        props.setUserId("userId");

        // When
        String result = props.getUserId();

        // Then
        assertEquals("userId", result);
    }

    @Test
    public void getPassword_should_return_password() {
        // Given
        SenderEncryptionProperties props = new SenderEncryptionProperties();
        props.setPassword("password");

        // When
        String result = props.getPassword();

        // Then
        assertEquals("password", result);
    }

    @Test
    public void getReceiverUserId_should_return_receiver_user_id() {
        // Given
        SenderEncryptionProperties props = new SenderEncryptionProperties();
        props.setReceiverUserId("receiverUserId");

        // When
        String result = props.getReceiverUserId();

        // Then
        assertEquals("receiverUserId", result);
    }
}
