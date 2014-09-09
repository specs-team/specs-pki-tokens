package org.specs.pkitokens.sts.utils;

import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.claims.SLAClaim;
import org.specs.pkitokens.core.claims.UserClaim;
import org.specs.specsdb.model.Role;
import org.specs.specsdb.model.Service;
import org.specs.specsdb.model.Sla;
import org.specs.specsdb.model.User;
import org.specs.specsdb.utils.EMF;

import javax.persistence.EntityManager;

public class TokenFactory {

    public static Token createToken(User user, int slaId) throws Exception {
        EntityManager em = EMF.createEntityManager();
        try {
            Sla sla = em.find(Sla.class, slaId);
            if (sla == null) {
                throw new Exception(String.format("Invalid SLA id: %d", slaId));
            }

            Token token = new Token();

            UserClaim userClaim = new UserClaim();
            token.getPayload().addClaim(userClaim);
            userClaim.setUserId(user.getUserId());
            userClaim.setUsername(user.getUsername());
            userClaim.setFirstname(user.getFirstName());
            userClaim.setLastname(user.getLastName());
            userClaim.setEmail(user.getEmail());

            for (Role role : user.getRoleList()) {
                userClaim.getRoles().add(role.getName());
            }

            SLAClaim slaClaim = new SLAClaim();
            token.getPayload().addClaim(slaClaim);
            slaClaim.setSlaId(String.valueOf(slaId));

            for (Service service : sla.getServiceList()) {
                SLAClaim.SpecsService claimSrv = new SLAClaim.SpecsService(service.getServiceId(), service.getUri());
                slaClaim.getServices().add(claimSrv);
            }

            return token;
        }
        finally {
            em.close();
        }
    }
}
