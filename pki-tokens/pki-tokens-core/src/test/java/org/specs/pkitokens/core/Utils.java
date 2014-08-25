package org.specs.pkitokens.core;

import org.specs.pkitokens.core.claims.AuthzClaim;
import org.specs.pkitokens.core.claims.SLAClaim;
import org.specs.pkitokens.core.claims.UserClaim;

public class Utils {

    public static Token createToken() {
        Token token = new Token();

        UserClaim userClaim = new UserClaim();
        userClaim.setUserId("d3c23310-18be-11e4-8c21-0800200c9a66");
        userClaim.setUsername("test.user");
        userClaim.setFirstname("Test");
        userClaim.setLastname("User");
        userClaim.setEmail("test.user@specs.org");
        userClaim.getRoles().add("SPECS_USER");
        token.getClaimsCollection().addClaim(userClaim);

        SLAClaim slaClaim = new SLAClaim();
        slaClaim.setSlaId("2683");
        token.getClaimsCollection().addClaim(slaClaim);

        AuthzClaim authzClaim = new AuthzClaim();
        token.getClaimsCollection().addClaim(authzClaim);
        AuthzClaim.SpecsService specsService1 = new AuthzClaim.SpecsService();
        specsService1.setId("c9a177a0-18bf-11e4-8c21-0800200c9a66");
        specsService1.setUri("https://someserver/specsservice1");
        authzClaim.addService(specsService1);
        AuthzClaim.SpecsService specsService2 = new AuthzClaim.SpecsService();
        specsService2.setId("1e807500-18c0-11e4-8c21-0800200c9a66");
        specsService2.setUri("https://someserver/specsservice2");
        authzClaim.addService(specsService2);

        return token;
    }
}
