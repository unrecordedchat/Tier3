package com.unrecorded.database.repositories;

import com.unrecorded.database.entities.EReaction;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining the database operations for the Reaction entity.
 */
public interface IReactionRepo {

    /**
     * Creates a new reaction in the database.
     *
     * @param userId The UUID of the user. Must not be null.
     * @param messageId The UUID of the message. Must not be null.
     * @param type The type of the reaction. Must not be null.
     * @throws DataAccessException If there is an issue accessing the database during reaction creation.
     */
    void createReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String type) throws DataAccessException;

    /**
     * Retrieves a reaction from the database by its composite key.
     *
     * @param userId The UUID of the user.
     * @param messageId The UUID of the message.
     * @param type The type of the reaction.
     * @return The EReaction object corresponding to the specified composite key, or null if no reaction is found.
     */
    EReaction getReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String type);

    /**
     * Deletes a reaction from the database by its composite key.
     *
     * @param userId The UUID of the user.
     * @param messageId The UUID of the message.
     * @param type The type of the reaction.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    void deleteReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String type) throws DataAccessException;

    /**
     * Retrieves all reactions for a specific message.
     *
     * @param messageId The UUID of the message.
     * @return A list of EReaction objects corresponding to the specified message.
     */
    @NotNull List<EReaction> getReactionsForMessage(@NotNull UUID messageId);
}