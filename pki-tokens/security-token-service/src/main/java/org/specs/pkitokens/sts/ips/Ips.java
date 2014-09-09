package org.specs.pkitokens.sts.ips;

import org.apache.log4j.Logger;
import org.specs.pkitokens.sts.jpa.EMF;
import org.specs.pkitokens.sts.jpa.model.AuthnAttempt;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Ips {
    private static final int ATTEMPTS_THRESHOLD = 5;
    private static final int TIME_THRESHOLD = 60;

    protected static Logger log = Logger.getLogger(Ips.class);
    private Map<String, Date> ipLocks = new HashMap<String, Date>();
    private Map<String, Date> usernameLocks = new HashMap<String, Date>();

    public void storeAuthnAttempt(String username, String ipAddress, Date timestamp, boolean success) {
        EntityManager em = EMF.createEntityManager();

        try {
            AuthnAttempt authnAttempt = new AuthnAttempt();
            authnAttempt.setUsername(username);
            authnAttempt.setIpAddress(ipAddress);
            authnAttempt.setTimestamp(timestamp);
            authnAttempt.setSuccess(success);
            em.getTransaction().begin();
            em.persist(authnAttempt);
            em.getTransaction().commit();
        }
        finally {
            EMF.closeEntityManager(em);
        }

        if (!success) {
            checkAuthnAttemptsByUsername(username);
            checkAuthnAttemptsByIp(ipAddress);
        }
    }

    private void checkAuthnAttemptsByUsername(String username) {
        EntityManager em = EMF.createEntityManager();
        try {
            Date now = new Date();
            Date fromDate = new Date(now.getTime() - 60 * 1000);
            TypedQuery<Long> query = em.createNamedQuery("AuthnAttempt.countFailedByUsername", Long.class);
            query.setParameter("username", username);
            query.setParameter("timestamp", fromDate);
            long num = query.getSingleResult();

            if (num >= 3) {
                Date expiryDate = new Date(now.getTime() + 60 * 1000);
                usernameLocks.put(username, expiryDate);
            }
        }
        finally {
            EMF.closeEntityManager(em);
        }
    }

    private void checkAuthnAttemptsByIp(String ipAddress) {
        EntityManager em = EMF.createEntityManager();
        try {
            Date now = new Date();
            Date fromDate = new Date(now.getTime() - 300 * 1000);
            TypedQuery<Long> query1 = em.createNamedQuery("AuthnAttempt.countFailedByIp", Long.class);
            query1.setParameter("ipAddress", ipAddress);
            query1.setParameter("timestamp", fromDate);
            long numOfFailed = query1.getSingleResult();

            TypedQuery<Long> query2 = em.createNamedQuery("AuthnAttempt.countSucceededByIp", Long.class);
            query2.setParameter("ipAddress", ipAddress);
            query2.setParameter("timestamp", fromDate);
            long numOfSucceeded = query2.getSingleResult();

            if (numOfFailed >= 5 &&
                    (numOfSucceeded == 0 || (double) numOfFailed / numOfSucceeded > 0.5)) {
                Date expiryDate = new Date(now.getTime() + 60 * 1000);
                ipLocks.put(ipAddress, expiryDate);
            }
        }
        finally {
            EMF.closeEntityManager(em);
        }
    }

    public boolean isUsernameThrottled(String username) {
        Date now = new Date();
        if (usernameLocks.containsKey(username)) {
            Date expiryDate = usernameLocks.get(username);
            if (now.before(expiryDate)) {
                return true;
            }
            else {
                usernameLocks.remove(username);
                return false;
            }
        }
        else {
            return false;
        }
    }

    public boolean isIpThrottled(String ipAddress) {
        Date now = new Date();
        if (ipLocks.containsKey(ipAddress)) {
            Date expiryDate = ipLocks.get(ipAddress);
            if (now.before(expiryDate)) {
                return true;
            }
            else {
                ipLocks.remove(ipAddress);
                return false;
            }
        }
        else {
            return false;
        }
    }
}
