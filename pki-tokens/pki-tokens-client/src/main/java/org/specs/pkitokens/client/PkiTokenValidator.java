package org.specs.pkitokens.client;

import org.apache.log4j.Logger;
import org.specs.pkitokens.core.RevocationVerifier;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.VerificationCertProvider;

public class PkiTokenValidator {
    private static Logger log = Logger.getLogger(PkiTokenValidator.class);

    private VerificationCertProvider verificationCertProvider;
    private RevocationVerifier revocationVerifier;

    public PkiTokenValidator(VerificationCertProvider verificationCertProvider, RevocationVerifier revocationVerifier) {
        this.verificationCertProvider = verificationCertProvider;
        this.revocationVerifier = revocationVerifier;
    }

    public Token decodeAndValidate(String encodedToken) throws Exception {
        return Token.decode(encodedToken, verificationCertProvider, revocationVerifier);
    }
}
