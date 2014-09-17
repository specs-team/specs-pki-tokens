package org.specs.pkitokens.client;

import org.apache.log4j.Logger;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.VerificationCertProvider;

public class PkiTokenValidator {
    private static Logger log = Logger.getLogger(PkiTokenValidator.class);

    private VerificationCertProvider verificationCertProvider;
    private final TRLCache trlCache;

    public PkiTokenValidator(VerificationCertProvider verificationCertProvider, TRLCache trlCache) {
        this.verificationCertProvider = verificationCertProvider;
        this.trlCache = trlCache;
    }

    public Token decodeAndValidate(String encodedToken) throws Exception {
        return Token.decode(encodedToken, verificationCertProvider, trlCache);
    }
}
