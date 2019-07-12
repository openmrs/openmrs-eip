package org.openmrs.sync.core.service.security;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.callbacks.KeyringConfigCallbacks;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs;
import org.bouncycastle.openpgp.PGPException;
import org.openmrs.sync.core.config.EncryptionProperties;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class AbstractSecurityService {

    protected static final String HEADER_USER_ID = "pgp.key.userId";

    private InMemoryKeyring keyRing;

    protected InMemoryKeyring getKeyRing(final EncryptionProperties props) {
        if (keyRing == null) {
            initKeyRing(props);
        }
        return keyRing;
    }

    private void initKeyRing(final EncryptionProperties props) {
        try {
            keyRing = KeyringConfigs.forGpgExportedKeys(KeyringConfigCallbacks.withPassword(props.getPassword()));

            FileUtils.getPublicKeysFromFolder(props.getKeysFolder()).forEach(this::addPublicKey);

            keyRing.addSecretKey(FileUtils.getPrivateKeysFromFolder(props.getKeysFolder()));
        } catch (IOException | PGPException e) {
            throw new OpenMrsSyncException("Error while initiating key ring", e);
        }
    }

    private void addPublicKey(final byte[] keyFile) {
        try {
            keyRing.addPublicKey(keyFile);
        } catch (IOException | PGPException e) {
            throw new OpenMrsSyncException("Impossible to add key to key ring", e);
        }
    }

    protected String toString(ByteArrayOutputStream encryptedOutputStream) {
        try {
            return encryptedOutputStream.toString(UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new OpenMrsSyncException("Error while converting output stream to string", e);
        }
    }
}
