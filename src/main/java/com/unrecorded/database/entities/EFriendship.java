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

import com.unrecorded.database.util.MultiTools;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * HibernateORM entity representing a friendship relationship between two users in the system.
 *
 * <p>This entity captures the connections between users, enabling functionalities such as social interactions
 * and network building within the application.
 * Each friendship is uniquely identified by a composite key consisting of two user UUIDs.</p>
 *
 * <h2>Entity Relationships:</h2>
 * <ul>
 *   <li>Involves the {@code EUser} entity as the source and target of friendships,
 *   representing user-to-user connections.</li>
 *   <li>The status of each friendship (e.g., Friend, Unknown, Pending) facilitates dynamic social networking features.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 1.1
 * @see com.unrecorded.database.repositories.FriendshipPSQL FriendshipPSQL
 * @since PREVIEW
 */
@Entity
@Table(name = "friendships", schema = "unrecorded")
public class EFriendship {

    /**
     * Represents the embedded composite primary key for the Friendship entity.
     * <p>The {@link FriendshipId} class contains the identifiers of the two users involved in the friendship.</p>
     */
    @EmbeddedId
    @NotNull
    private FriendshipId id;

    /**
     * Represents the status of the friendship, such as 'FRD' (Friend), 'UNK' (Unknown), 'PND' (Pending).
     * <p>It governs the current state of the relationship between two users.</p>
     */
    @Column(name = "status", nullable = false, length = 3)
    @NotNull
    private String status;

    /**
     * Default constructor required by JPA.
     */
    public EFriendship() {
    }

    /**
     * Constructs an EFriendship instance representing a friendship between two specified users.
     *
     * @param userId1 The UUID of the first user involved in the friendship.
     * @param userId2 The UUID of the second user involved in the friendship.
     * @param status  The status of the friendship.
     */
    public EFriendship(@NotNull UUID userId1, @NotNull UUID userId2, @NotNull String status) {
        this.id = new FriendshipId(userId1, userId2);
        this.status = status;
    }

    /**
     * Retrieves the unique identifier for the friendship.
     *
     * @return The {@link FriendshipId} representing the unique composite identifier for this friendship.
     */
    public @NotNull FriendshipId getId() {
        return id;
    }

    /**
     * Sets the composite primary key for this friendship.
     *
     * @param id The {@link FriendshipId} to be set, representing the unique identifiers of both users.
     */
    public void setId(@NotNull FriendshipId id) {
        this.id = id;
    }

    /**
     * Retrieves the current status of the friendship.
     *
     * @return A string representation of the current status.
     */
    public @NotNull String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the friendship.
     *
     * @param status The new status to assign to the friendship.
     */
    public void setStatus(@NotNull String status) {
        this.status = status;
    }

    /**
     * Compares this EFriendship object with another for equality.
     *
     * @param o The object to compare with this instance.
     * @return True if the specified object is equal to this instance, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EFriendship that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(status, that.status);
    }

    /**
     * Computes the hash code value for this object based on its primary key and status.
     *
     * @return An integer representing the hash code.
     */
    @Override
    public int hashCode() {
        return MultiTools.hash(id, status);
    }

    /**
     * Returns a string representation of the Friendship entity's current state.
     *
     * @return A formatted string detailing both user identifiers and their friendship status.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM Entity 'Friendship':\n User 1: %s\n User 2: %s\n Status: %s //\n", id.userId1, id.userId2, status);
    }
    
    /**
     * Represents a composite primary key for the `EFriendship` entity, uniquely
     * identifying a friendship between two users in the system.
     *
     * <p>This key is composed of two UUIDs: {@code userId1} and {@code userId2}, which
     * represent the two users involved in the friendship.
     * The combination of these two identifiers ensures that each friendship is unique and accurately represents
     * the relationship.</p>
     *
     * <h2>Purpose:</h2>
     * <ul>
     *   <li>Ensures that a friendship is uniquely identified by its two participants.</li>
     *   <li>Prevents duplicate friendships between the same pair of users.</li>
     * </ul>
     *
     * <h2>Key Usage:</h2>
     * <ul>
     *   <li>{@link #userId1}: Represents the UUID of the first user in the relationship.</li>
     *   <li>{@link #userId2}: Represents the UUID of the second user in the relationship.</li>
     * </ul>
     *
     * <h2>Note:</h2>
     * <p>Despite the order of `userId1` and `userId2`, the database treats the pair as a unique combination,
     *  ensuring the uniqueness of the friendship regardless of which user is listed first.</p>
     *
     * @author Sergiu Chirap
     * @version 1.1
     * @see com.unrecorded.database.entities.EFriendship EFriendship
     * @since PREVIEW
     */
    @Embeddable
    public static class FriendshipId implements java.io.Serializable {

        /**
         * The unique identifier for the first user involved in the friendship.
         */
        @Column(name = "user_id_1", nullable = false)
        @NotNull
        private UUID userId1;

        /**
         * The unique identifier for the second user involved in the friendship.
         */
        @Column(name = "user_id_2", nullable = false)
        @NotNull
        private UUID userId2;

        /**
         * Default constructor required by JPA.
         */
        public FriendshipId() {
        }

        /**
         * Constructs a composite key for two users involved in the friendship.
         *
         * @param userId1 The unique identifier for the first user.
         * @param userId2 The unique identifier for the second user.
         */
        public FriendshipId(@NotNull UUID userId1, @NotNull UUID userId2) {
            this.userId1 = userId1;
            this.userId2 = userId2;
        }

        /**
         * Retrieves the UUID of the first user in the friendship.
         *
         * @return The UUID of the first user.
         */
        public @NotNull UUID getUserId1() {
            return userId1;
        }

        /**
         * Sets the UUID for the first user involved in the friendship.
         *
         * @param userId1 The UUID for the first user.
         */
        public void setUserId1(@NotNull UUID userId1) {
            this.userId1 = userId1;
        }

        /**
         * Retrieves the UUID of the second user in the friendship.
         *
         * @return The UUID of the second user.
         */
        public @NotNull UUID getUserId2() {
            return userId2;
        }

        /**
         * Sets the UUID for the second user involved in the friendship.
         *
         * @param userId2 The UUID for the second user.
         */
        public void setUserId2(@NotNull UUID userId2) {
            this.userId2 = userId2;
        }

        /**
         * Compares this FriendshipId object to another object for equality.
         *
         * @param o The object to compare with this instance.
         * @return True if both objects are equal, false otherwise.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FriendshipId that)) return false;
            return Objects.equals(userId1, that.userId1) && Objects.equals(userId2, that.userId2);
        }

        /**
         * Generates a hash code for the FriendshipId.
         *
         * @return The hash code calculated from userId1 and userId2.
         */
        @Override
        public int hashCode() {
            return MultiTools.hash(userId1, userId2);
        }

        /**
         * Provides a string representation of this composite friendship key.
         *
         * @return A structured string displaying the user IDs.
         */
        @Override
        public String toString() {
            return String.format("// HibernateORM Entity 'Composite Friendship Key':\n User 1: %s\n User 2: %s //\n", userId1, userId2);
        }
    }
}