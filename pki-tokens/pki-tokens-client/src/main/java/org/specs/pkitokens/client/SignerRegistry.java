package org.specs.pkitokens.client;

import org.specs.pkitokens.core.CertUtils;
import org.specs.pkitokens.core.TokenSigner;

import javax.ws.rs.core.Response;
import java.security.cert.X509Certificate;

public class SignerRegistry {
    private JerseyClient jerseyClient;
    private String stsAddress;
    private TokenSigner tokenSigner;

    public SignerRegistry(String stsAddress, String trustStoreFile, String trustStorePass) {
        this.jerseyClient = new JerseyClient(trustStoreFile, trustStorePass);
        this.stsAddress = stsAddress;
    }

    public TokenSigner getTokenSigner() throws Exception {
        if (tokenSigner == null) {
            Response response = jerseyClient.getClient()
                    .target(stsAddress)
                    .path("/certificates/signing")
                    .request()
                    .accept("application/x-pem-file")
                    .get();

            if (response.getStatus() != 200) {
                throw new Exception(String.format("Failed to obtain signing certificate from the STS: %d %s",
                        response.getStatus(), response.getStatusInfo()));
            }

            String signingCertPem = response.readEntity(String.class);
            X509Certificate signingCertificate = CertUtils.readCertificate(signingCertPem);
            tokenSigner = new TokenSigner(signingCertificate);
        }

        return tokenSigner;
    }
}
