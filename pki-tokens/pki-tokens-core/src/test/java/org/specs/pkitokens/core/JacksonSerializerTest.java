package org.specs.pkitokens.core;

import org.junit.Test;
import org.specs.pkitokens.core.claims.Claim;
import org.specs.pkitokens.core.claims.UserClaim;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JacksonSerializerTest {

    @Test
    public void testPolymorphicDeserialization() throws IOException {
        UserClaim userClaim = new UserClaim();
        userClaim.setUserId("123");
        userClaim.setUsername("testuser");

        String json = JacksonSerializer.writeValueAsString(userClaim);
        System.out.println(json);

        Claim claim = JacksonSerializer.readValue(json, Claim.class);
        assertEquals(claim.getClass(), UserClaim.class);
        UserClaim userClaim1 = (UserClaim)claim;
        assertEquals(userClaim1.getUserId(), userClaim.getUserId());
        assertEquals(userClaim1.getUsername(), userClaim.getUsername());
    }

    @Test
    public void testSerialization() throws IOException {
        Token token = Utils.createToken();
        String json = JacksonSerializer.writeValueAsString(token);
        Token token1 = JacksonSerializer.readValue(json, Token.class);
        String json1 = JacksonSerializer.writeValueAsString(token1);
        assertEquals(json, json1);
    }
}