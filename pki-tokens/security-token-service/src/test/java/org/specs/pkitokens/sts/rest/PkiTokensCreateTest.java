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
import org.specs.pkitokens.core.VerificationCertProvider;
import org.specs.pkitokens.core.VerificationCertProviderP12;
import org.specs.pkitokens.sts.Utils;
import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.utils.Conf;
import org.specs.specsdb.model.Sla;
import org.specs.specsdb.model.User;

import javax.persistence.EntityManager;
import java.security.Security;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PkiTokensCreateTest extends JerseyTest {
    private static final String SIGNING_KEYSTORE_FILE = "src/test/resources/signing-keystore.p12";
    private static final String SIGNING_KEYSTORE_PASS = "password";

    public PkiTokensCreateTest() throws Exception {
        super(new WebAppDescriptor.Builder("org.specs.pkitokens.sts.rest").build());
    }

    @Before
    public void setUp() throws Exception {
        EMF.init("StsTestPersistenceUnit");
        org.specs.specsdb.utils.EMF.init("SpecsdbTestPersistenceUnit");
        Conf.load("src/test/resources/test-config.xml");
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        // create test user
        EntityManager em = org.specs.specsdb.utils.EMF.createEntityManager();
        User user = Utils.createTestUser();

        // create SLA
        Sla sla = new Sla();
        sla.setSlaId(1);
        sla.setName("Demo SLA");
        sla.setUser(user);

        em.getTransaction().begin();
        em.persist(user);
        em.persist(sla);
        em.getTransaction().commit();
    }

    @After
    public void tearDown() throws Exception {
        EMF.close();
    }

    @Test
    public void testObtainToken() throws Exception {
        WebResource webResource = resource();

        JSONObject reqData = new JSONObject();
        reqData.put("username", "test.user");
        reqData.put("password", "somepassword");
        reqData.put("slaId", "1");
        String encodedToken = webResource.path("/pkitokens").post(String.class, reqData);

        // decode the token
        VerificationCertProvider verifCertProvider = new VerificationCertProviderP12(
                SIGNING_KEYSTORE_FILE,
                SIGNING_KEYSTORE_PASS
        );

        Token token = Token.decode(encodedToken, verifCertProvider);
        assertNotNull(token.getTokenId().length());
        assertTrue(token.getHeader().getExpiryDate().after(new Date()));
    }
}
