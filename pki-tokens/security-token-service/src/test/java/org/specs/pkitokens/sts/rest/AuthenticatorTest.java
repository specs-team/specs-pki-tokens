package org.specs.pkitokens.sts.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.specs.pkitokens.sts.Utils;
import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.utils.Conf;
import org.specs.specsdb.model.Sla;
import org.specs.specsdb.model.User;

import javax.persistence.EntityManager;
import java.security.Security;

import static org.junit.Assert.assertEquals;

@Ignore
public class AuthenticatorTest extends JerseyTest {

    public AuthenticatorTest() throws Exception {
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
    public void testDetectionByIP() throws Exception {
        WebResource webResource = resource();

        JSONObject reqData = new JSONObject();
        reqData.put("password", "invalidpassword");
        reqData.put("slaId", "1");
        ClientResponse response;

        for (int i = 1; i <= 10; i++) {
            reqData.put("username", "test.user." + i);
            response = webResource.path("/pkitokens").post(ClientResponse.class, reqData);
            assertEquals(response.getStatus(), 403);
        }

        // next attempt should be blocked
        reqData.put("username", "test.user.last");
        response = webResource.path("/pkitokens").post(ClientResponse.class, reqData);
        assertEquals(response.getStatus(), 429);
    }

    @Test
    public void testDetectionByUsername() throws Exception {
        WebResource webResource = resource();

        JSONObject reqData = new JSONObject();
        reqData.put("username", "test.user");
        reqData.put("password", "invalidpassword");
        reqData.put("slaId", "1");
        ClientResponse response;

        for (int i = 1; i <= 5; i++) {
            response = webResource.path("/pkitokens").post(ClientResponse.class, reqData);
            assertEquals(response.getStatus(), 401);
        }

        // next attempt should be blocked
        response = webResource.path("/pkitokens").post(ClientResponse.class, reqData);
        assertEquals(response.getStatus(), 403);
        String msg = response.getEntity(String.class);
        System.out.println(msg);

        // account is locked now, authentication attempt should be blocked
        response = webResource.path("/pkitokens").post(ClientResponse.class, reqData);
        assertEquals(response.getStatus(), 403);
    }
}
