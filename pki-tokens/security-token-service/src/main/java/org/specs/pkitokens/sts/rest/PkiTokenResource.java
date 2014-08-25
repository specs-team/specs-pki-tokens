package org.specs.pkitokens.sts.rest;

import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.jpa.model.PkiToken;

import javax.persistence.EntityManager;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/pkitokens/{tokenId}")
public class PkiTokenResource {

    private String tokenId;

    public PkiTokenResource(@PathParam("tokenId") String tokenId) {
        this.tokenId = tokenId;
    }

    @DELETE
    public Response revokeToken() {
        EntityManager em = EMF.createEntityManager();
        try {
            PkiToken pkiToken = em.find(PkiToken.class, tokenId);
            if (tokenId == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            if (pkiToken.isRevoked()) {
                throw new WebApplicationException(Response.Status.NOT_MODIFIED);
            }

            em.getTransaction().begin();
            pkiToken.setRevoked(true);
            pkiToken.setRevocationDate(new Date());
            em.getTransaction().commit();
        }
        finally {
            EMF.closeEntityManager(em);
        }

        return Response.status(Response.Status.OK).build();
    }
}
