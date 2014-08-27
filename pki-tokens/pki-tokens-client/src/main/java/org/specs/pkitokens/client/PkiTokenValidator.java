package org.specs.pkitokens.client;

import org.apache.log4j.Logger;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.exceptions.ValidationException;

public class PkiTokenValidator {
    private static Logger log = Logger.getLogger(PkiTokenValidator.class);

    private SignerRegistry signerRegistry;
    private final TRLCache trlCache;

    public PkiTokenValidator(SignerRegistry signerRegistry, TRLCache trlCache) {
        this.signerRegistry = signerRegistry;
        this.trlCache = trlCache;
    }

    public Token decodeAndValidate(String encodedToken) throws Exception {
        Token token = Token.decode(encodedToken, signerRegistry.getTokenSigner());
        if (trlCache.isRevoked(token.getHeader().getTokenId())) {
            throw new ValidationException("The token is revoked.");
        }

        return token;
    }
}
