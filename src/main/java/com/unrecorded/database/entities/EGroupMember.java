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

package com.unrecorded.database.entities;

import com.unrecorded.database.util.MiscUtils;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * HibernateORM entity representing a membership relationship between a user and a group in the system.
 *
 * <p>This entity connects users to groups, defining their roles and participation within each group.
 * The composite primary key, {@link GroupMemberId}, comprises the user's and group's unique identifiers.</p>
 *
 * <h2>Entity Relationships:</h2>
 * <ul>
 *   <li>Associates with the {@code EUser} entity to identify the user member of the group.</li>
 *   <li>Links to the {@code EGroup} entity to indicate which group the user is part of.</li>
 *   <li>Defines user roles within groups to manage permissions and authority (e.g., member, moderator, admin).</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 1.1
 * @see com.unrecorded.database.repositories.GroupMemberPSQL GroupMemberPSQL
 * @since PREVIEW
 */
@Entity
@Table(name = "group_members", schema = "unrecorded")
public class EGroupMember {

    /**
     * Composite primary key consisting of group ID and user ID, encapsulated in a {@link GroupMemberId} instance.
     * <p>This key uniquely identifies each membership record in the 'group_members' table.</p>
     */
    @EmbeddedId
    @NotNull
    private GroupMemberId id;

    /**
     * Represents the role of the member within the group, influencing permissions and responsibilities.
     * <p>Possible roles include 'member', 'admin', 'moderator', etc., enforcing the group hierarchy.</p>
     */
    @Column(name = "role", nullable = false)
    @NotNull
    private String role;

    /**
     * Default constructor required by JPA.
     */
    public EGroupMember() {
    }

    /**
     * Constructs a new EGroupMember instance with the specified group ID, user ID, and role.
     *
     * @param groupId The unique identifier for the group.
     * @param userId  The unique identifier for the user.
     * @param role    The role of the user within the group.
     */
    public EGroupMember(@NotNull UUID groupId, @NotNull UUID userId, @NotNull String role) {
        this.id = new GroupMemberId(groupId, userId);
        this.role = role;
    }

    /**
     * Retrieves the composite primary key associated with this group membership.
     *
     * @return The {@link GroupMemberId} encapsulating the group and user IDs.
     */
    public @NotNull GroupMemberId getId() {
        return id;
    }

    /**
     * Sets the composite primary key for this group membership.
     *
     * @param id A {@link GroupMemberId} object representing the composite key.
     */
    public void setId(@NotNull GroupMemberId id) {
        this.id = id;
    }

    /**
     * Retrieves the role of the user within the group.
     *
     * @return The role as a string, reflecting the user's position and rights in the group.
     */
    public @NotNull String getRole() {
        return role;
    }

    /**
     * Assigns a role to the user within the group.
     *
     * @param role A non-null and non-empty string representing the user's role.
     */
    public void setRole(@NotNull String role) {
        this.role = Objects.requireNonNull(role, "Role cannot be null or empty");
    }

    /**
     * Determines if this EGroupMember is equivalent to another object.
     *
     * @param o The object to be compared for equality.
     * @return True if the specified object equals this instance; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EGroupMember that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(role, that.role);
    }

    /**
     * Evaluates the hash code of this EGroupMember, based on its ID and role properties.
     *
     * @return An integer representing the hash code.
     */
    @Override
    public int hashCode() {
        return MiscUtils.hash(id, role);
    }

    /**
     * Produces a string representation of the group member, featuring group ID, user ID, and role.
     *
     * @return A structured string detailing this group member entity's state.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM Entity 'GroupMember':\n Group: %s\n User: %s\n Role: %s //\n", id.groupId, id.userId, role);
    }

    /**
     * Represents a composite primary key for the {@link EGroupMember} entity, uniquely
     * identifying the membership of a user within a group in the system.
     *
     * <p>The composite key consists of two unique identifiers: {@code groupId} and {@code userId}.
     * This structure ensures that each membership record is uniquely associated with a specific
     * user and a specific group.</p>
     *
     * <h2>Purpose:</h2>
     * <ul>
     *   <li>Enforces the uniqueness of a user's membership in a group.</li>
     *   <li>Makes it possible to store additional properties of the membership 
     *       (e.g., roles) connected to this key.</li>
     * </ul>
     *
     * <h2>Key Usage:</h2>
     * <ul>
     *   <li>{@link #groupId}: Identifies the group involved in the membership relationship.</li>
     *   <li>{@link #userId}: Identifies the user involved in the membership relationship.</li>
     * </ul>
     *
     * @author Sergiu Chirap
     * @version 1.1
     * @see com.unrecorded.database.entities.EGroupMember EGroupMember
     * @since PREVIEW
     */
    @Embeddable
    public static class GroupMemberId implements Serializable {

        /**
         * Unique identifier for a group in the context of membership.
         */
        @Column(name = "group_id", nullable = false)
        @NotNull
        private UUID groupId;

        /**
         * Unique identifier for a user in the context of membership.
         */
        @Column(name = "user_id", nullable = false)
        @NotNull
        private UUID userId;

        /**
         * Default constructor required by JPA.
         */
        public GroupMemberId() {
        }

        /**
         * Constructs a composite key using specified group ID and user ID.
         *
         * @param groupId The UUID representing the group.
         * @param userId  The UUID representing the user.
         */
        public GroupMemberId(@NotNull UUID groupId, @NotNull UUID userId) {
            this.groupId = groupId;
            this.userId = userId;
        }

        /**
         * Gets the group ID linked to this membership.
         *
         * @return The UUID for the groupID.
         */
        public @NotNull UUID getGroupId() {
            return groupId;
        }

        /**
         * Assigns a group ID to this membership key.
         *
         * @param groupId The group's UUID.
         */
        public void setGroupId(@NotNull UUID groupId) {
            this.groupId = groupId;
        }

        /**
         * Gets the user ID linked to this membership.
         *
         * @return The UUID for the userId.
         */
        public @NotNull UUID getUserId() {
            return userId;
        }

        /**
         * Assigns a user ID to this membership key.
         *
         * @param userId The user's UUID.
         */
        public void setUserId(@NotNull UUID userId) {
            this.userId = userId;
        }

        /**
         * Compares this GroupMemberId object with another to check for equality.
         *
         * @param o The object to compare against.
         * @return True if the objects are equal; otherwise, false.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GroupMemberId that)) return false;
            return Objects.equals(groupId, that.groupId) && Objects.equals(userId, that.userId);
        }

        /**
         * Computes a hash code for this GroupMemberId based on group and user IDs.
         *
         * @return The hash code as an integer.
         */
        @Override
        public int hashCode() {
            return MiscUtils.hash(groupId, userId);
        }

        /**
         * Provides a string representation of this GroupMemberId, showing group and user IDs.
         *
         * @return A string detailing this object.
         */
        @Override
        public String toString() {
            return String.format("// HibernateORM Entity 'Composite GroupMember Key':\n Group: %s\n User: %s //\n", groupId, userId);
        }
    }
}