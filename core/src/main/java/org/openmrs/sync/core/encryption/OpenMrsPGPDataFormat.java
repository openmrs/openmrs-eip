package org.openmrs.sync.core.encryption;

import org.apache.camel.model.dataformat.PGPDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenMrsPGPDataFormat extends PGPDataFormat {

    /*public OpenMrsPGPDataFormat(@Value("${pgp.security.key.file}") final String encryptionKeyFileName,
                                @Value("${pgp.security.key.userId}") final String encryptionKeyUserId,
                                @Value("${pgp.signature.key.file}") final String signatureKeyFileName,
                                @Value("${pgp.signature.key.userId}") final String signatureKeyUserId,
                                @Value("${pgp.signature.key.password}") final String signatureKeyPassword) {
        setKeyFileName("file:" + encryptionKeyFileName);
        setKeyUserid(encryptionKeyUserId);
        setSignatureKeyFileName("file:" + signatureKeyFileName);
        setSignatureKeyUserid(signatureKeyUserId);
        setSignaturePassword(signatureKeyPassword);
    }*/
}
