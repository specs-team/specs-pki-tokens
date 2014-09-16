package org.specs.pkitokens.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class CertUtilsTest {
    private static final String SIGNING_CERT_FILE = "src/test/resources/signing-cert.pem";

    @Before
    public void setUp() throws Exception {
        // register BouncyCastleProvider
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Test
    public void testReadPrivateKey() throws Exception {

    }

    @Test
    public void testReadCertificate() throws Exception {

    }

    @Test
    public void testReadCertificate1() throws Exception {

    }

    @Test
    public void testWriteToPem() throws Exception {
        X509Certificate x509Certificate = CertUtils.readCertificate(new File(SIGNING_CERT_FILE));
        String certPem = CertUtils.writeToPem(x509Certificate);
        String original = readFile(new File(SIGNING_CERT_FILE));
        assertEquals(certPem.replaceAll("\\s+", ""), original.replaceAll("\\s+", ""));
    }

    private String readFile(File file) throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        scan.useDelimiter("\\Z");
        return scan.next();
    }
}