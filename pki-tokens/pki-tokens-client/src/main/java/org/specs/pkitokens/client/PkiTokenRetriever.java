package org.specs.pkitokens.client;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.VerificationCertProvider;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PkiTokenRetriever {
    private static Logger log = Logger.getLogger(PkiTokenRetriever.class);

    private String stsAddress;
    private JerseyClient jerseyClient;
    private VerificationCertProvider verifCertProvider;

    public PkiTokenRetriever(String stsAddress,
                             String trustStoreFile, String trustStorePass,
                             String keyStoreFile, String keyStorePass,
                             VerificationCertProvider verifCertProvider) {
        this.stsAddress = stsAddress;
        jerseyClient = new JerseyClient(trustStoreFile, trustStorePass, keyStoreFile, keyStorePass);
        this.verifCertProvider = verifCertProvider;
    }

    public Token obtainToken(String username, String password, int slaId) throws Exception {

        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("password", password);
        data.put("slaId", slaId);

        Response response = jerseyClient.getClient()
                .target(stsAddress)
                .path("/pkitokens")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .post(Entity.json(data.toString()));

        if (response.getStatus() != 200) {
            throw new Exception(String.format("Unexpected response from the STS: %d %s",
                    response.getStatus(), response.getStatusInfo()));
        }

        String encodedToken = response.readEntity(String.class);

        // decode the token. There is no need to check the token revocation status.
        Token token = Token.decode(encodedToken, verifCertProvider, null);

        return token;
    }
}
