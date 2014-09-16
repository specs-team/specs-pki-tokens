package org.specs.pkitokens.client;

import org.apache.log4j.Logger;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.VerificationCertProvider;
import org.specs.pkitokens.core.exceptions.ValidationException;

public class PkiTokenValidator {
    private static Logger log = Logger.getLogger(PkiTokenValidator.class);

    private VerificationCertProvider verificationCertProvider;
    private final TRLCache trlCache;

    public PkiTokenValidator(VerificationCertProvider verificationCertProvider, TRLCache trlCache) {
        this.verificationCertProvider = verificationCertProvider;
        this.trlCache = trlCache;
    }

    public Token decodeAndValidate(String encodedToken) throws Exception {
        Token token = Token.decode(encodedToken, verificationCertProvider);
        if (trlCache.isRevoked(token.getTokenId())) {
            throw new ValidationException("The token is revoked.");
        }

        return token;
    }
}
