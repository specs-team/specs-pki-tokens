package org.specs.pkitokens.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import java.security.Security;

import static org.junit.Assert.assertEquals;

public class TokenTest {
    private static final String SIGNER_NAME = "specs-demo";
    private static final String SIGNING_KEYSTORE_FILE = "src/test/resources/signing-keystore.p12";
    private static final String SIGNING_KEYSTORE_PASS = "password";
    private static final String SIGNING_CERT_FINGERPRINT = "01:18:BD:FE:5A:AF:DC:64:21:F5:07:93:7C:87:50:F6:5E:4C:75:B0";
    private static final String SIGNING_PRIVATE_KEY_PASS = "password";

    @Before
    public void setUp() throws Exception {
        // register BouncyCastleProvider
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Test
    public void testSign() throws Exception {
        TokenSigner signer = new TokenSigner(
                SIGNER_NAME,
                SIGNING_KEYSTORE_FILE,
                SIGNING_KEYSTORE_PASS,
                SIGNING_CERT_FINGERPRINT,
                SIGNING_PRIVATE_KEY_PASS
        );

        Token token = Utils.createToken();
        String encodedToken = token.sign(signer);
        System.out.println("Encoded token:\n" + encodedToken);
        System.out.println();
        System.out.println("Token dump:\n" + token.dump());

        VerificationCertProvider verifCertProvider = new VerificationCertProviderP12(
                SIGNING_KEYSTORE_FILE,
                SIGNING_KEYSTORE_PASS
        );

        Token token1 = Token.decode(encodedToken, verifCertProvider);

        assertEquals(token.getTokenId(), token1.getTokenId());
        assertEquals(token.toJson(), token1.toJson());
        assertEquals(token1.getEncodedValue(), encodedToken);
    }

}