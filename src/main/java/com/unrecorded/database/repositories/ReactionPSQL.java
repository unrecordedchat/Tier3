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
import com.unrecorded.database.entities.EReaction.ReactionId;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.util.FieldValidator;
import com.unrecorded.database.util.HibernateUtil;
import com.unrecorded.database.util.LoggerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * This class manages database operations for reactions, including creation, retrieval, deletion, and soft deletion.
 *
 * <p><b>Purpose:</b> The `ReactionPSQL` class implements the `IReactionRepo` interface,
 * providing methods for performing CRUD operations on reactions while adhering
 * to input validation, logging, and transaction management best practices.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Field validation for emoji constraints and composite key validation.</li>
 *   <li>Efficient retrieval of reactions using Hibernate queries.</li>
 *   <li>Structured and secure logging for all operations using {@link LoggerUtil}.</li>
 *   <li>Thread-safe and reusable transaction handling via {@link HibernateUtil}.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see EReaction
 * @see HibernateUtil
 * @since 0.2
 */
public class ReactionPSQL implements IReactionRepo {

    /**
     * Creates and saves a new reaction in the database.
     *
     * <p>This method validates the required parameters, constructs a reaction entity,
     * and persists it in the database.</p>
     *
     * @param userId    The UUID of the user reacting.
     * @param messageId The UUID of the message being reacted to.
     * @param emoji     The emoji used for the reaction. Must not be empty and should meet allowed emoji constraints.
     * @throws IllegalArgumentException If validation fails for any input parameters.
     * @throws DataAccessException      If there is an issue with the database operation.
     */
    @Override
    public void createReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String emoji) throws IllegalArgumentException, DataAccessException {
        FieldValidator.emojiConstraints(emoji);
        LoggerUtil.logInfo("Creating reaction for user " + userId + ", message {}, emoji {}", messageId.toString(), emoji);
        HibernateUtil.executeTransaction(true, session -> {
            EReaction existingReaction = getReaction(userId, messageId, emoji);
            if (existingReaction != null) throw new IllegalArgumentException("Reaction already exists.");
            EReaction reaction = new EReaction(userId, messageId, emoji);
            session.persist(reaction);
            LoggerUtil.logInfo("Reaction created successfully: {}", reaction.toString());
            return null;
        });
    }

    /**
     * Retrieves a reaction by its composite primary key.
     *
     * @param userId    The UUID of the user who reacted.
     * @param messageId The UUID of the message that received the reaction.
     * @param emoji     The emoji used in the reaction.
     * @return The corresponding {@link EReaction} entity, or {@code null} if not found.
     * @throws IllegalArgumentException If any input parameter is invalid.
     * @throws DataAccessException      If an error occurs during the database query.
     */
    @Override
    public @Nullable EReaction getReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String emoji) throws IllegalArgumentException, DataAccessException {
        FieldValidator.emojiConstraints(emoji);
        LoggerUtil.logInfo("Fetching reaction for user " + userId + ", message {}, emoji {}", messageId.toString(), emoji);
        return HibernateUtil.executeTransaction(false, session -> {
            ReactionId reactionId = new ReactionId(userId, messageId, emoji);
            EReaction reaction = session.find(EReaction.class, reactionId);
            if (reaction != null) LoggerUtil.logDebug("Reaction retrieved: {}", reaction.toString());
            else
                LoggerUtil.logWarn("No reaction found for user " + userId + ", message " + messageId + ", emoji " + emoji + "");
            return reaction;
        });
    }

    /**
     * Retrieves all reactions associated with a specific message.
     *
     * <p>This method fetches all reactions from the `reactions` table in the database that belong
     * to a specific message, ordered by default criteria (e.g., insertion order).</p>
     *
     * @param messageId The UUID of the target message being queried.
     * @return A list of {@link EReaction} entities corresponding to the specified message.
     * @throws DataAccessException If the database query fails.
     */
    @Override
    public @Nullable List<EReaction> getReactionsForMessage(@NotNull UUID messageId) throws DataAccessException {
        LoggerUtil.logInfo("Fetching all reactions for messageId={}", messageId.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            List<EReaction> reactions = session.createQuery("FROM EReaction WHERE id.messageId = :messageId", EReaction.class).setParameter("messageId", messageId).list();
            LoggerUtil.logInfo("Fetched {} reactions for messageId={}", String.valueOf(reactions.size()), messageId.toString());
            return reactions;
        });
    }

    /**
     * Deletes a reaction from the database using its composite key.
     *
     * @param userId    The UUID of the user who reacted.
     * @param messageId The UUID of the message that received the reaction.
     * @param emoji     The emoji used in the reaction.
     * @throws IllegalArgumentException If validation fails for the input parameters.
     * @throws DataAccessException      If the database operation fails.
     */
    @Override
    public void deleteReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String emoji) throws IllegalArgumentException, DataAccessException {
        FieldValidator.emojiConstraints(emoji);
        LoggerUtil.logInfo("Deleting reaction for user " + userId + ", messageId={}, emoji={}", messageId.toString(), emoji);
        HibernateUtil.executeTransaction(true, session -> {
            ReactionId id = new ReactionId(userId, messageId, emoji);
            EReaction reaction = session.find(EReaction.class, id);
            if (reaction != null) {
                session.remove(reaction);
                LoggerUtil.logInfo("Reaction deleted successfully: {}", reaction.toString());
            } else
                LoggerUtil.logWarn("No reaction found to delete for user " + userId + ", message " + messageId + ", emoji " + emoji);
            return null;
        });
    }
}