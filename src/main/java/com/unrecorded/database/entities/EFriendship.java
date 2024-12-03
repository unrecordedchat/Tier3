package com.unrecorded.database.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * HibernateORM entity representing a friendship relationship between two users.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since PREVIEW
 */
@Entity
@Table(name = "Friendships", schema = "unrecorded")
public class EFriendship {

    /**
     * Represents the embedded composite primary key for the Friendship entity.
     */
    @EmbeddedId
    private FriendshipId id;

    /**
     * Represents the status of an entity or process.
     */
    @Column(name = "status", nullable = false)
    @NotNull
    private String status;

    /**
     * Default constructor required by JPA.
     */
    public EFriendship() {
    }

    /**
     * Constructs an EFriendship object which represents a friendship between two users identified by their UUIDs.
     *
     * @param userId1 The UUID of the first user involved in the friendship; must not be null
     * @param userId2 The UUID of the second user involved in the friendship; must not be null
     * @param status The status of the friendship; must not be null
     */
    public EFriendship(@NotNull UUID userId1, @NotNull UUID userId2, @NotNull String status) {
        this.id = new FriendshipId(userId1, userId2);
        this.status = status;
    }

    /**
     * Retrieves the unique identifier for the friendship.
     *
     * @return The FriendshipId representing this friendship's unique identifier.
     */
    public FriendshipId getId() {
        return id;
    }

    /**
     * Sets the ID for this friendship instance.
     *
     * @param id The FriendshipId to be set
     */
    public void setId(FriendshipId id) {
        this.id = id;
    }

    /**
     * Retrieves the current status as string.
     *
     * @return The current status
     */
    public @NotNull String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the friendship.
     *
     * @param status The new status to be assigned to the friendship.
     */
    public void setStatus(@NotNull String status) {
        this.status = status;
    }

    /**
     * Compares this EFriendship object to the specified object for equality.
     *
     * @param o The object to compare with this EFriendship instance
     * @return True if the specified object is equal to this EFriendship, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EFriendship that = (EFriendship) o;
        return id.equals(that.id) && status.equals(that.status);
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return The hash code value for this object, as an integer.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }

    /**
     * Returns a string representation of the Friendship entity.
     *
     * @return A formatted string representing the state of the Friendship entity,
     *         including user identifiers and their status.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM Entity 'Friendship':\n  User 1: %s\n  User 2: %s\n  Status: %s //\n", id.userId1, id.userId2, status);
    }

    /**
     * Represents a composite primary key for the Friendship entity, capturing a unique
     * friendship relationship between two users identified by their UUIDs.
     *
     * @author Sergiu Chirap
     * @version 1.0
     * @since PREVIEW
     */
    @Embeddable
    public static class FriendshipId implements java.io.Serializable {

        /**
         * Represents the unique identifier for the first user involved in the friendship relationship.
         * <p>This UUID is part of the composite primary key for the Friendship entity, ensuring the uniqueness of the friendship pair.
         */
        @Column(name = "userId1", nullable = false)
        @NotNull
        private UUID userId1;

        /**
         * Represents the unique identifier for the second user involved in the friendship relationship.
         * <p>This UUID is part of the composite primary key for the Friendship entity, ensuring the uniqueness of the friendship pair.
         */
        @Column(name = "userId2", nullable = false)
        @NotNull
        private UUID userId2;

        /**
         * Default constructor required by JPA.
         */
        public FriendshipId() {
        }

        /**
         * Constructs a new composite key for the Friendship entity using two user UUIDs.
         *
         * @param userId1 The unique identifier for the first user in the friendship
         * @param userId2 The unique identifier for the second user in the friendship
         */
        public FriendshipId(@NotNull UUID userId1, @NotNull UUID userId2) {
            this.userId1 = userId1;
            this.userId2 = userId2;
        }

        /**
         * Retrieves the unique identifier for the first user involved in the friendship relationship.
         *
         * @return The UUID representing the first user in the friendship relationship.
         */
        public @NotNull UUID getUserId1() {
            return userId1;
        }

        /**
         * Sets the unique identifier for the first user involved in the friendship relationship.
         *
         * @param userId1 The UUID representing the first user in the friendship relationship.
         */
        public void setUserId1(@NotNull UUID userId1) {
            this.userId1 = userId1;
        }

        /**
         * Retrieves the unique identifier for the second user involved in the friendship relationship.
         *
         * @return The UUID representing the second user in the friendship relationship.
         */
        public @NotNull UUID getUserId2() {
            return userId2;
        }

        /**
         * Sets the unique identifier for the second user involved in the friendship relationship.
         *
         * @param userId2 The UUID representing the second user in the friendship relationship.
         */
        public void setUserId2(@NotNull UUID userId2) {
            this.userId2 = userId2;
        }

        /**
         * Compares this FriendshipId object to another object for equality.
         * <p>The comparison is based on the equality of userId1 and userId2 fields.
         *
         * @param o The object to compare with this FriendshipId for equality
         * @return True if the specified object is also a FriendshipId and both userId1
         * and userId2 are equal; false otherwise
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FriendshipId that)) return false;
            return userId1.equals(that.userId1) && userId2.equals(that.userId2);
        }

        /**
         * Generates a hash code for the FriendshipId object.
         *
         * @return A hash code value for this FriendshipId, which is computed based
         * on the hash codes of userId1 and userId2.
         */
        @Override
        public int hashCode() {
            return Objects.hash(userId1, userId2);
        }

        /**
         * Returns a string representation of the composite friendship key in a formatted manner.
         *
         * @return A formatted string detailing the user IDs of the composite friendship key.
         */
        @Override
        public String toString() {
            return String.format("// HibernateORM Entity 'Composite Friendship Key':\n  User 1: %s\n  User 2: %s //\n", userId1, userId2);
        }
    }
}