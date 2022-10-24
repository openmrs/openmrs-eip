package org.openmrs.eip.component.service.security;

import lombok.extern.slf4j.Slf4j;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.util.io.Streams;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.config.SenderEncryptionProperties;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * The service to encrypt outgoing messages
 */
@Slf4j
@Service("pgpEncryptService")
public class PGPEncryptService extends AbstractSecurityService implements Processor {
	
	private SenderEncryptionProperties props;
	
	public PGPEncryptService(final SenderEncryptionProperties props) {
		this.props = props;
	}
	
	/**
	 * Encrypts and sign the message in parameter with the private key present in the key ring
	 * 
	 * @param unencryptedMessage the message to encrypt
	 * @return the encrypted message
	 */
	public String encryptAndSign(final String unencryptedMessage) {
		InMemoryKeyring keyRing = getKeyRing(props);
		
		ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
		try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(encryptedOutputStream);
		        OutputStream bouncyGPGOutputStream = BouncyGPG.encryptToStream().withConfig(keyRing).withStrongAlgorithms()
		                .toRecipient(props.getReceiverUserId()).andSignWith(props.getUserId()).armorAsciiOutput()
		                .andWriteTo(bufferedOutputStream)) {
			Streams.pipeAll(new ByteArrayInputStream(unencryptedMessage.getBytes()), bouncyGPGOutputStream);
			
			log.info("Successfully encrypted the message");
			
		}
		catch (IOException | PGPException | NoSuchAlgorithmException | SignatureException | NoSuchProviderException e) {
			throw new EIPException("Error during encryption process", e);
		}
		return toString(encryptedOutputStream);
	}
	
	/**
	 * Encrypts and sign the message and puts it in the body Also puts the sender's private key userId
	 * in the header for the reveiving part to know with which public key to decrypt the message
	 *
	 * @param exchange the Camel exchange object
	 */
	@Override
	public void process(final Exchange exchange) {
		String body = HEADER_USER_KEY_PROP + props.getUserId() + "\n";
		body += encryptAndSign(exchange.getIn().getBody(String.class));
		exchange.getIn().setBody(body);
	}
}
