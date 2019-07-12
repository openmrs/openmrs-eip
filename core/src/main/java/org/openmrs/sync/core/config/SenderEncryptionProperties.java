package org.openmrs.sync.core.config;

public class SenderEncryptionProperties implements EncryptionProperties {

    private String keysFolder;

    private String userId;

    private String password;

    private String receiverUserId;

    @Override
    public String getKeysFolder() {
        return keysFolder;
    }

    public void setKeysFolder(final String keysFolder) {
        this.keysFolder = keysFolder;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(final String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }
}
