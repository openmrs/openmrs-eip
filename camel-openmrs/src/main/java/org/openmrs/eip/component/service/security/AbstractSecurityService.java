package org.openmrs.eip.component.service.security;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.callbacks.KeyringConfigCallbacks;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs;
import org.bouncycastle.openpgp.PGPException;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.FileUtils;
import org.openmrs.eip.component.config.EncryptionProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * IMPORTANT. All classes implementing this abstract class should be singletons
 */
public abstract class AbstractSecurityService {

    protected static final String HEADER_USER_KEY_PROP = "sender:";

    private InMemoryKeyring keyRing;

    /**
     * Returns the key ring containing all the keys registered in the keysFolderPath of the encryption
     * properties.n
     * Should remain in memory. The keyring is initiated on first call
     * @param props the encryption properties
     * @return the key ring object
     */
    protected InMemoryKeyring getKeyRing(final EncryptionProperties props) {
        if (keyRing == null) {
            initKeyRing(props);
        }
        return keyRing;
    }

    private void initKeyRing(final EncryptionProperties props) {
        try {
            keyRing = KeyringConfigs.forGpgExportedKeys(KeyringConfigCallbacks.withPassword(props.getPassword()));

            FileUtils.getPublicKeysFromFolder(props.getKeysFolderPath()).forEach(this::addPublicKey);

            keyRing.addSecretKey(FileUtils.getPrivateKeysFromFolder(props.getKeysFolderPath()));
        } catch (IOException | PGPException e) {
            throw new EIPException("Error while initiating key ring", e);
        }
    }

    private void addPublicKey(final byte[] keyFile) {
        try {
            keyRing.addPublicKey(keyFile);
        } catch (IOException | PGPException e) {
            throw new EIPException("Impossible to add key to key ring", e);
        }
    }

    protected String toString(ByteArrayOutputStream encryptedOutputStream) {
        try {
            return encryptedOutputStream.toString(UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new EIPException("Error while converting output stream to string", e);
        }
    }
}
