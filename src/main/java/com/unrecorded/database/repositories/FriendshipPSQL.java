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

import com.unrecorded.database.entities.EFriendship;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.util.FieldValidator;
import com.unrecorded.database.util.HibernateUtil;
import com.unrecorded.database.util.LoggerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * This class manages operations related to friendships, including creation, retrieval, updating, and deletion.
 * <p>
 * The `FriendshipPSQL` class handles database transactions for the `EFriendship` entity with a focus on
 * securing data, detailed logging, and validation.
 * It uses Hibernate ORM to interact with a PostgreSQL database.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>CRUD operations for `EFriendship` entities.</li>
 *   <li>Detailed debug and information logging through {@link LoggerUtil}.</li>
 *   <li>Graceful error handling via {@link DataAccessException}.</li>
 *   <li>Validation of friendship constraints and user inputs.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>This class uses Hibernate sessions and thread-safe utilities, ensuring operations remain secure in multithreaded
 * environments.</p>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see EFriendship
 * @see HibernateUtil
 * @since PREVIEW
 */
public class FriendshipPSQL implements IFriendshipRepo {

    /**
     * Creates a new friendship between two users in the database.
     *
     * <p>This method validates the input parameters, creates a new friendship entity, and stores it
     * in the database.
     * It ensures that the input fields are correct and no constraint violations occur during the creation process.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Validate the UUIDs of the users involved.</li>
     *   <li>Validate the friendship status (e.g., "FRD", "PND").</li>
     *   <li>Attempt to persist the `EFriendship` entity in the database.</li>
     *   <li>Rollback the transaction and throw a {@link DataAccessException} in case of failure.</li>
     * </ol>
     *
     * @param userId1 The unique identifier (UUID) of the first user.
     * @param userId2 The unique identifier (UUID) of the second user.
     * @param status  The status of the friendship (e.g., "FRD", "PND").
     * @throws IllegalArgumentException If the input data is invalid or null.
     * @throws DataAccessException      If an error occurs during database access.
     * @see EFriendship
     */
    public void createFriendship(@NotNull UUID userId1, @NotNull UUID userId2, @NotNull String status) throws IllegalArgumentException, DataAccessException {
        FieldValidator.userLinkConstraints(userId1, userId2);
        FieldValidator.friendStatusConstraints(status);
        LoggerUtil.logInfo("Creating friendship between userId1: " + userId1 + " and userId2: " + userId2);
        HibernateUtil.executeTransaction(true, session -> {
            if (getFriendship(userId1, userId2) != null) {
                LoggerUtil.logWarn("Friendship already exists between userId1: " + userId1 + " and userId2: " + userId2);
                throw new IllegalArgumentException("Friendship already exists.");
            }
            EFriendship friendship = new EFriendship(userId1, userId2, status);
            session.persist(friendship);
            LoggerUtil.logInfo("Friendship successfully created between userId1: " + userId1 + " and userId2: " + userId2);
            return null;
        });
    }

    /**
     * Retrieves a friendship based on the two user IDs.
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Validate the UUIDs of the users.</li>
     *   <li>Query the database for a matching `EFriendship` entity.</li>
     *   <li>Return the entity or `null` if no result is found.</li>
     * </ol>
     *
     * @param userId1 The unique identifier of the first user.
     * @param userId2 The unique identifier of the second user.
     * @return The friendship entity if present, `null` otherwise.
     * @throws IllegalArgumentException If UUIDs are invalid.
     * @throws DataAccessException      If an error occurs during the query.
     */
    public EFriendship getFriendship(@NotNull UUID userId1, @NotNull UUID userId2) throws IllegalArgumentException, DataAccessException {
        FieldValidator.userLinkConstraints(userId1, userId2);
        LoggerUtil.logDebug("Retrieving friendship between userId1: " + userId1 + " and userId2: " + userId2);
        return HibernateUtil.executeTransaction(false, session -> session.get(EFriendship.class, new EFriendship.FriendshipId(userId1, userId2)));
    }

    /**
     * Updates the status of an existing friendship.
     *
     * <p>The method ensures that the friendship exists before updating and validates the new status.</p>
     *
     * @param userId1   The UUID of the first user in the friendship.
     * @param userId2   The UUID of the second user in the friendship.
     * @param newStatus The new status for the friendship.
     * @throws IllegalArgumentException If the status is invalid or friendship does not exist.
     * @throws DataAccessException      If there is an issue with the database update.
     */
    public void updateFriendshipStatus(@NotNull UUID userId1, @NotNull UUID userId2, @NotNull String newStatus) throws IllegalArgumentException, DataAccessException {
        FieldValidator.userLinkConstraints(userId1, userId2);
        FieldValidator.friendStatusConstraints(newStatus);
        LoggerUtil.logInfo("Updating friendship status between userId1: " + userId1 + " and userId2: " + userId2);
        HibernateUtil.executeTransaction(true, session -> {
            EFriendship friendship = getFriendship(userId1, userId2);
            if (friendship == null) throw new IllegalArgumentException("Friendship does not exist.");
            friendship.setStatus(newStatus);
            session.merge(friendship);
            LoggerUtil.logInfo("Successfully updated friendship status between " + userId1 + " and " + userId2 + " to " + newStatus);
            return null;
        });
    }

    /**
     * Deletes a friendship between two users.
     *
     * @param userId1 The UUID of the first user.
     * @param userId2 The UUID of the second user.
     * @throws IllegalArgumentException If the friendship does not exist.
     * @throws DataAccessException      If there is an error during deletion.
     */
    public void deleteFriendship(@NotNull UUID userId1, @NotNull UUID userId2) throws IllegalArgumentException, DataAccessException {
        FieldValidator.userLinkConstraints(userId1, userId2);
        LoggerUtil.logInfo("Deleting friendship between userId1: " + userId1 + " and userId2: " + userId2);
        HibernateUtil.executeTransaction(true, session -> {
            EFriendship friendship = getFriendship(userId1, userId2);
            if (friendship == null) throw new IllegalArgumentException("Friendship does not exist.");
            session.remove(friendship);
            LoggerUtil.logInfo("Successfully deleted friendship between " + userId1 + " and " + userId2);
            return null;
        });
    }

    /**
     * Returns all friendships for a user by UUID.
     *
     * @param userId The UUID of the user whose friendships are being retrieved.
     * @return A list of friendships involving the specified user.
     * @throws DataAccessException If a database query fails.
     */
    public @Nullable List<EFriendship> getFriendshipsForUser(@NotNull UUID userId) throws DataAccessException {
        LoggerUtil.logDebug("Retrieving friendships for userId: " + userId);
        return HibernateUtil.executeTransaction(false, session -> session.createQuery("FROM EFriendship WHERE id.userId1 = :userId OR id.userId2 = :userId", EFriendship.class).setParameter("userId", userId).list());
    }
}