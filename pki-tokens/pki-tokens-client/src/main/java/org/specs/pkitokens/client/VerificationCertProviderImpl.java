package org.specs.pkitokens.client;

import org.specs.pkitokens.core.CertUtils;
import org.specs.pkitokens.core.VerificationCertProvider;

import javax.ws.rs.core.Response;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class VerificationCertProviderImpl implements VerificationCertProvider {
    private JerseyClient jerseyClient;
    private String stsAddress;
    private Map<String, X509Certificate> certCache = new HashMap<String, X509Certificate>();

    public VerificationCertProviderImpl(String stsAddress, String trustStoreFile, String trustStorePass) {
        this.jerseyClient = new JerseyClient(trustStoreFile, trustStorePass);
        this.stsAddress = stsAddress;
    }

    @Override
    public X509Certificate getCertificate(String fingerprint) throws Exception {
        if (!certCache.containsKey(fingerprint)) {
            Response response = jerseyClient.getClient()
                    .target(stsAddress)
                    .path("/certificates/" + fingerprint)
                    .request()
                    .accept("application/x-pem-file")
                    .get();

            if (response.getStatus() != 200) {
                throw new Exception(String.format("Failed to obtain signing certificate from the STS: %d %s",
                        response.getStatus(), response.getStatusInfo()));
            }

            String verificationCertPem = response.readEntity(String.class);
            X509Certificate verificationCert = CertUtils.readCertificate(verificationCertPem);
            certCache.put(fingerprint, verificationCert);
            return verificationCert;
        }
        else {
            return certCache.get(fingerprint);
        }
    }
}
