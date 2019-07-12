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

@Service
public class PGPEncryptService extends AbstractSecurityService implements Processor {

    private SenderEncryptionProperties props;

    public PGPEncryptService(final SenderEncryptionProperties props) {
        this.props = props;
    }

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

    @Override
    public void process(final Exchange exchange) {
        exchange.getIn().setHeader(HEADER_USER_ID, props.getUserId());
        exchange.getIn().setBody(encryptAndSign((String) exchange.getIn().getBody()));
    }
}
