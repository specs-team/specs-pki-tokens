package org.specs.pkitokens.core;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 *  Represents an Identity that can sign tokens.
 */
public class TokenSigner {

    private X509Certificate signingCertificate;
    private PrivateKey signingPrivateKey;

    public TokenSigner(X509Certificate signingCertificate) {
        this.signingCertificate = signingCertificate;
    }

    public TokenSigner(File signingCertificateFile) throws Exception {

        this.signingCertificate = CertUtils.readCertificate(signingCertificateFile);
    }

    public TokenSigner(File signingCertificateFile,
                       File signingPrivateKeyFile,
                       String signingPrivateKeyPassword) throws Exception {

        this.signingCertificate = CertUtils.readCertificate(signingCertificateFile);
        this.signingPrivateKey = CertUtils.readPrivateKey(signingPrivateKeyFile, signingPrivateKeyPassword);
    }

    public X509Certificate getSigningCertificate() {
        return signingCertificate;
    }

    public PrivateKey getSigningPrivateKey() {
        return signingPrivateKey;
    }

    public String getSignerName() {
        return signingCertificate.getSubjectDN().getName();
    }
}
