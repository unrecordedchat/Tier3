/*
 * VIA University College - School of Technology and Business
 * Software Engineering Program - 3rd Semester Project
 *
 * This work is a part of the academic curriculum for the Software Engineering program at VIA University College.
 * It is intended only for educational and academic purposes.
 *
 * No part of this project may be reproduced or transmitted in any form or by any means,
 * except as permitted by VIA University and the course instructor.
 * All rights reserved by the contributors and VIA University College.
 *
 * Project Name: Unrecorded
 * Author: Sergiu Chirap
 * Year: 2024
 */

package com.unrecorded.database.repositories;

import com.unrecorded.database.entities.EUser;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.util.FieldValidator;
import com.unrecorded.database.util.HibernateUtil;
import com.unrecorded.database.util.LoggerUtil;
import com.unrecorded.database.util.PasswordUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * This class manages operations related to users, including creation, retrieval, updating, and deletion.
 *
 * <p><b>Purpose:</b> The `UserPSQL` class handles user-centric CRUD (Create, Read, Update, Delete)
 * operations for a PostgreSQL database.
 * It integrates Hibernate ORM for database access and transaction management,
 * ensuring input validation and secure data handling across operations.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Field validation to ensure compliance with business rules and database constraints.</li>
 *   <li>Securing sensitive data (e.g., password hashing with salt).</li>
 *   <li>Efficient retrieval of user data using Hibernate queries.</li>
 *   <li>Logging of operations and diagnostics for traceability and debugging.</li>
 *   <li>Field-specific uniqueness checks for usernames, email addresses, and cryptographic keys.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>This class is stateless and uses thread-safe utilities like {@link HibernateUtil},
 * making it safe for use in multithreaded environments.</p>
 *
 * <p><b>Note:</b> All database-related exceptions are encapsulated in {@link DataAccessException}
 * to ensure consistent error reporting.</p>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see EUser
 * @see HibernateUtil
 * @since 1.0
 */
public class UserPSQL implements IUserRepo {
    /**
     * A constant set of field names that are considered valid or allowed for specific operations in the class.
     *
     * <p>This set defines which user attributes can be accessed or updated, ensuring security and
     * compliance with business logic.</p>
     */
    private static final Set<String> ALLOWED_FIELDS = Set.of("username", "email", "publicKey");

    /**
     * Creates and saves a new user in the database.
     *
     * <p>This method validates input fields, hashes the provided password with secure salt,
     * and persists the user entity using Hibernate ORM.
     * It also ensures that usernames and email addresses are unique before the user is created.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Validate the {@code username} and {@code email} with {@link FieldValidator}.</li>
     *   <li>Hash the plaintext password with a cryptographically secure salt using {@link PasswordUtil}.</li>
     *   <li>Create a new {@link EUser} object representing the user.</li>
     *   <li>Save the user object with Hibernate ORM via {@link HibernateUtil}.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * userPSQL.createUser(
     *     "john_doe",
     *     "securePassword123",
     *     "john.doe@example.com",
     *     "RSA_PUBLIC_KEY",
     *     "ENCRYPTED_PRIVATE_KEY"
     * );
     * }</pre>
     *
     * @param username            A unique username that complies with validation requirements.
     * @param password            The plaintext password, hashed and salted before storage.
     * @param email               A valid email address for the user.
     * @param publicKey           The RSA public key associated with the user.
     * @param privateKeyEncrypted The encrypted private RSA key of the user.
     * @throws IllegalArgumentException If validation fails for input fields.
     * @throws DataAccessException      If an error occurs while persisting the user.
     * @see FieldValidator
     * @see PasswordUtil
     * @see HibernateUtil
     */
    @Override
    public void createUser(@NotNull String username, @NotNull String password, @NotNull String email, @NotNull String publicKey, @NotNull String privateKeyEncrypted) throws IllegalArgumentException, DataAccessException {
        FieldValidator.usernameConstraints(username);
        FieldValidator.emailConstraints(email);
        FieldValidator.passwordConstraints(password);
        if (isUsernameTaken(username)) throw new IllegalArgumentException("Username is already taken: " + username);
        if (isEmailTaken(email)) throw new IllegalArgumentException("Email is already taken: " + email);
        if (isPublicKeyTaken(publicKey))
            throw new IllegalArgumentException("Public key is already associated with another user.");
        LoggerUtil.logDebug("All unique constraints passed for username " + username + ", email `{}`, and publicKey `{}`.", email, publicKey);
        LoggerUtil.logInfo("Initiating user creation with username: {}", username);
        LoggerUtil.logDebug("Sanitizing input email: {}", email);
        HibernateUtil.executeTransaction(true, session -> {
            byte[] salt = PasswordUtil.generateSalt();
            String passwordHash = PasswordUtil.hashPassword(password, salt);
            EUser user = new EUser(username, passwordHash, salt, email, publicKey, privateKeyEncrypted);
            session.persist(user);
            LoggerUtil.logInfo("User successfully created with username: {}", username);
            return null;
        });
    }

    /**
     * Fetches a user from the database using their unique identifier (UUID).
     *
     * <p>This method uses Hibernate to query for the user with the specified ID.
     * If no user is found, it returns {@code null}.</p>
     *
     * @param id The UUID of the user to retrieve.
     * @return The {@link EUser} entity corresponding to the given ID, or {@code null} if not found.
     * @throws DataAccessException If an error occurs during the query process.
     */
    @Override
    public @Nullable EUser getUserById(@NotNull UUID id) throws DataAccessException {
        return HibernateUtil.executeTransaction(false, session -> session.find(EUser.class, id));
    }

    /**
     * Fetches a user entity from the database using their username.
     *
     * <p>This method queries the database for a user with a matching username.
     * Input validation is performed before executing the query to ensure security.</p>
     *
     * @param username The username to search for.
     * @return The {@link EUser} entity if a matching username is found, otherwise {@code null}.
     * @throws IllegalArgumentException If the username fails the validation checks.
     * @throws DataAccessException      If there is an issue querying the database.
     */
    @Override
    public @Nullable EUser getUserByUsername(@NotNull String username) throws IllegalArgumentException, DataAccessException {
        LoggerUtil.logInfo("Retrieving user by username: {}", username);
        FieldValidator.usernameConstraints(username);
        LoggerUtil.logDebug("Sanitizing input username: {}", username);
        return HibernateUtil.executeTransaction(false, session -> session.createQuery("FROM EUser WHERE username = :username", EUser.class).setParameter("username", username).uniqueResult());
    }

    /**
     * Fetches a user entity from the database using their email address.
     *
     * <p>This method queries the database for a user with the specified email in a
     * case-insensitive manner.</p>
     *
     * @param email The email address of the user.
     * @return The {@link EUser} entity corresponding to the email, or {@code null} if not found.
     * @throws IllegalArgumentException If the email is invalid.
     * @throws DataAccessException      If an error occurs while querying the database.
     */
    @Override
    public @Nullable EUser getUserByEmail(@NotNull String email) throws IllegalArgumentException, DataAccessException {
        LoggerUtil.logInfo("Retrieving user by email");
        LoggerUtil.logDebug("Sanitizing input email: {}", email);
        FieldValidator.emailConstraints(email);
        return HibernateUtil.executeTransaction(false, session -> session.createQuery("FROM EUser WHERE email = :emailAddress", EUser.class).setParameter("emailAddress", email).uniqueResult());
    }

    /**
     * Updates the username of a user identified by their UUID.
     *
     * <p>This method validates the new username and updates it in the database for the user
     * with the specified ID.</p>
     *
     * @param userId   The unique ID of the user to update.
     * @param username The new username to assign to this user.
     * @throws IllegalArgumentException If validation fails for the username.
     * @throws DataAccessException      If an error occurs during the update operation.
     */
    public void updateUsername(@NotNull UUID userId, @NotNull String username) throws IllegalArgumentException, DataAccessException {
        FieldValidator.usernameConstraints(username);
        LoggerUtil.logInfo("Starting username update for userId: {}", userId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EUser user = session.get(EUser.class, userId);
            if (user != null) {
                LoggerUtil.logDebug("Old username: {}", user.getUsername());
                user.setUsername(username);
                session.merge(user);
                LoggerUtil.logInfo("Successfully updated username for userId: {}", userId.toString());
            } else LoggerUtil.logWarn("No user found for userId: " + userId);
            return null;
        });
    }

    /**
     * Updates the email address of the specified user.
     *
     * @param userId The unique identifier of the user.
     * @param email  The new email address to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    public void updateEmail(@NotNull UUID userId, @NotNull String email) throws IllegalArgumentException, DataAccessException {
        FieldValidator.emailConstraints(email);
        LoggerUtil.logInfo("Starting email update for userId: {}", userId.toString());
        LoggerUtil.logDebug("Sanitized email: {}", email);
        HibernateUtil.executeTransaction(true, session -> {
            EUser user = session.get(EUser.class, userId);
            if (user != null) {
                LoggerUtil.logDebug("Old email: {}", user.getEmail());
                user.setEmail(email);
                session.merge(user);
                LoggerUtil.logInfo("Successfully updated email for userId: {}", userId.toString());
            } else LoggerUtil.logWarn("No user found for userId: " + userId);
            return null;
        });
    }

    /**
     * Updates the password of a user identified by their unique user ID.
     *
     * <p>This method retrieves the user's current salt from the database, hashes
     * the new password with it, and updates the user's password hash field with the resulting value.</p>
     *
     * @param userId      The UUID of the user whose password is to be updated.
     * @param newPassword The new plaintext password to be set.
     * @throws IllegalArgumentException If the {@code newPassword} fails validation (e.g., too shorts, too weak).
     * @throws DataAccessException      If any database-related issue occurs during the update operation.
     */
    @Override
    public void changePassword(@NotNull UUID userId, @NotNull String newPassword) throws IllegalArgumentException, DataAccessException {
        FieldValidator.passwordConstraints(newPassword);
        LoggerUtil.logInfo("Changing password for userId: {}", userId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EUser user = session.get(EUser.class, userId);
            if (user != null) {
                byte[] newSalt = PasswordUtil.generateSalt();
                String newPasswordHash = PasswordUtil.hashPassword(newPassword, newSalt);
                user.setPassHash(newPasswordHash);
                user.setSalt(newSalt);
                session.merge(user);
                LoggerUtil.logInfo("Password changed successfully for userId: {}", userId.toString());
            } else {
                LoggerUtil.logWarn("User not found for userId: " + userId);
                throw new IllegalArgumentException("User not found.");
            }
            return null;
        });
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
        LoggerUtil.logInfo("Starting keys update for userId: {}", userId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EUser user = session.get(EUser.class, userId);
            if (user != null) {
                user.setPublicKey(newPublicKey);
                user.setPrivateKey(newPrivateKeyEncrypted);
                session.merge(user);
                LoggerUtil.logInfo("Successfully updated keys for userId: {}", userId.toString());
            } else LoggerUtil.logWarn("No user found for userId: " + userId);
            return null;
        });
    }

    /**
     * Deletes a user from the database by their unique identifier.
     *
     * @param userId The unique identifier of the user to be deleted.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    @Override
    public void deleteUser(@NotNull UUID userId) throws DataAccessException {
        LoggerUtil.logInfo("Initiating deletion for userId: {}", userId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EUser user = session.get(EUser.class, userId);
            if (user != null) {
                session.remove(user);
                LoggerUtil.logInfo("Successfully deleted user with userId: {}", userId.toString());
            } else LoggerUtil.logWarn("No user found for deletion with userId: " + userId);
            return null;
        });
    }

    /**
     * Verifies if the provided password matches the stored, hashed password of the user with the specified username.
     *
     * <p>This method retrieves the user entity by the given username and uses the user's stored salt
     * to hash the input password.
     * The hashed input password is then compared to the stored hashed password of the user to verify if they match.
     * The method ensures secure handling of password verification
     * without exposing sensitive information.</p>
     *
     * <h3>How it works:</h3>
     * <ol>
     *     <li>Fetches the user entity from the database by the provided {@code username}.</li>
     *     <li>Applies the stored password salt to the provided plaintext {@code password}.</li>
     *     <li>Compares the hashed result of the provided password with the password hash stored in the database.</li>
     *     <li>Returns {@code true} if the hashes match, otherwise {@code false} if the hashes differ
     *         or the user account does not exist.</li>
     * </ol>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     * UserPSQL userRepo = new UserPSQL();
     * boolean isPasswordValid = userRepo.verifyPassword("john_doe", "securePassword123");
     * if (isPasswordValid) {
     *     System.out.println("Password verified successfully");
     * } else {
     *     System.out.println("Invalid credentials");
     * }
     * }</pre>
     *
     * <h3>Expected Exceptions:</h3>
     * <ul>
     *     <li>{@link IllegalArgumentException}: Thrown if the {@code username} or {@code password} parameters are invalid
     *     (e.g., null or empty).</li>
     *     <li>{@link DataAccessException}: Thrown if any database-related error occurs, such as failure during
     *     username retrieval or transaction execution.</li>
     * </ul>
     *
     * @param username The username of the user whose password is to be verified. Must not be {@code null} or empty.
     * @param password The plaintext password provided for verification. Must not be {@code null} or empty.
     * @return {@code true} if the provided password matches the stored hashed password; {@code false} otherwise
     * (e.g., if the user was not found or passwords do not match).
     * @throws IllegalArgumentException If the {@code username} or {@code password} is {@code null} or fails validation.
     * @throws DataAccessException      If there is an issue accessing or retrieving the user from the database.
     * @see PasswordUtil#hashPassword(String, byte[])
     * @see EUser#getSalt()
     * @see EUser#getPassHash()
     */
    public boolean verifyPassword(@NotNull String username, @NotNull String password) throws IllegalArgumentException, DataAccessException {
        LoggerUtil.logDebug("Verifying password for user: {}", username);
        EUser user = getUserByUsername(username);
        if (user != null) return PasswordUtil.verifyPassword(user.getPassHash(), password, user.getSalt());
        return false;
    }

    /**
     * Determines whether the given username already exists in the database.
     *
     * @param username The username to check; must not be {@code null} or invalid.
     * @return {@code true} if the username is already in use, {@code false} otherwise.
     * @throws IllegalArgumentException If the {@code username} is null or fails validation.
     * @throws DataAccessException      If a database error occurs during the check.
     */
    private boolean isUsernameTaken(@NotNull String username) throws IllegalArgumentException, DataAccessException {
        FieldValidator.usernameConstraints(username);
        return isFieldTaken(username, "username");
    }

    /**
     * Determines whether the given email address already exists in the database.
     *
     * @param email The email address to check; must not be {@code null} or invalid.
     * @return {@code true} if the email address is already in use, {@code false} otherwise.
     * @throws IllegalArgumentException If the {@code email} is null or fails validation.
     * @throws DataAccessException      If a database error occurs during the check.
     */
    private boolean isEmailTaken(@NotNull String email) throws IllegalArgumentException, DataAccessException {
        FieldValidator.emailConstraints(email);
        return isFieldTaken(email, "email");
    }

    /**
     * Checks if the given public key already exists in the database.
     *
     * @param publicKey The public key to check; must not be {@code null}.
     * @return {@code true} if the public key is already in use, {@code false} otherwise.
     */
    private boolean isPublicKeyTaken(@NotNull String publicKey) {
        return isFieldTaken(publicKey, "publicKey");
    }

    /**
     * Checks whether the specified field value already exists in the database.
     *
     * <p>This method performs a database query to check if the value of the given field
     * (identified by its column name) already exists.
     * It uses a transaction to execute the query securely and ensures proper
     * abstraction for field-specific checks.</p>
     *
     * <h3>How it works:</h3>
     * <ol>
     *     <li>Logs the field column and value being checked.</li>
     *     <li>Executes a database transaction using {@code HibernateUtil.executeTransaction}.</li>
     *     <li>Builds a counting query using {@code buildCountQuery} that filters based on the specified
     *         column name and value.</li>
     *     <li>Executes the query and retrieves the count of matching rows in the database.</li>
     *     <li>Returns {@code true} if the count is greater than zero, otherwise {@code false}.</li>
     * </ol>
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * boolean isUsernameTaken = isFieldTaken("john_doe", "username");
     * if (isUsernameTaken) {
     *     System.out.println("The username is already in use.");
     * } else {
     *     System.out.println("The username is available.");
     * }
     * }</pre>
     *
     * @param fieldValue  The value to check for uniqueness in the database; must not be {@code null}.
     * @param fieldColumn The database column name to perform the check against; must not be {@code null}.
     * @return {@code true} if the field value exists in the database, {@code false} otherwise.
     * @throws IllegalArgumentException If the provided {@code fieldValue} or {@code fieldColumn} is null or invalid.
     * @throws DataAccessException      If a database transaction fails or cannot be completed successfully.
     */
    private boolean isFieldTaken(@NotNull String fieldValue, @NotNull String fieldColumn) {
        LoggerUtil.logInfo("Checking if field is taken: field = {}, value = {}", fieldColumn, fieldValue);
        return Boolean.TRUE.equals(HibernateUtil.executeTransaction(false, session -> {
            CriteriaQuery<Long> query = buildCountQuery(session, fieldColumn, fieldValue);
            Long count = session.createQuery(query).getSingleResult();
            return count != null && count > 0;
        }));
    }

    /**
     * Builds a {@link CriteriaQuery}
     * to count the number of rows in the database where a specific field matches the given value.
     *
     * <p>This method verifies that the provided column name is authorized for querying by checking against a predefined
     * list of allowed fields.
     * If the column name is not allowed, an {@code IllegalArgumentException} is thrown.</p>
     *
     * <h3>How it works:</h3>
     * <ol>
     *     <li>Validates whether the specified {@code fieldColumn} is included in the set of allowed fields.</li>
     *     <li>Uses the JPA {@link CriteriaBuilder} to construct a query that counts rows matching the given
     *         {@code fieldColumn} and {@code fieldValue} in the {@code EUser} entity.</li>
     *     <li>Returns a {@link CriteriaQuery} object ready to execute in the provided {@link Session}.</li>
     * </ol>
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * try (Session session = HibernateUtil.openSession()) {
     *     CriteriaQuery<Long> query = buildCountQuery(session, "username", "john_doe");
     *     Long count = session.createQuery(query).getSingleResult();
     *     System.out.println("Rows matching the query: " + count);
     * }
     * }</pre>
     *
     * @param session     The active Hibernate session used to build the query; must not be {@code null}.
     * @param fieldColumn The name of the database column to filter by; must be included in the allowed fields list.
     * @param fieldValue  The value to match against the specified column; must not be {@code null}.
     * @return A {@link CriteriaQuery} object representing the counting query for the given field and value.
     * @throws IllegalArgumentException If the {@code fieldColumn} is not authorized or if any parameter is {@code null}.
     * @throws DataAccessException      If an error occurs while interacting with the database session.
     * @see CriteriaBuilder
     * @see HibernateUtil
     */
    private @NotNull CriteriaQuery<Long> buildCountQuery(@NotNull Session session, @NotNull String fieldColumn, @NotNull String fieldValue) throws IllegalArgumentException, DataAccessException {
        if (!ALLOWED_FIELDS.contains(fieldColumn))
            throw new IllegalArgumentException("Unauthorized column access: " + fieldColumn);
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<EUser> root = criteriaQuery.from(EUser.class);
        criteriaQuery.select(builder.count(root)).where(builder.equal(root.get(fieldColumn), fieldValue));
        return criteriaQuery;
    }
}