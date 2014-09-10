package org.specs.pkitokens.sts.utils;

import org.junit.Before;
import org.junit.Test;
import org.specs.pkitokens.sts.Utils;
import org.specs.specsdb.model.User;

import java.util.Date;
import java.util.UUID;

public class MailServiceTest {
    private User user;

    @Before
    public void setUp() throws Exception {
        Conf.load("src/test/resources/test-config.xml");
        user = Utils.createTestUser();
        user.setIsLocked(true);
        user.setLockDate(new Date());
        user.setUnlockCode(UUID.randomUUID().toString());
    }

    @Test
    public void testSendAccountBlockedNotification() throws Exception {
        MailService.sendAccountBlockedNotification(user);
    }
}