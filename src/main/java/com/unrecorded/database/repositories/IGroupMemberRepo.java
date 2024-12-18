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

import com.unrecorded.database.entities.EGroupMember;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining database operations for managing group memberships.
 *
 * <p>The {@code IGroupMemberRepo} interface specifies the methods for interacting
 * with the {@link EGroupMember} entity, covering all CRUD (Create, Read, Update, Delete)
 * operations and additional utilities related to group membership management.</p>
 *
 * <p>Implementations should ensure proper validation, error handling, and efficient
 * transaction management for all operations while maintaining thread safety.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Adding and removing members from groups.</li>
 *   <li>Fetching group members or user memberships.</li>
 *   <li>Updating roles within group memberships.</li>
 *   <li>Thread-safe operations for multithreaded usage.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since 2024
 */
public interface IGroupMemberRepo {

    /**
     * Adds a new membership for a user in a group with a specified role.
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Validates the provided parameters including role constraints.</li>
     *   <li>Persists the {@link EGroupMember} entity representing the membership.</li>
     * </ol>
     *
     * @param groupId The unique identifier (UUID) of the group.
     * @param userId The unique identifier (UUID) of the user.
     * @param role The role of the user within the group.
     * @throws IllegalArgumentException If validation fails for the provided parameters.
     * @throws DataAccessException If an issue occurs while persisting the membership to the database.
     */
    void addMemberToGroup(@NotNull UUID groupId, @NotNull UUID userId, @NotNull String role) throws IllegalArgumentException, DataAccessException;

    /**
     * Fetches all members belonging to a specific group.
     *
     * @param groupId The unique identifier (UUID) of the group whose members are to be retrieved.
     * @return A list of {@link EGroupMember} entities representing the group members.
     *         Returns an empty list if no members are found.
     * @throws DataAccessException If an issue occurs during data retrieval.
     */
    @Nullable List<EGroupMember> getMembersByGroupId(@NotNull UUID groupId) throws DataAccessException;

    /**
     * Fetches all group memberships associated with a specific user.
     *
     * @param userId The unique identifier (UUID) of the user whose memberships are to be retrieved.
     * @return A list of {@link EGroupMember} entities representing the user's memberships.
     *         Returns an empty list if the user is not part of any group.
     * @throws DataAccessException If an issue occurs during data retrieval.
     */
    @Nullable List<EGroupMember> getGroupsByUserId(@NotNull UUID userId) throws DataAccessException;

    /**
     * Updates the role of a user in a specific group.
     *
     * <p>The role will be updated only if a valid membership exists for the given parameters.</p>
     *
     * @param groupId The unique identifier (UUID) of the group.
     * @param userId The unique identifier (UUID) of the user.
     * @param newRole The new role to assign to the user within the group.
     * @throws IllegalArgumentException If validation fails for the new role.
     * @throws DataAccessException If an issue occurs while updating the membership or if the record does not exist.
     */
    void updateMemberRole(@NotNull UUID groupId, @NotNull UUID userId, @NotNull String newRole) throws IllegalArgumentException, DataAccessException;

    /**
     * Removes a user's membership from a specific group.
     *
     * <p>The membership will be deleted if a record exists for the given parameters.</p>
     *
     * @param groupId The unique identifier (UUID) of the group.
     * @param userId The unique identifier (UUID) of the user.
     * @throws DataAccessException If an issue occurs while removing the membership or if the record does not exist.
     */
    void removeMemberFromGroup(@NotNull UUID groupId, @NotNull UUID userId) throws DataAccessException;
}