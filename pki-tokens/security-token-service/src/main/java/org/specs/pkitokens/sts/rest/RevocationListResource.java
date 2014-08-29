package org.specs.pkitokens.sts.rest;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.jpa.model.PkiToken;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/trl")
public class RevocationListResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getTokenRevocationList(@QueryParam("from") Long from) throws Exception {
        Date fromDate = null;
        if (from != null) {
            fromDate = new Date(from);
        }

        Date toDate = new Date();

        JSONObject trl = new JSONObject();
        JSONArray trlTokensArr = new JSONArray();
        trl.put("tokens", trlTokensArr);
        trl.put("fromDate", (fromDate != null) ? fromDate.getTime() : null);
        trl.put("toDate", toDate.getTime());

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<PkiToken> query = null;
            if (fromDate == null) {
                query = em.createNamedQuery("PkiToken.findRevoked", PkiToken.class);
                query.setParameter("toDate", toDate);
            }
            else {
                query = em.createNamedQuery("PkiToken.findRevokedFrom", PkiToken.class);
                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
            }
            List<PkiToken> revokedTokens = query.getResultList();
            for (PkiToken pkiToken : revokedTokens) {
                JSONObject trlItem = new JSONObject();
                trlItem.put("id", pkiToken.getTokenId());
                trlItem.put("exp", pkiToken.getExpiryDate().getTime());
                trlTokensArr.put(trlItem);
            }
        }
        finally {
            EMF.closeEntityManager(em);
        }

        return trl;
    }
}
