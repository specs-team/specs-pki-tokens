package org.specs.pkitokens.sts.rest;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.TokenSigner;
import org.specs.pkitokens.sts.Utils;
import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.utils.Conf;

import java.security.Security;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PkiTokensCreateTest extends JerseyTest {

    public PkiTokensCreateTest() throws Exception {
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
        Utils.dropTestDatabase();
    }

    @Test
    public void testCreateToken() throws Exception {
        WebResource webResource = resource();

        JSONObject reqData = new JSONObject();
        reqData.put("username", "testuser");
        reqData.put("password", "password");
        reqData.put("slaId", "1");
        String encodedToken = webResource.path("/pkitokens").post(String.class, reqData);

        // decode the token
        TokenSigner tokenSigner = new TokenSigner(Conf.getSigningCertificateFile());
        Token token = Token.decode(encodedToken, tokenSigner);
        assertEquals(token.getHeader().getTokenId().length(), 36);
        assertTrue(token.getHeader().getExpiryDate().after(new Date()));
    }
}
