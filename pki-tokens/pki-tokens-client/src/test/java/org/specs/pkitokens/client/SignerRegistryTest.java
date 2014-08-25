package org.specs.pkitokens.client;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.specs.pkitokens.core.TokenSigner;

import java.security.Security;

import static org.junit.Assert.assertNotNull;

public class SignerRegistryTest {
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
    public void testGetTokenSigner() throws Exception {
        JerseyClient jerseyClient = new JerseyClient(TRUSTSTORE_FILE, TRUSTSTORE_PASS);
        SignerRegistry signerRegistry = new SignerRegistry(STS_ADDRESS, TRUSTSTORE_FILE, TRUSTSTORE_PASS);
        TokenSigner tokenSigner = signerRegistry.getTokenSigner();
        assertNotNull(tokenSigner.getSigningCertificate());
    }
}