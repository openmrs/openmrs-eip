package org.openmrs.eip.component.config;

/**
 * Class holding encryption properties on receiver's side
 */
public class ReceiverEncryptionProperties implements EncryptionProperties {

    private String keysFolderPath;

    private String password;

    /**
     * Path to the folder containing the private key and the public keys of the module
     * If the path starts with 'file:', the program will look in the absolute path following the prefix.
     * Otherwise, it will look relatively to the root folder of the application
     * @return path
     */
    @Override
    public String getKeysFolderPath() {
        return keysFolderPath;
    }

    public void setKeysFolderPath(final String keysFolderPath) {
        this.keysFolderPath = keysFolderPath;
    }

    /**
     * Password for the private pgp key
     * @return password
     */
    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
