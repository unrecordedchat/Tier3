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
import com.unrecorded.database.util.FieldValidator;
import com.unrecorded.database.util.HibernateUtil;
import com.unrecorded.database.util.LoggerUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * This class manages operations related to group memberships, including adding members,
 * retrieving memberships, and updating/deleting membership records.
 *
 * <p><b>Purpose:</b> The `GroupMemberPSQL` class handles CRUD (Create, Read, Update, Delete)
 * operations for the `EGroupMember` entity in a PostgreSQL database using Hibernate ORM.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Adding members to groups and assigning roles.</li>
 *   <li>Retrieving group members or user membership details.</li>
 *   <li>Updating membership roles and other properties.</li>
 *   <li>Deleting memberships from the database.</li>
 * </ul>
 *
 * <p><b>Note:</b> All database-related exceptions are wrapped in {@link DataAccessException}
 * for consistent error reporting.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see EGroupMember
 * @see HibernateUtil
 * @since 0.3
 */
public class GroupMemberPSQL implements IGroupMemberRepo {

    /**
     * Adds a new membership for a user in a group with the specified role.
     *
     * <p>Validates the inputs and persists a new {@link EGroupMember} to the database.</p>
     *
     * @param groupId The unique ID of the group.
     * @param userId  The unique ID of the user.
     * @param role    The role assigned to the user within the group.
     * @throws IllegalArgumentException If validation fails for inputs.
     * @throws DataAccessException      If a database issue occurs while creating the membership.
     */
    public void addMemberToGroup(@NotNull UUID groupId, @NotNull UUID userId, @NotNull String role) throws IllegalArgumentException, DataAccessException {
        FieldValidator.groupRoleConstraints(role);
        LoggerUtil.logInfo("Adding user to group. group: " + groupId + ", user: {}, role: {}", userId.toString(), role);
        HibernateUtil.executeTransaction(true, session -> {
            EGroupMember groupMember = new EGroupMember(groupId, userId, role);
            session.persist(groupMember);
            LoggerUtil.logInfo("Successfully added user: " + userId + " to group: {} with role: {}", groupId.toString(), role);
            return null;
        });
    }

    /**
     * Fetches all memberships for a specific group.
     *
     * @param groupId The unique ID of the group.
     * @return A list of {@link EGroupMember} representing the group members.
     * Returns an empty list if no members are found.
     * @throws DataAccessException If a database issue occurs during the fetch.
     */
    public @Nullable List<EGroupMember> getMembersByGroupId(@NotNull UUID groupId) throws DataAccessException {
        LoggerUtil.logInfo("Fetching members of groupId: {}", groupId.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<EGroupMember> query = builder.createQuery(EGroupMember.class);
            Root<EGroupMember> root = query.from(EGroupMember.class);
            query.select(root).where(builder.equal(root.get("id").get("groupId"), groupId));
            List<EGroupMember> members = session.createQuery(query).getResultList();
            LoggerUtil.logInfo("Found {} members in groupId: {}", String.valueOf(members.size()), groupId.toString());
            return members;
        });
    }

    /**
     * Fetches all group memberships for a specific user.
     *
     * @param userId The unique ID of the user.
     * @return A list of {@link EGroupMember} representing the memberships of the user.
     * Returns an empty list if the user is not part of any group.
     * @throws DataAccessException If a database issue occurs during the fetch.
     */
    public @Nullable List<EGroupMember> getGroupsByUserId(@NotNull UUID userId) throws DataAccessException {
        LoggerUtil.logInfo("Fetching groups for userId: {}", userId.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<EGroupMember> query = builder.createQuery(EGroupMember.class);
            Root<EGroupMember> root = query.from(EGroupMember.class);
            query.select(root).where(builder.equal(root.get("id").get("userId"), userId));
            List<EGroupMember> memberships = session.createQuery(query).getResultList();
            LoggerUtil.logInfo("Found {} groups for user: {}", String.valueOf(memberships.size()), userId.toString());
            return memberships;
        });
    }

    /**
     * Updates the role of a user in a specific group.
     *
     * @param groupId The ID of the group.
     * @param userId  The ID of the user.
     * @param newRole The new role to assign.
     * @throws IllegalArgumentException If validation fails for the new role.
     * @throws DataAccessException      If a database issue occurs during the update, or if the membership record is not found.
     */
    public void updateMemberRole(@NotNull UUID groupId, @NotNull UUID userId, @NotNull String newRole) throws IllegalArgumentException, DataAccessException {
        FieldValidator.groupRoleConstraints(newRole);
        LoggerUtil.logInfo("Updating role for user: " + userId + " in group: {}. New role: {}", groupId.toString(), newRole);
        HibernateUtil.executeTransaction(true, session -> {
            EGroupMember groupMember = session.get(EGroupMember.class, new EGroupMember.GroupMemberId(groupId, userId));
            if (groupMember != null) {
                groupMember.setRole(newRole);
                session.merge(groupMember);
                LoggerUtil.logInfo("Successfully updated role of user: {} in group: {}", userId.toString(), groupId.toString());
            } else {
                LoggerUtil.logWarn("Membership not found for user: " + userId + " in group: {}" + groupId);
                throw new IllegalArgumentException("Membership not found.");
            }
            return null;
        });
    }

    /**
     * Deletes a specific group membership.
     *
     * @param groupId The ID of the group.
     * @param userId  The ID of the user.
     * @throws DataAccessException If a database issue occurs during the deletion.
     */
    public void removeMemberFromGroup(@NotNull UUID groupId, @NotNull UUID userId) throws DataAccessException {
        LoggerUtil.logInfo("Removing user: {} from group: {}", userId.toString(), groupId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EGroupMember groupMember = session.get(EGroupMember.class, new EGroupMember.GroupMemberId(groupId, userId));
            if (groupMember != null) {
                session.remove(groupMember);
                LoggerUtil.logInfo("Successfully removed user: {} from group: {}", userId.toString(), groupId.toString());
            } else LoggerUtil.logWarn("No membership record found for user: " + userId + " in group: " + groupId);
            return null;
        });
    }
}