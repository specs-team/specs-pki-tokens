package org.specs.pkitokens.sts.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.specs.pkitokens.core.Token;
import org.specs.specsdb.utils.EMF;

public class TokenFactoryTest {

    @Before
    public void setUp() throws Exception {
        EMF.init("SpecsdbTestPersistenceUnit");
    }

    @After
    public void tearDown() throws Exception {
        EMF.close();
    }

    @Test
    public void testCreateToken() throws Exception {
        Token token = TokenFactory.createToken("testuser", "password", 1);
        // TODO: check token
    }
}