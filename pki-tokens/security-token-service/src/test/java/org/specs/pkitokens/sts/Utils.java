package org.specs.pkitokens.sts;

import org.apache.log4j.Logger;
import org.specs.specsdb.model.User;
import org.specs.specsdb.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLNonTransientConnectionException;

public class Utils {
    private static Logger log = Logger.getLogger(Utils.class);

    public static void dropTestDatabase() throws Exception {
        log.debug("Dropping Derby test database...");
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:derby:memory:stsDB;drop=true");
            conn.close();
        }
        catch (SQLNonTransientConnectionException e) {
            // nothing wrong
            log.info("Test database has been dropped successfully.");
        }
    }

    public static User createTestUser() throws Exception {
        User user = new User();
        user.setUserId("f3191fb0-2bc1-11e4-8c21-0800200c9a66");
        user.setUsername("test.user");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test.user@specs.org");

        String password = "somepassword";
        user.setPassword(PasswordHasher.hashPassword(password));

        return user;
    }
}
