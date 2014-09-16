package org.specs.pkitokens.core;

import java.security.cert.X509Certificate;

public interface VerificationCertProvider {

    public X509Certificate getCertificate(String fingerprint) throws Exception;

}
