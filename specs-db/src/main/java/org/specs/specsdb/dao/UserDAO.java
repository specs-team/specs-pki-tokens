package org.specs.specsdb.dao;

import org.specs.specsdb.model.User;
import org.specs.specsdb.utils.PasswordHasher;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class UserDAO {

    private EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    public User findByEmail(String email) {

        try {
            TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public User findByUsername(String username) {

        try {
            TypedQuery<User> query = em.createNamedQuery("User.findByUsername", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public User authenticate(String username, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        try {
            TypedQuery<User> query = em.createNamedQuery("User.findByUsername", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();

            if (PasswordHasher.checkPassword(password, user.getPassword())) {
                return user;
            }
            else {
                return null;
            }
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public boolean authenticate(User user, String password) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        if (user.isLocked()) {
            return false;
        }
        else {
            return PasswordHasher.checkPassword(password, user.getPassword());
        }
    }

    public boolean authenticate(User user, String password, String unlockCode) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        if (!user.isLocked()) {
            return false;
        }
        else {
            // check unlock code
            if (user.getUnlockCode() != null && !user.getUnlockCode().equals(unlockCode)) {
                return false;
            }

            // check password
            return PasswordHasher.checkPassword(password, user.getPassword());
        }
    }
}
