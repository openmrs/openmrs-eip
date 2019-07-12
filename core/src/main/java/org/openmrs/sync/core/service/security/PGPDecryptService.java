package org.openmrs.sync.core.service.security;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.util.io.Streams;
import org.openmrs.sync.core.config.ReceiverEncryptionProperties;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.NoSuchProviderException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class PGPDecryptService extends AbstractSecurityService implements Processor {

    private ReceiverEncryptionProperties props;

    public PGPDecryptService(final ReceiverEncryptionProperties props) {
        this.props = props;
    }

    public String verifyAndDecrypt(final String encryptedMessage,
                                   final String userId) {

        InMemoryKeyring keyRing = getKeyRing(props);

        ByteArrayOutputStream unencryptedOutputStream = new ByteArrayOutputStream();
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(unencryptedOutputStream);
                InputStream bouncyGPGInputStream = BouncyGPG
                        .decryptAndVerifyStream()
                        .withConfig(keyRing)
                        .andRequireSignatureFromAllKeys(userId)
                        .fromEncryptedInputStream(IOUtils.toInputStream(encryptedMessage, UTF_8))
        ) {
            Streams.pipeAll(bouncyGPGInputStream, bufferedOutputStream);
        } catch (IOException | PGPException | NoSuchProviderException e) {
            throw new OpenMrsSyncException("Error during decryption process", e);
        }
        return toString(unencryptedOutputStream);
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        String userId = (String) exchange.getIn().getHeader(HEADER_USER_ID);
        exchange.getIn().setBody(verifyAndDecrypt((String) exchange.getIn().getBody(), userId));
    }
}
