package org.specs.pkitokens.client;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.specs.pkitokens.core.Token;

import java.security.Security;

public class PkiTokenRetrieverTest {
    private static final String TRUSTSTORE_FILE = "src/test/resources/specs-truststore.jks";
    private static final String TRUSTSTORE_PASS = "password";
    private static final String STS_ADDRESS = "https://localhost:8443/sts";

    @Before
    public void setUp() throws Exception {
        // register BouncyCastleProvider
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Test
    public void testObtainToken() throws Exception {
        PkiTokenRetriever pkiTokenRetriever = new PkiTokenRetriever(STS_ADDRESS, TRUSTSTORE_FILE, TRUSTSTORE_PASS);
        Token token = pkiTokenRetriever.obtainToken("testuser", "password", 1);
        System.out.println(token.toJson());
    }
}