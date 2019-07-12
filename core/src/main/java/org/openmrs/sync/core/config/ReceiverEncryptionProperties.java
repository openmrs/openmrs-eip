package org.openmrs.sync.core.config;

import org.springframework.stereotype.Component;

public class ReceiverEncryptionProperties implements EncryptionProperties {

    private String keysFolder;

    private String password;

    @Override
    public String getKeysFolder() {
        return keysFolder;
    }

    public void setKeysFolder(final String keysFolder) {
        this.keysFolder = keysFolder;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
