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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Interface defining user-related database operations.
 *
 * <p>The {@code IUserRepo} interface specifies methods for interacting with the
 * {@link EUser} entity, including CRUD (Create, Read, Update, Delete) functionality
 * and additional user-specific operations.
 * Implementations should ensure appropriate validation, error handling, and secure data management.</p>
 *
 * <p>All database-related exceptions should be wrapped in {@link DataAccessException}
 * for uniform error reporting.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>CRUD operations for managing user data in the database.</li>
 *   <li>Validation of inputs such as usernames, email addresses, and encryption keys.</li>
 *   <li>Support for secure password verification.</li>
 *   <li>Thread-safe operation for use in multithreaded environments.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @since PREVIEW
 */
public interface IUserRepo {

    /**
     * Creates a new user in the database.
     *
     * <p>This method validates input fields, hashes the provided password with secure salt,
     * and persists the {@link EUser} entity in the database.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Validate the {@code username} and {@code email} fields.</li>
     *   <li>Hash the plaintext password with a cryptographically secure salt.</li>
     *   <li>Create and persist the {@link EUser} entity.</li>
     * </ol>
     *
     * @param username The username for the new user. Must be unique and conform to validation rules.
     * @param password The plaintext password for the new user. It will be salted and hashed before being stored.
     * @param email The email address for the new user. Must be valid and unique.
     * @param publicKey The RSA public key associated with the user.
     * @param privateKeyEncrypted The encrypted private RSA key of the user.
     * @throws IllegalArgumentException If validation fails for any parameter.
     * @throws DataAccessException If an error occurs while persisting, the user in the database.
     */
    void createUser(@NotNull String username, @NotNull String password, @NotNull String email,
                    @NotNull String publicKey, @NotNull String privateKeyEncrypted) throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves a user from the database by their unique identifier (UUID).
     *
     * @param userId The unique identifier (UUID) of the user to retrieve.
     * @return The {@link EUser} entity corresponding to the given {@code userId}, or {@code null} if no user is found.
     * @throws DataAccessException If an issue occurs while querying the database.
     */
    @Nullable EUser getUserById(@NotNull UUID userId) throws DataAccessException;

    /**
     * Retrieves a user from the database using their username.
     *
     * <p>The input username is validated before querying the database.</p>
     *
     * @param username The username of the user to retrieve.
     * @return The {@link EUser} entity corresponding to the specified {@code username}, or {@code null} if no user is found.
     * @throws IllegalArgumentException If the {@code username} fails validation.
     * @throws DataAccessException If there is an issue during the query process.
     */
    @Nullable EUser getUserByUsername(@NotNull String username) throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves a user from the database using their email address.
     *
     * <p>The input email address is validated before querying the database.</p>
     *
     * @param emailAddress The email address of the user to retrieve. Must not be null.
     * @return The {@link EUser} entity corresponding to the given email address, or {@code null} if not found.
     * @throws IllegalArgumentException If the {@code emailAddress} fails validation.
     * @throws DataAccessException If there is an issue during the query process.
     */
    @Nullable EUser getUserByEmail(@NotNull String emailAddress) throws IllegalArgumentException, DataAccessException;

    /**
     * Updates the username of a user identified by their UUID.
     *
     * <p>This method validates the new username and ensures it is unique
     * before committing the changes to the database.</p>
     *
     * @param userId The unique identifier (UUID) of the user to update.
     * @param username The new username to assign. It must pass validation checks.
     * @throws IllegalArgumentException If the {@code username} is invalid.
     * @throws DataAccessException If there is an issue updating the database.
     */
    void updateUsername(@NotNull UUID userId, @NotNull String username) throws IllegalArgumentException, DataAccessException;

    /**
     * Updates the email address of the specified user.
     *
     * <p>The email address is validated and must be unique before the update is committed.</p>
     *
     * @param userId The unique identifier (UUID) of the user to update.
     * @param email The new email address to assign. Must pass validation checks.
     * @throws IllegalArgumentException If the {@code email} is invalid.
     * @throws DataAccessException If there is an issue updating the database.
     */
    void updateEmail(@NotNull UUID userId, @NotNull String email) throws IllegalArgumentException, DataAccessException;

    /**
     * Updates the password of a user identified by their unique user ID.
     *
     * <p>This method retrieves the user's current salt from the database, hashes
     * the new password with it, and updates the user's password hash field with the resulting value.</p>
     *
     * @param userId  The UUID of the user whose password is to be updated.
     * @param newPassword The new plaintext password to be set.
     * @throws IllegalArgumentException If the {@code newPassword} fails validation (e.g., too shorts, too weak).
     * @throws DataAccessException      If any database-related issue occurs during the update operation.
     */
    void changePassword(@NotNull UUID userId, @NotNull String newPassword) throws IllegalArgumentException, DataAccessException;
    
    /**
     * Updates the RSA public and private keys of the specified user.
     *
     * @param userId The unique identifier (UUID) of the user to update.
     * @param newPublicKey The new RSA public key to save.
     * @param newPrivateKeyEncrypted The new encrypted private RSA key to save.
     * @throws DataAccessException If an issue occurs while updating the database.
     */
    void updateKeys(@NotNull UUID userId, @NotNull String newPublicKey, @NotNull String newPrivateKeyEncrypted) throws DataAccessException;

    /**
     * Deletes a user from the database by their unique identifier.
     *
     * @param userId The unique identifier (UUID) of the user to delete.
     * @throws DataAccessException If an issue occurs while deleting the user from the database.
     */
    void deleteUser(@NotNull UUID userId) throws DataAccessException;

    /**
     * Verifies if the provided password matches the stored, securely hashed password of the user.
     *
     * <p>The method retrieves the user's stored password hash and salt, then
     * hashes the provided password with the same salt for comparison.</p>
     *
     * @param username The username of the user whose password needs to be verified.
     * @param password The plaintext password to verify.
     * @return {@code true} if the password matches the stored hash, or {@code false} otherwise.
     * @throws IllegalArgumentException If the username or password fails validation.
     * @throws DataAccessException If there is an issue accessing the database.
     */
    boolean verifyPassword(@NotNull String username, @NotNull String password) throws IllegalArgumentException, DataAccessException;
}