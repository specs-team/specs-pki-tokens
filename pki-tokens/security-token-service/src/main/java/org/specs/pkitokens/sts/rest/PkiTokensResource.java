package org.specs.pkitokens.sts.rest;

import org.codehaus.jettison.json.JSONObject;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.TokenSigner;
import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.jpa.model.PkiToken;
import org.specs.pkitokens.sts.utils.Authenticator;
import org.specs.pkitokens.sts.utils.Conf;
import org.specs.pkitokens.sts.utils.TokenFactory;
import org.specs.specsdb.model.User;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pkitokens")
public class PkiTokensResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createToken(JSONObject data, @Context HttpServletRequest request) throws Exception {
        int slaId;
        String username, password, unlockCode;
        try {
            username = data.getString("username");
            password = data.getString("password");
            unlockCode = data.has("unlockCode") ? data.getString("unlockCode") : null;
            slaId = data.getInt("slaId");
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Authenticator authenticator = new Authenticator();
        User user = null;
        try {
            user = authenticator.authenticate(username, password, unlockCode, request);
        }
        catch (Authenticator.InvalidCredentialsException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
        catch (Authenticator.TooManyAttemptsException e) {
            return Response.status(429) // Too Many Requests response code
                    .entity(e.getMessage()).build();
        }
        catch (Authenticator.AccountLockedException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }

        Token token = TokenFactory.createToken(user, slaId);

        TokenSigner tokenSigner = new TokenSigner(
                Conf.getSigningCertificateFile(),
                Conf.getSigningPrivateKeyFile(),
                Conf.getSigningPrivateKeyPass()
        );

        String encodedToken = token.sign(tokenSigner);

        EntityManager em = EMF.createEntityManager();
        try {
            PkiToken pkiToken = new PkiToken();
            pkiToken.setTokenId(token.getHeader().getTokenId());
            pkiToken.setExpiryDate(token.getHeader().getExpiryDate());

            em.getTransaction().begin();
            em.persist(pkiToken);
            em.getTransaction().commit();
        }
        finally {
            EMF.closeEntityManager(em);
        }

        return Response.ok().entity(encodedToken).build();
    }
}
