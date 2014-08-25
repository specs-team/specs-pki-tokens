package org.specs.pkitokens.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.Security;

import static org.junit.Assert.assertEquals;

public class TokenTest {
    private static final String SIGNING_CERT = "src/test/resources/signing-cert.pem";
    private static final String SIGNING_PRIVATE_KEY = "src/test/resources/signing-key.pem";
    private static final String SIGNING_PRIVATE_KEY_PASS = "specs";

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
                new File(SIGNING_CERT),
                new File(SIGNING_PRIVATE_KEY),
                SIGNING_PRIVATE_KEY_PASS
        );

        Token token = Utils.createToken();
        String encodedToken = token.sign(signer);
        System.out.println(encodedToken);

        TokenSigner signer1 = new TokenSigner(new File(SIGNING_CERT));
        Token token1 = Token.decode(encodedToken, signer1);

        assertEquals(token.toJson(), token1.toJson());
        assertEquals(token1.getEncodedValue(), encodedToken);
    }

}