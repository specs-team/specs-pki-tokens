package org.specs.pkitokens.client;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import java.security.Security;

public class TRLCacheTest {
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
    public void testGetTRL() throws Exception {
        TRLCache trlCache = new TRLCache(STS_ADDRESS, TRUSTSTORE_FILE, TRUSTSTORE_PASS);
        Thread.sleep(1000);
        trlCache.stop();
    }
}