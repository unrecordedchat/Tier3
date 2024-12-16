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

import com.unrecorded.database.entities.EReaction;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining database operations for managing reactions.
 *
 * <p>The {@code IReactionRepo} interface specifies methods for performing CRUD (Create, Read, Update, Delete) operations
 * on {@link EReaction} entities, along with additional reaction-specific functionality.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Supports creation of reactions with validation of inputs.</li>
 *     <li>Enables retrieval and deletion of reactions based on composite keys.</li>
 *     <li>Provides methods to handle list-based queries for reactions associated with specific messages.</li>
 *     <li>Ensures consistent use of error handling and reporting through {@link DataAccessException}.</li>
 * </ul>
 *
 * <p>All implementations should ensure appropriate behavior for managing
 * the composite primary key (userId, messageId, emoji) inherent to {@code EReaction} models.</p>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see EReaction
 * @see DataAccessException
 * @since 0.2
 */
public interface IReactionRepo {

    /**
     * Creates a new reaction in the database.
     *
     * <p>This method validates input parameters, constructs a new {@link EReaction} entity,
     * and persists it in the database.</p>
     *
     * @param userId    The UUID of the user reacting.
     * @param messageId The UUID of the message being reacted to.
     * @param emoji     The emoji associated with the reaction.
     * @throws IllegalArgumentException If the input validation fails (e.g., invalid emoji).
     * @throws DataAccessException      If an issue occurs while persisting the reaction in the database.
     */
    void createReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String emoji)
            throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves a specific reaction from the database using its composite key.
     *
     * <p>Composite key for a reaction includes `userId`, `messageId`, and `emoji`.
     * If the reaction does not exist, the method returns {@code null}.</p>
     *
     * @param userId    The UUID of the user who reacted.
     * @param messageId The UUID of the message that received the reaction.
     * @param emoji     The emoji associated with the reaction.
     * @return The corresponding {@link EReaction} entity, or {@code null} if no reaction exists.
     * @throws IllegalArgumentException If input parameters fail validation.
     * @throws DataAccessException      If an error occurs during database retrieval.
     */
    @Nullable EReaction getReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String emoji)
            throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves all reactions associated with a specific message.
     *
     * <p>This method performs a query to fetch all reactions from the database
     * where the messageId matches the provided parameter.</p>
     *
     * @param messageId The UUID of the target message.
     * @return A {@link List} of {@link EReaction} entities linked to the specified message.
     * @throws DataAccessException If an issue occurs while retrieving reactions from the database.
     */
    @Nullable List<EReaction> getReactionsForMessage(@NotNull UUID messageId) throws DataAccessException;

    /**
     * Deletes a reaction from the database based on its composite key.
     *
     * <p>This method removes the reaction entity corresponding to the provided composite key
     * from the database.
     * If no such reaction exists, no changes are made.</p>
     *
     * @param userId    The UUID of the user who reacted.
     * @param messageId The UUID of the message that received the reaction.
     * @param emoji     The emoji associated with the reaction.
     * @throws IllegalArgumentException If input parameters fail validation.
     * @throws DataAccessException      If the database operation fails.
     */
    void deleteReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String emoji) throws IllegalArgumentException, DataAccessException;
}