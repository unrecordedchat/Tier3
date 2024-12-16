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

import com.unrecorded.database.entities.EGroup;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining group-related database operations.
 *
 * <p>The {@code IGroupRepo} interface specifies methods for interacting with the
 * {@link EGroup} entity, including CRUD (Create, Read, Update, Delete) functionality
 * and additional group-specific operations like ownership transfer and group membership validation.
 * Implementations should ensure appropriate validation, error handling, and secure data management.</p>
 *
 * <p>All database-related exceptions should be wrapped in {@link DataAccessException}
 * for uniform error reporting.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>CRUD operations for managing group data in the database.</li>
 *   <li>Validation of inputs such as group names and ownership updates.</li>
 *   <li>Support for thread-safe transaction handling and error integrity.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since 0.3
 */
public interface IGroupRepo {

    /**
     * Creates a new group in the database.
     * <h3>Steps:</h3>
     * <ol>
     *     <li>Validate the {@code name} and {@code ownerId} fields.</li>
     *     <li>Create and persist the {@link EGroup} entity in the database.</li>
     * </ol>
     *
     * @param name    The name of the group. Must conform to validation rules.
     * @param ownerId The unique identifier (UUID) of the group owner.
     * @throws IllegalArgumentException If the group name is invalid or empty.
     * @throws DataAccessException      If an issue occurs during database operations.
     */
    void createGroup(@NotNull String name, @NotNull UUID ownerId) throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves a group by its unique identifier from the database.
     *
     * @param groupId The unique identifier (UUID) of the group.
     * @return The {@link EGroup} entity if found, or {@code null} if not found or inactive.
     * @throws DataAccessException If an issue occurs while querying the database.
     */
    @Nullable EGroup getGroupById(@NotNull UUID groupId) throws DataAccessException;

    /**
     * Retrieves all groups owned by a specific user.
     *
     * <p>The {@code ownerId} must be a valid user UUID, and the method returns active groups
     * matching the specified owner ID.</p>
     *
     * @param ownerId The unique identifier (UUID) of the group owner.
     * @return A list of groups owned by the user, or an empty list if no groups exist.
     * @throws DataAccessException If an issue occurs during database queries.
     */
    @Nullable List<EGroup> getGroupsByOwner(@NotNull UUID ownerId) throws DataAccessException;

    /**
     * Updates the name of an existing group.
     *
     * <h3>Steps:</h3>
     * <ol>
     *     <li>Validate the new group name.</li>
     *     <li>Locate the group via {@code groupId} and modify its {@code name}.</li>
     *     <li>Persist the changes into the database.</li>
     * </ol>
     *
     * @param groupId The unique identifier (UUID) of the group to update.
     * @param newName The new name for the group. Must conform to validation rules.
     * @throws IllegalArgumentException If the new name is invalid or empty.
     * @throws DataAccessException      If an issue occurs during database modifications.
     */
    void updateGroupName(@NotNull UUID groupId, @NotNull String newName) throws IllegalArgumentException, DataAccessException;

    /**
     * Deletes a group from the database.
     *
     * <p>This method performs a "soft delete," marking the group as inactive
     * rather than completely removing it from the database to preserve audit data.</p>
     *
     * @param groupId The unique identifier (UUID) of the group.
     * @throws DataAccessException If an issue occurs during deletion operations.
     */
    void deleteGroup(@NotNull UUID groupId) throws DataAccessException;

    /**
     * Transfers ownership of a group to a new owner.
     *
     * <h3>Steps:</h3>
     * <ol>
     *     <li>Validate the {@code groupId} and new owner ID ({@code newOwnerId}).</li>
     *     <li>Verify that {@code newOwnerId} is a group member.</li>
     *     <li>Update the owner ID field in the group entity and persist the changes.</li>
     * </ol>
     *
     * @param groupId    The unique identifier (UUID) of the group.
     * @param newOwnerId The unique identifier (UUID) of the new owner.
     * @throws IllegalArgumentException If the {@code newOwnerId} is not a group member or input is invalid.
     * @throws DataAccessException      If a database error occurs during operations.
     */
    void updateGroupOwner(@NotNull UUID groupId, @NotNull UUID newOwnerId) throws IllegalArgumentException, DataAccessException;
}