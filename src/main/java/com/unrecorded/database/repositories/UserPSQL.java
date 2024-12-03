package com.unrecorded.database.repositories;

import com.unrecorded.database.DBA;
import com.unrecorded.database.entities.EUser;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.exceptions.TypeOfDAE;
import com.unrecorded.database.util.PasswordUtil;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * This class manages operations related to users, including creation, retrieval, updating, and deletion.
 * <p>Uses HibernateORM to save in a PostgreSQL database.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see com.unrecorded.database.entities.EUser EUser
 * @see com.unrecorded.database.DBA DBA
 * @since PREVIEW
 */
public class UserPSQL implements IUserRepo {

    /**
     * Logger for recording events and debugging information within the UserPSQL class.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserPSQL.class);

    /**
     * Creates a new user in the database.
     *
     * @param username            The username for the new user.
     * @param password            The password for the new user.
     * @param emailAddress        The email address for the new user.
     * @param publicKey           The public key for the new user.
     * @param privateKeyEncrypted The encrypted private key for the new user.
     * @throws DataAccessException If there is an issue accessing the database during user creation.
     */
    public void createUser(@NotNull String username, @NotNull String password, @NotNull String emailAddress, @NotNull String publicKey, @NotNull String privateKeyEncrypted) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Creating user with email: {}", emailAddress);
        byte[] salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);
        EUser user = new EUser(username, passwordHash, salt, emailAddress, publicKey, privateKeyEncrypted);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                logger.info("User {} created successfully", username);
            } catch (ConstraintViolationException e) {
                if (transaction != null) transaction.rollback();
                logger.error("ConstraintViolationException while creating User.", e);
                StringBuilder msg = new StringBuilder("SQL-UNIQUE // Error while creating user!\n");
                if (isEmailTaken(emailAddress)) msg.append("- Email address already taken.\n");
                if (isUsernameTaken(username)) msg.append("- Username already taken.\n");
                if (isPublicKeyTaken(publicKey)) msg.append("- Public key already taken.\n");
                throw new DataAccessException(TypeOfDAE.INS, msg.toString(), e, false, false);
            } catch (PersistenceException e) {
                if (transaction != null) transaction.rollback();
                logger.error("PersistenceException while creating user", e);
                throw new DataAccessException(TypeOfDAE.GNL, "There was an unexpected persistence-related error while creating the user.", e, true, true);
            }
        }
    }

    /**
     * Retrieves a user from the database by their unique identifier.
     *
     * @param userId The unique identifier of the user to retrieve.
     * @return The EUser object corresponding to the specified userId, or null if no user is found.
     */
    public @Nullable EUser getUserById(@NotNull UUID userId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving user by ID: {}", userId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.find(EUser.class, userId);
        }
    }

    /**
     * Retrieves a user from the database using their username.
     *
     * @param username The username of the user to retrieve.
     * @return The EUser object corresponding to the specified username, or null if no user is found.
     */
    public @Nullable EUser getUserByUsername(@NotNull String username) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving user by username: {}", username);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.createQuery("FROM EUser WHERE username = :username", EUser.class).setParameter("username", username).uniqueResult();
        }
    }

    /**
     * Retrieves a user from the database using their email address.
     *
     * @param emailAddress The email address of the user to retrieve.
     * @return The EUser object corresponding to the specified email address, or null if no user is found
     */
    public @Nullable EUser getUserByEmail(String emailAddress) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving user by email: {}", emailAddress);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.createQuery("FROM EUser WHERE email = :emailAddress", EUser.class).setParameter("emailAddress", emailAddress).uniqueResult();
        }
    }

    /**
     * Updates the username of the specified user.
     *
     * @param userId   The unique identifier of the user.
     * @param username The new username to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    public void updateUsername(@NotNull UUID userId, @NotNull String username) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Updating username for userId: {}", userId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EUser user = session.get(EUser.class, userId);
                if (user != null) {
                    user.setUsername(username);
                    session.merge(user);
                    transaction.commit();
                    logger.info("Username updated successfully for userId: {}", userId);
                } else logUserNotFound(userId);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while updating username for userId: {}", userId, e);
                throw new DataAccessException(TypeOfDAE.UPD, "Error while updating username.", e, false, false);
            }
        }
    }

    /**
     * Updates the email address of the specified user.
     *
     * @param userId       The unique identifier of the user.
     * @param emailAddress The new email address to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    public void updateEmailAddress(@NotNull UUID userId, @NotNull String emailAddress) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Updating email address for userId: {}", userId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EUser user = session.get(EUser.class, userId);
                if (user != null) {
                    user.setEmail(emailAddress);
                    session.merge(user);
                    transaction.commit();
                    logger.info("Email address updated successfully for userId: {}", userId);
                } else logUserNotFound(userId);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while updating email address for userId: {}", userId, e);
                throw new DataAccessException(TypeOfDAE.UPD, "Error while updating email address.", e, false, false);
            }
        }
    }

    /**
     * Updates the public and encrypted private keys of the specified user.
     *
     * @param userId                 The unique identifier of the user.
     * @param newPublicKey           The new public key to be set.
     * @param newPrivateKeyEncrypted The new encrypted private key to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    public void updateKeys(@NotNull UUID userId, @NotNull String newPublicKey, @NotNull String newPrivateKeyEncrypted) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Updating keys for userId: {}", userId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EUser user = session.get(EUser.class, userId);
                if (user != null) {
                    user.setPublicKey(newPublicKey);
                    user.setPrivateKey(newPrivateKeyEncrypted);
                    session.merge(user);
                    transaction.commit();
                    logger.info("Public and private keys updated successfully for userId: {}", userId);
                } else logUserNotFound(userId);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while updating public and private keys for userId: {}", userId, e);
                throw new DataAccessException(TypeOfDAE.UPD, "Error while updating public and private keys.", e, false, false);
            }
        }
    }

    /**
     * Deletes a user from the database by their unique identifier.
     *
     * @param userId The unique identifier of the user to be deleted.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    public void deleteUser(@NotNull UUID userId) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Deleting user by ID: {}", userId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EUser user = session.get(EUser.class, userId);
                if (user != null) {
                    session.remove(user);
                    transaction.commit();
                    logger.info("User deleted successfully for userId: {}", userId);
                } else logUserNotFound(userId);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while deleting user for userId: {}", userId, e);
                throw new DataAccessException(TypeOfDAE.DEL, "Error while deleting user.", e, false, false);
            }
        }
    }

    /**
     * Verifies if the provided password matches the stored password of the user with the specified username.
     *
     * @param username The username of the user whose password needs to be verified
     * @param password The provided password to verify
     * @return True if the provided password matches the stored password, false otherwise
     */
    public boolean verifyPassword(@NotNull String username, @NotNull String password) {
        if (logger.isDebugEnabled()) logger.debug("Verifying password for user: {}", username);
        EUser user = getUserByUsername(username);
        if (user != null) {
            byte[] salt = user.getSalt();
            String hashToVerify = PasswordUtil.hashPassword(password, salt);
            return hashToVerify.equals(user.getPassHash());
        }
        return false;
    }

    /**
     * Checks if the given username is already taken in the database.
     *
     * @param username The username to be checked for existence.
     * @return True, if the username is already taken, false otherwise.
     */
    private boolean isUsernameTaken(@NotNull String username) {
        if (logger.isDebugEnabled()) logger.debug("Checking if username {} is taken", username);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(e) FROM EUser e WHERE e.username = :username", Long.class).setParameter("username", username).uniqueResult();
            return count != null && count > 0;
        }
    }

    /**
     * Checks if the provided email address is already taken in the database.
     *
     * @param emailAddress The email address to be checked for existence.
     * @return True, if the email address is already taken, false otherwise.
     */
    private boolean isEmailTaken(@NotNull String emailAddress) {
        if (logger.isDebugEnabled()) logger.debug("Checking if email address {} is taken", emailAddress);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(e) FROM EUser e WHERE e.email = :emailAddress", Long.class).setParameter("emailAddress", emailAddress).uniqueResult();
            return count != null && count > 0;
        }
    }

    /**
     * Checks if the provided public key is already present in the database.
     *
     * @param publicKey The public key to check for existence.
     * @return True, if the public key is already taken, false otherwise.
     */
    private boolean isPublicKeyTaken(@NotNull String publicKey) {
        if (logger.isDebugEnabled()) logger.debug("Checking if public key {} is taken", publicKey);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(e) FROM EUser e WHERE e.publicKey = :publicKey", Long.class).setParameter("publicKey", publicKey).uniqueResult();
            return count != null && count > 0;
        }
    }

    /**
     * Logs a warning message indicating that a user with the specified ID was not found.
     *
     * @param userId The unique identifier of the user that was not found.
     */
    private void logUserNotFound(UUID userId) {
        logger.warn("No user found with ID: {}", userId);
    }
}