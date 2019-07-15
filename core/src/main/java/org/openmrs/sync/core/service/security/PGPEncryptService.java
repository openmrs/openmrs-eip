package org.openmrs.sync.core.service.security;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.util.io.Streams;
import org.openmrs.sync.core.config.SenderEncryptionProperties;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * The service to encrypt outgoing messages
 */
@Service
public class PGPEncryptService extends AbstractSecurityService implements Processor {

    private SenderEncryptionProperties props;

    public PGPEncryptService(final SenderEncryptionProperties props) {
        this.props = props;
    }

    /**
     * Encrypts and sign the message in parameter with the private key present in the key ring
     * @param unencryptedMessage the message to encrypt
     * @return the encrypted message
     */
    public String encryptAndSign(final String unencryptedMessage) {
        InMemoryKeyring keyRing = getKeyRing(props);

        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(encryptedOutputStream);
                OutputStream bouncyGPGOutputStream = BouncyGPG.encryptToStream()
                        .withConfig(keyRing)
                        .withStrongAlgorithms()
                        .toRecipient(props.getReceiverUserId())
                        .andSignWith(props.getUserId())
                        .armorAsciiOutput()
                        .andWriteTo(bufferedOutputStream)
        ) {
            Streams.pipeAll(new ByteArrayInputStream(unencryptedMessage.getBytes()), bouncyGPGOutputStream);
        } catch (IOException | PGPException | NoSuchAlgorithmException | SignatureException | NoSuchProviderException e) {
            throw new OpenMrsSyncException("Error during encryption process", e);
        }
        return toString(encryptedOutputStream);
    }

    /**
     * Encrypts and sign the message and puts it in the body
     * Also puts the sender's private key userId in the header for the reveiving part
     * to know with which public key to decrypt the message
     * @param exchange the Camel exchange object
     */
    @Override
    public void process(final Exchange exchange) {
        exchange.getIn().setHeader(HEADER_USER_ID, props.getUserId());
        exchange.getIn().setBody(encryptAndSign((String) exchange.getIn().getBody()));
    }
}
