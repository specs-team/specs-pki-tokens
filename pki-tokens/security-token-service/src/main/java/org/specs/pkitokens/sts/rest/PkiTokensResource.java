package org.specs.pkitokens.sts.rest;

import org.codehaus.jettison.json.JSONObject;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.TokenSigner;
import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.jpa.model.PkiToken;
import org.specs.pkitokens.sts.utils.Conf;
import org.specs.pkitokens.sts.utils.TokenFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pkitokens")
public class PkiTokensResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String createToken(JSONObject data) throws Exception {
        TokenSigner tokenSigner = new TokenSigner(
                Conf.getSigningCertificateFile(),
                Conf.getSigningPrivateKeyFile(),
                Conf.getSigningPrivateKeyPass()
        );

        String username;
        String password;
        int slaId;
        try {
            username = data.getString("username");
            password = data.getString("password");
            slaId = data.getInt("slaId");
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Token token = TokenFactory.createToken(username, password, slaId);

        String encodedToken = token.sign(tokenSigner);

        EntityManager em = EMF.createEntityManager();
        try {
            PkiToken pkiToken = new PkiToken();
            pkiToken.setTokenId(token.getMetadata().getTokenId());
            pkiToken.setExpiryDate(token.getMetadata().getExpiryDate());

            em.getTransaction().begin();
            em.persist(pkiToken);
            em.getTransaction().commit();
        }
        finally {
            EMF.closeEntityManager(em);
        }

        return encodedToken;
    }
}
