package com.unrecorded.database.repositories;

import com.unrecorded.database.entities.EFriendship;
import com.unrecorded.database.exceptions.DataAccessException;
import jdk.jfr.Description;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining the database operations for the Friendship entity.
 */
public interface IFriendshipRepo {

    /**
     * Creates a new friendship in the database.
     *
     * @param userId1 The UUID of the first user. Must not be null.
     * @param userId2 The UUID of the second user. Must not be null.
     * @param status  The status of the friendship. Must not be null.
     * @throws DataAccessException If there is an issue accessing the database during friendship creation.
     */
    void createFriendship(@NotNull UUID userId1, @NotNull UUID userId2, @NotNull String status) throws DataAccessException;

    /**
     * Retrieves a friendship from the database by its composite key.
     *
     * @param userId1 The UUID of the first user.
     * @param userId2 The UUID of the second user.
     * @return The EFriendship object corresponding to the specified composite key, or null if no friendship is found.
     */
    @Nullable EFriendship getFriendship(@NotNull UUID userId1, @NotNull UUID userId2);

    /**
     * Updates the status of a specified friendship.
     *
     * @param userId1 The UUID of the first user.
     * @param userId2 The UUID of the second user.
     * @param newStatus The new status to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    void updateFriendshipStatus(@NotNull UUID userId1, @NotNull UUID userId2, @NotNull String newStatus) throws DataAccessException;

    /**
     * Deletes a friendship from the database by its composite key.
     *
     * @param userId1 The UUID of the first user.
     * @param userId2 The UUID of the second user.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    void deleteFriendship(@NotNull UUID userId1, @NotNull UUID userId2) throws DataAccessException;

    /**
     * Retrieves all friendships for a specific user.
     *
     * @param userId The UUID of the user whose friendships are to be retrieved.
     * @return A list of EFriendship objects corresponding to the specified userId, or an empty list if no friendships are found.
     */
    @NotNull List<EFriendship> getFriendshipsForUser(@NotNull UUID userId);

    /**
     * Retrieves all friendships in the database.
     *
     * @return A list of all EFriendship objects stored in the database.
     * @throws DataAccessException If there is an issue accessing the database during retrieval.
     */
    @Description("Administration")
    @NotNull List<EFriendship> getAllFriendships() throws DataAccessException;
}