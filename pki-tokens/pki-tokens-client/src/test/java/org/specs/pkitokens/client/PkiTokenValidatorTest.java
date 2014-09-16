package org.specs.pkitokens.client;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.VerificationCertProvider;

import java.security.Security;

import static org.junit.Assert.assertEquals;

public class PkiTokenValidatorTest {
    private static Logger log = Logger.getLogger(PkiTokenValidatorTest.class);

    private static final String TRUSTSTORE_FILE = "src/test/resources/specs-truststore.jks";
    private static final String TRUSTSTORE_PASS = "password";
    private static final String STS_ADDRESS = "https://localhost:8443/sts";

    private TRLCache trlCache;

    @Before
    public void setUp() throws Exception {
        // register BouncyCastleProvider
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        trlCache = new TRLCache(STS_ADDRESS, TRUSTSTORE_FILE, TRUSTSTORE_PASS);
    }

    @After
    public void tearDown() throws InterruptedException {
        trlCache.stop();
    }

    @Test
    public void testObtainToken() throws Exception {
        PkiTokenRetriever pkiTokenRetriever = new PkiTokenRetriever(STS_ADDRESS, TRUSTSTORE_FILE, TRUSTSTORE_PASS);
        Token token = pkiTokenRetriever.obtainToken("testuser", "somepassword", 1);
        String encodedToken = token.getEncodedValue();
        log.debug("Token obtained: " + encodedToken);

        VerificationCertProvider verifCertProvider = new VerificationCertProviderImpl(STS_ADDRESS, TRUSTSTORE_FILE, TRUSTSTORE_PASS);
        PkiTokenValidator pkiTokenValidator = new PkiTokenValidator(verifCertProvider, trlCache);

        Token token1 = pkiTokenValidator.decodeAndValidate(encodedToken);
        log.debug("The token is valid.");
        assertEquals(token.toJson(), token1.toJson());

    }
}