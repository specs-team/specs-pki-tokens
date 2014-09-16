package org.specs.pkitokens.core;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Represents an Identity that can sign tokens.
 */
public class TokenSigner {

    private String signerName;
    private final String keystoreFile;
    private final String keystorePass;
    private String signingCertFingerprint;
    private String signingPrivateKeyPass;

    private KeyStore keyStore;
    private X509Certificate signingCert;
    private PrivateKey signingKey;

    public TokenSigner(String signerName,
                       String keystoreFile,
                       String keystorePass,
                       String signingCertFingerprint,
                       String signingPrivateKeyPass) throws Exception {
        this.signerName = signerName;
        this.keystoreFile = keystoreFile;
        this.keystorePass = keystorePass;
        this.signingCertFingerprint = signingCertFingerprint;
        this.signingPrivateKeyPass = signingPrivateKeyPass;

        try {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keystoreFile), keystorePass.toCharArray());
            signingCert = (X509Certificate) keyStore.getCertificate(signingCertFingerprint);
            signingKey = (PrivateKey) keyStore.getKey(signingCertFingerprint, signingPrivateKeyPass.toCharArray());
        }
        catch (Exception e) {
            throw new Exception(String.format("Failed to read keystore '%s': %s", keystoreFile, e.getMessage()));
        }
    }

    public String getSignerName() {
        return signerName;
    }

    public X509Certificate getSigningCertificate() {
        return signingCert;
    }

    public X509Certificate getSigningCertificate(String fingerprint) throws KeyStoreException {
        return (X509Certificate) keyStore.getCertificate(fingerprint);
    }

    public PrivateKey getSigningPrivateKey() {
        return signingKey;
    }

    public String getSignerFingerprint() {
        return signingCertFingerprint;
    }
}
