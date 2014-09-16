package org.specs.pkitokens.core;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

public class VerificationCertProviderP12 implements VerificationCertProvider {

    private KeyStore keyStore;

    public VerificationCertProviderP12(String keystoreFile, String keystorePass) throws Exception {
        keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keystoreFile), keystorePass.toCharArray());
    }

    @Override
    public X509Certificate getCertificate(String fingerprint) throws KeyStoreException {
        return (X509Certificate) keyStore.getCertificate(fingerprint);
    }
}
