package org.specs.pkitokens.sts.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.TokenSigner;
import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.utils.Conf;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.security.Security;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class PkiTokensRevocationTest extends JerseyTest {

    public PkiTokensRevocationTest() throws Exception {
        super(new WebAppDescriptor.Builder("org.specs.pkitokens.sts.rest").build());
    }

    @Before
    public void setUp() throws Exception {
        EMF.init("TestDerbyPersistenceUnit");
        Conf.load("src/test/resources/test-config.xml");
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @After
    public void tearDown() throws Exception {
        EMF.close();
    }

    @Test
    public void testCreateToken() throws Exception {
        WebResource webResource = resource();

        // request a PKI token
        JSONObject reqData = new JSONObject();
        reqData.put("username", "testuser");
        reqData.put("password", "password");
        reqData.put("slaId", "1");
        String encodedToken = webResource.path("/pkitokens").post(String.class, reqData);

        TokenSigner tokenSigner = new TokenSigner(Conf.getSigningCertificateFile());
        Token token = Token.decode(encodedToken, tokenSigner);

        // token revocation list should be empty
        JSONObject trl = webResource.path("/trl").get(JSONObject.class);
        JSONArray trlItems = trl.getJSONArray("tokens");
        assertEquals(trlItems.length(), 0);

        // revoke the token
        URI tokenUri = UriBuilder.fromPath("/pkitokens/{tokenId}").build(token.getHeader().getTokenId());
        ClientResponse response = webResource.path(tokenUri.toString()).delete(ClientResponse.class);
        assertEquals(response.getStatus(), 200);

        // token is already revoked, status code 304 should be returned
        response = webResource.path(tokenUri.toString()).delete(ClientResponse.class);
        assertEquals(response.getStatus(), 304);

        // token revocation list should contain the token
        trl = webResource.path("/trl").get(JSONObject.class);
        trlItems = trl.getJSONArray("tokens");
        assertEquals(trlItems.length(), 1);
        JSONObject trlItem0 = trlItems.getJSONObject(0);
        assertEquals(trlItem0.getString("id"), token.getHeader().getTokenId());
        assertEquals(token.getHeader().getExpiryDate().getTime(), trlItem0.getLong("exp"));

        // get revoked tokens from now
        trl = webResource
                .path("/trl")
                .queryParam("from", Long.toString(new Date().getTime()))
                .get(JSONObject.class);
        trlItems = trl.getJSONArray("tokens");
        assertEquals(trlItems.length(), 0);

        // get revoked tokens from a time point a minute ago
        long from = new Date().getTime() - 60000;
        trl = webResource
                .path("/trl")
                .queryParam("from", Long.toString(from))
                .get(JSONObject.class);
        trlItems = trl.getJSONArray("tokens");
        assertEquals(trlItems.length(), 1);
        assertEquals(trl.getDouble("toDate"), (double) (new Date().getTime()), 1000);
        assertEquals(trl.getLong("fromDate"), from);
    }
}
