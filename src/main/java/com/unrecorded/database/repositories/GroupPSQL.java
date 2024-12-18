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
import com.unrecorded.database.entities.EGroupMember;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.util.FieldValidator;
import com.unrecorded.database.util.HibernateUtil;
import com.unrecorded.database.util.LoggerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * This class provides CRUD (Create, Read, Update, Delete) operations for group-related functionality.
 *
 * <p><b>Purpose:</b> The `GroupPSQL` class manages all interactions with the `EGroup` entity in a PostgreSQL
 * database through Hibernate ORM.
 * It handles the creation and modification of groups, group membership validation, 
 * and administrative operations like updating ownership or deleting groups.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Validation: Ensures group name and ownership inputs align with database and business constraints.</li>
 *   <li>Transactional Integrity: Leverages Hibernate to manage transaction consistency and rollback on errors.</li>
 *   <li>Query Optimization: Utilizes named queries and criteria filtering for efficient data access.</li>
 *   <li>Compatibility with group management logic, such as member validation and administrator updates.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>This class is stateless and interacts with thread-safe utilities like {@link HibernateUtil},
 * making it safe for concurrent use in multithreaded applications.</p>
 *
 * <p><b>Note:</b> Exceptions related to database interactions are encapsulated in {@link DataAccessException} for
 * unified error handling and reporting.</p>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see EGroup
 * @see HibernateUtil
 * @since PREVIEW
 */
public class GroupPSQL implements IGroupRepo {

    /**
     * Creates a new group in the database.
     *
     * <p>Validates the group name and owner identifier before persisting a new {@link EGroup} instance.
     * Ensures that additional integrity checks, such as name constraints, are applied.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Checks that the group name and owner ID are valid.</li>
     *   <li>Initializes a new {@link EGroup} entity with these attributes.</li>
     *   <li>Persists the entity using Hibernate ORM.</li>
     * </ol>
     *
     * @param name    A valid and non-empty string representing the group name.
     * @param ownerId The UUID of the user who will be set as the group's owner.
     * @throws IllegalArgumentException If the parameters are invalid, such as an empty name.
     * @throws DataAccessException      If a problem occurs while saving to the database.
     * @see FieldValidator#groupNameConstraints(String)
     */
    @Override
    public void createGroup(@NotNull String name, @NotNull UUID ownerId) throws IllegalArgumentException, DataAccessException {
        FieldValidator.groupNameConstraints(name);
        LoggerUtil.logInfo("Initiating group creation for name: {}, owner: {}", name, ownerId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EGroup group = new EGroup(name, ownerId);
            session.persist(group);
            LoggerUtil.logInfo("Group created successfully: {}", group.toString());
            return null;
        });
    }

    /**
     * Retrieves a specific group from the database using its unique identifier (UUID).
     *
     * <p>This method searches for the {@link EGroup} entity with the provided ID. If no such group exists,
     * or it is inactive, the method returns {@code null}.</p>
     *
     * @param id The UUID of the group to be retrieved.
     * @return The {@link EGroup} entity corresponding to the specified ID, or {@code null} if not found.
     * @throws DataAccessException If any issue occurs while querying the database.
     */
    @Override
    public @Nullable EGroup getGroupById(@NotNull UUID id) throws DataAccessException {
        LoggerUtil.logInfo("Retrieving group by id={}", id.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            EGroup group = session.find(EGroup.class, id);
            if (group != null) LoggerUtil.logDebug("Group retrieved successfully: {}", group.toString());
            else LoggerUtil.logWarn("Group not found or inactive for id " + id);
            return group;
        });
    }

    /**
     * Retrieves all groups owned by a specific user.
     *
     * <p>This method queries the database for all {@link EGroup} entities where the provided user UUID
     * matches the owner ID of the group.</p>
     *
     * @param ownerId The UUID of the user who owns the groups.
     * @return A list of groups owned by the specified user, or an empty list if none exist.
     * @throws DataAccessException If any issue occurs while querying the database.
     */
    @Override
    public @Nullable List<EGroup> getGroupsByOwner(@NotNull UUID ownerId) throws DataAccessException {
        LoggerUtil.logInfo("Fetching groups owned by user: {}", ownerId.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            List<EGroup> groups = session.createQuery("FROM EGroup WHERE ownerId = :ownerId", EGroup.class).setParameter("ownerId", ownerId).list();
            LoggerUtil.logInfo("Fetched {} groups owned by user: {}.", String.valueOf(groups.size()), ownerId.toString());
            return groups;
        });
    }

    /**
     * Updates the name of an existing group.
     *
     * <p>This method validates the new name and updates the `name` field of the corresponding
     * {@link EGroup} entity, ensuring database and business logic compliance.</p>
     *
     * @param groupId The UUID of the group to be updated.
     * @param name    The new name to assign to the group.
     * @throws IllegalArgumentException If the provided name fails validation.
     * @throws DataAccessException      If an error occurs while updating the database.
     * @see FieldValidator#groupNameConstraints(String)
     */
    @Override
    public void updateGroupName(@NotNull UUID groupId, @NotNull String name) throws IllegalArgumentException, DataAccessException {
        FieldValidator.groupNameConstraints(name);
        LoggerUtil.logInfo("Updating group name for group: {}", groupId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EGroup group = session.find(EGroup.class, groupId);
            if (group != null) {
                group.setName(name);
                session.merge(group);
                LoggerUtil.logInfo("Group name updated successfully for group {}: {}", groupId.toString(), group.toString());
            } else LoggerUtil.logWarn("Group not found or inactive for group: " + groupId);
            return null;
        });
    }

    /**
     * Deletes a group from the database.
     *
     * <p>This method uses "soft delete" logic, marking the group as inactive rather than removing it
     * entirely from the database for auditability.</p>
     *
     * @param groupId The UUID of the group to be deleted.
     * @throws DataAccessException If an error occurs while updating the database.
     */
    @Override
    public void deleteGroup(@NotNull UUID groupId) throws DataAccessException {
        LoggerUtil.logInfo("Initiating deletion for group: {}", groupId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EGroup group = session.find(EGroup.class, groupId);
            if (group != null) {
                session.merge(group);
                LoggerUtil.logInfo("Group soft deleted successfully: {}", group.toString());
            } else LoggerUtil.logWarn("Group not found or already inactive for group: " + groupId);
            return null;
        });
    }

    /**
     * Updates the owner of a group.
     *
     * <p>The method transfers ownership to a new user, provided the new owner is already a
     * member of the group (validated via {@link EGroupMember}).</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Validate the provided group ID and user ID for the new owner.</li>
     *   <li>Check if the new owner is listed as a member of the group via the {@link EGroupMember} table.</li>
     *   <li>Update the `ownerId` field of the group if all checks pass.</li>
     * </ol>
     *
     * @param groupId    The UUID of the group for which ownership needs to be transferred.
     * @param newOwnerId The UUID of the user to be set as the new owner.
     * @throws IllegalArgumentException If the new owner is not a valid group member or input is invalid.
     * @throws DataAccessException      If any error occurs while accessing the database.
     * @see EGroupMember
     */
    public void updateGroupOwner(@NotNull UUID groupId, @NotNull UUID newOwnerId) throws IllegalArgumentException, DataAccessException {
        LoggerUtil.logInfo("Initiating group ownership transfer for group ID: {} to new owner: {}", groupId.toString(), newOwnerId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EGroup group = session.find(EGroup.class, groupId);
            if (group == null) {
                LoggerUtil.logWarn("Group not found for group ID: " + groupId);
                throw new IllegalArgumentException("Group does not exist.");
            }
            EGroupMember.GroupMemberId newOwnerKey = new EGroupMember.GroupMemberId(groupId, newOwnerId);
            EGroupMember newOwnerMembership = session.find(EGroupMember.class, newOwnerKey);
            if (newOwnerMembership == null) {
                LoggerUtil.logWarn("User ID: " + newOwnerId + " is not a member of group ID: " + groupId);
                throw new IllegalArgumentException("The new owner must be a member of the group.");
            }
            group.setOwnerId(newOwnerId);
            session.merge(group);
            LoggerUtil.logInfo("Group ownership successfully transferred: group ID = {}, new owner ID = {}", groupId.toString(), newOwnerId.toString());
            return null;
        });
    }
}