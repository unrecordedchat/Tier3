package com.unrecorded.database.repositories;

import com.unrecorded.database.entities.EUser;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Interface defining database operations for the User entity.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since PREVIEW
 */
public interface IUserRepo {

    /**
     * Creates a new user in the database.
     *
     * @param username            The username for the new user. Must not be null.
     * @param password            The password for the new user. Must not be null.
     * @param emailAddress        The email address for the new user. Must not be null.
     * @param publicKey           The public key for the new user. Must not be null.
     * @param privateKeyEncrypted The encrypted private key for the new user. Must not be null.
     * @throws DataAccessException If there is an issue accessing the database during user creation.
     */
    void createUser(@NotNull String username, @NotNull String password, @NotNull String emailAddress, @NotNull String publicKey, @NotNull String privateKeyEncrypted) throws DataAccessException;

    /**
     * Retrieves a user from the database by their unique identifier.
     *
     * @param userId The unique identifier of the user to retrieve.
     * @return The EUser object corresponding to the specified userId, or null if no user is found.
     */
    @Nullable EUser getUserById(@NotNull UUID userId);

    /**
     * Retrieves a user from the database using their username.
     *
     * @param username The username of the user to retrieve.
     * @return The EUser object corresponding to the specified username, or null if no user is found.
     */
    @Nullable EUser getUserByUsername(@NotNull String username);

    /**
     * Retrieves a user from the database using their email address.
     *
     * @param emailAddress The email address of the user to retrieve.
     * @return The EUser object corresponding to the specified email address, or null if no user is found.
     */
    @Nullable EUser getUserByEmail(String emailAddress);

    /**
     * Updates the username of the specified user.
     *
     * @param userId   The unique identifier of the user.
     * @param username The new username to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    void updateUsername(@NotNull UUID userId, @NotNull String username) throws DataAccessException;

    /**
     * Updates the email address of the specified user.
     *
     * @param userId       The unique identifier of the user.
     * @param emailAddress The new email address to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    void updateEmailAddress(@NotNull UUID userId, @NotNull String emailAddress) throws DataAccessException;

    /**
     * Updates the public and encrypted private keys of the specified user.
     *
     * @param userId                 The unique identifier of the user.
     * @param newPublicKey           The new public key to be set.
     * @param newPrivateKeyEncrypted The new encrypted private key to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    void updateKeys(@NotNull UUID userId, @NotNull String newPublicKey, @NotNull String newPrivateKeyEncrypted) throws DataAccessException;

    /**
     * Deletes a user from the database by their unique identifier.
     *
     * @param userId The unique identifier of the user to be deleted.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    void deleteUser(@NotNull UUID userId) throws DataAccessException;

    /**
     * Verifies if the provided password matches the stored password of the user with the specified username.
     *
     * @param username The username of the user whose password needs to be verified
     * @param password The provided password to verify
     * @return True if the provided password matches the stored password, false otherwise
     */
    boolean verifyPassword(@NotNull String username, @NotNull String password);
}