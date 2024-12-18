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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining the database operations for the Friendship entity.
 *
 * @author Sergiu Churap
 * @version 1.0
 * @since 0.2
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
     * @param userId1   The UUID of the first user.
     * @param userId2   The UUID of the second user.
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
    @Nullable List<EFriendship> getFriendshipsForUser(@NotNull UUID userId);
}