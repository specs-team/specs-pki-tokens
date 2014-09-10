package org.specs.pkitokens.sts.utils;

import org.specs.pkitokens.sts.ips.Ips;
import org.specs.pkitokens.sts.ips.IpsFactory;
import org.specs.specsdb.dao.UserDAO;
import org.specs.specsdb.model.User;
import org.specs.specsdb.utils.EMF;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

public class Authenticator {

    public User authenticate(String username, String password, String unlockCode, HttpServletRequest request) throws
            Exception {

        Ips ips = IpsFactory.getIps();
        String ipAddress = request.getRemoteAddr();
        Date timestamp = new Date();

        if (IpsFactory.getIps().isIpThrottled(ipAddress)) {
            throw new TooManyAttemptsException(String.format(
                    "Too many failed authentication attempts have been detected from the IP address %s. As a security" +
                            " measure, the IP is temporarily banned.", ipAddress
            ));
        }

        if (IpsFactory.getIps().isUsernameThrottled(username)) {
            throw new TooManyAttemptsException(String.format(
                    "Too many failed authentication attempts have been detected for the username %s. As a security " +
                            "measure, the username has been temporarily locked.", username
            ));
        }

        EntityManager em = EMF.createEntityManager();
        try {
            UserDAO userDAO = new UserDAO(em);
            User user = userDAO.findByUsername(username);
            if (user == null) { // invalid username
                ips.storeAuthnAttempt(username, ipAddress, timestamp, false);
                throw new InvalidCredentialsException("Invalid credentials.");
            }

            // authenticate user
            if (user.isLocked()) {
                boolean authenticated = userDAO.authenticate(user, password, unlockCode);
                if (authenticated) {
                    // user was successfully authenticated, we can remove the lock
                    em.getTransaction().begin();
                    unlockUser(user);
                    em.getTransaction().commit();

                    ips.storeAuthnAttempt(username, ipAddress, timestamp, true);

                    return user;
                }
                else {
                    em.getTransaction().begin();
                    user.setAuthnAttempts(user.getAuthnAttempts() + 1);
                    em.getTransaction().commit();

                    ips.storeAuthnAttempt(username, ipAddress, timestamp, false);

                    if (unlockCode != null) {
                        throw new InvalidCredentialsException("Invalid credentials.");
                    }
                    else {
                        String message = "Due to too many failed login attempts your account has been locked. Please " +
                                "provide the unlock code you have received by email to your registered email address.";
                        throw new AccountLockedException(message);
                    }
                }
            }
            else {
                boolean authenticated = userDAO.authenticate(user, password);
                if (authenticated) {
                    ips.storeAuthnAttempt(username, ipAddress, timestamp, true);
                    return user;
                }
                else {
                    // increase authn attempts counter
                    int authnAttempts = user.getAuthnAttempts() + 1;
                    em.getTransaction().begin();
                    user.setAuthnAttempts(authnAttempts);
                    em.getTransaction().commit();

                    ips.storeAuthnAttempt(username, ipAddress, timestamp, false);

                    if (authnAttempts > Conf.getIpsAccountLockoutAttempts()) {
                        // lock the user account
                        em.getTransaction().begin();
                        lockUser(user);
                        em.getTransaction().commit();

                        // notify the account owner
                        if (Conf.getIpsAccountBlockedNotifEnabled()) {
                            MailService.sendAccountBlockedNotification(user);
                        }

                        throw new AccountLockedException("You have exceeded the maximum number of attempts to authenticate with " +
                                "your credentials. For security reasons, your account has been locked. An unlock code has been " +
                                "sent to your registered email address. Please provide the unlock code together with " +
                                "your credentials.");
                    }
                    else {
                        throw new InvalidCredentialsException("Invalid credentials.");
                    }
                }
            }
        }
        finally {
            em.close();
        }
    }

    private void lockUser(User user) {
        user.setIsLocked(true);
        user.setLockDate(new Date());
        user.setUnlockCode(UUID.randomUUID().toString());
    }

    private void unlockUser(User user) {
        user.setIsLocked(false);
        user.setLockDate(null);
        user.setUnlockCode(null);
        user.setAuthnAttempts(0);
    }

    public static class InvalidCredentialsException extends Exception {
        public InvalidCredentialsException(String s) {
            super(s);
        }
    }

    public static class TooManyAttemptsException extends Exception {
        public TooManyAttemptsException(String s) {
            super(s);
        }
    }

    public static class AccountLockedException extends Exception {
        public AccountLockedException(String s) {
            super(s);
        }
    }
}
