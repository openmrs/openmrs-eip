package org.openmrs.eip.component.service.security;

import lombok.extern.slf4j.Slf4j;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.util.io.Streams;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.config.ReceiverEncryptionProperties;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.NoSuchProviderException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The service to decrypt incoming messages
 */
@Slf4j
@Service("pgpDecryptService")
public class PGPDecryptService extends AbstractSecurityService implements Processor {

    private ReceiverEncryptionProperties props;

    private static final String LINE_SEPARATOR_REGEX = "\r\n|\r|\n";

    public PGPDecryptService(final ReceiverEncryptionProperties props) {
        this.props = props;
    }

    /**
     * Verifies the signature and decrypts the message in parameter with the public key
     * corresponding to the given userId
     * @param encryptedMessage the message to decrypt
     * @return the encrypted message
     */
    public String verifyAndDecrypt(final String encryptedMessage) {

        String senderUserId = extractSenderUserId(encryptedMessage);

        InMemoryKeyring keyRing = getKeyRing(props);

        ByteArrayOutputStream unencryptedOutputStream = new ByteArrayOutputStream();
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(unencryptedOutputStream);
                InputStream bouncyGPGInputStream = BouncyGPG
                        .decryptAndVerifyStream()
                        .withConfig(keyRing)
                        .andRequireSignatureFromAllKeys(senderUserId)
                        .fromEncryptedInputStream(IOUtils.toInputStream(encryptedMessage, UTF_8))
        ) {
            Streams.pipeAll(bouncyGPGInputStream, bufferedOutputStream);

        } catch (IOException | PGPException | NoSuchProviderException e) {
            throw new EIPException("Error during decryption process", e);
        }

        return toString(unencryptedOutputStream);
    }

    private String extractSenderUserId(final String encryptedMessage) {
        String[] splittedString = encryptedMessage.split(LINE_SEPARATOR_REGEX, 2);

        if (!splittedString[0].startsWith(HEADER_USER_KEY_PROP)) {
            throw new EIPException("Message should start with 'sender:'");
        }

        return splittedString[0].replace(HEADER_USER_KEY_PROP, "");
    }

    /**
     * Verifies and decrypts the message and puts it in the body with the senders
     * public key userId from the header
     * to know with which public key to decrypt the message
     * @param exchange the Camel exchange object
     */
    @Override
    public void process(final Exchange exchange) {
        exchange.getIn().setBody(verifyAndDecrypt((String) exchange.getIn().getBody()));
    }
}
