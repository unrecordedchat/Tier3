package com.unrecorded.database.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * HibernateORM entity representing a reaction to a message or post within the system.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since PREVIEW
 */
@Entity
@Table(name = "Reactions", schema = "unrecorded")
public class EReaction {

    /**
     * Represents the composite primary key for the entity.
     */
    @EmbeddedId
    private ReactionId id;
    
    /**
     * Default constructor required by JPA.
     */
    public EReaction() {
    }

    /**
     * Constructs an EReaction object with a unique identifier that associates a user, a message, and a reaction type.
     *
     * @param userId a unique identifier for the user expressing the reaction; cannot be null
     * @param messageId a unique identifier for the message receiving the reaction; cannot be null
     * @param type the type of the reaction being expressed (e.g., 'like', 'love'); cannot be null
     */
    public EReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String type) {
        this.id = new ReactionId(userId, messageId, type);
    }

    /**
     * Retrieves the unique identifier for the reaction.
     *
     * @return The {@link ReactionId} associated with this instance.
     */
    public ReactionId getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this instance of Reaction.
     *
     * @param id The {@link ReactionId} to be assigned as the identifier for this instance
     */
    public void setId(@NotNull ReactionId id) {
        this.id = id;
    }

    /**
     * Checks if this object is equal to the specified object.
     *
     * @param o The object to be compared for equality with this object
     * @return True if the specified object is equal to this object; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return id.equals(o);
        return id.equals(((EReaction) o).id);
    }

    /**
     * Generates a hash code for this object using its identifier.
     *
     * @return An integer hash code value based on the object's id.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns a string representation of the Reaction entity, including user ID, message ID, and emoji details.
     *
     * @return A formatted string containing the user ID, message ID, and emoji associated with this Reaction.
     */
    @Override
    public String toString() {
        return id.toString();
    }
    

    /**
     * Represents a composite key for the Reaction entity, encompassing user ID, message ID, and the emoji used in the reaction.
     *
     * @author Sergiu Chirap
     * @version 1.0
     * @see EReaction
     * @since PREVIEW
     */
    @Embeddable
    public static class ReactionId implements Serializable {
        /**
         * Represents the unique identifier for a user within a reaction context.
         */
        @Column(name = "userId", nullable = false)
        @NotNull
        private UUID userId;

        /**
         * Represents the unique identifier for a message within a reaction context.
         */
        @Column(name = "messageId", nullable = false)
        @NotNull
        private UUID messageId;

        /**
         * Represents the emoji associated with the reaction of a message.
         */
        @Column(name = "emoji", nullable = false)
        @NotNull
        private String emoji;

        /**
         * Default constructor required by JPA.
         */
        public ReactionId() {
        }

        /**
         * Constructs a ReactionId object with the specified user ID, message ID, and emoji.
         *
         * @param userId    The UUID representing the user who reacted.
         * @param messageId The UUID representing the message to which the reaction is made.
         * @param emoji     The emoji used in the reaction as a String.
         */
        public ReactionId(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String emoji) {
            this.userId = userId;
            this.messageId = messageId;
            this.emoji = emoji;
        }

        /**
         * Retrieves the user ID associated with this reaction.
         *
         * @return The UUID representing the user ID.
         */
        public @NotNull UUID getUserId() {
            return userId;
        }

        /**
         * Sets the user ID associated with this reaction.
         *
         * @param userId The UUID representing the user ID to be set.
         */
        public void setUserId(@NotNull UUID userId) {
            this.userId = userId;
        }

        /**
         * Retrieves the message ID associated with this reaction.
         *
         * @return The UUID representing the message ID.
         */
        public @NotNull UUID getMessageId() {
            return messageId;
        }

        /**
         * Sets the message ID associated with the reaction.
         *
         * @param messageId The UUID representing the message ID to be set.
         */
        public void setMessageId(@NotNull UUID messageId) {
            this.messageId = messageId;
        }

        /**
         * Retrieves the emoji associated with the reaction.
         *
         * @return The emoji of the reaction as a String.
         */
        public @NotNull String getEmoji() {
            return emoji;
        }

        /**
         * Sets the emoji of the reaction.
         *
         * @param type The emoji of the reaction as a String.
         */
        public void setEmoji(@NotNull String type) {
            this.emoji = type;
        }

        /**
         * Compares this ReactionId object to the specified object for equality.
         *
         * @param o The object to be compared for equality with this ReactionId
         * @return True if the specified object is equal to this ReactionId, otherwise false
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReactionId that = (ReactionId) o;
            return Objects.equals(userId, that.userId) && Objects.equals(messageId, that.messageId) && Objects.equals(emoji, that.emoji);
        }

        /**
         * Computes the hash code for the current instance of the ReactionId class.
         *
         * @return An integer representing the hash code value computed based on userId, messageId,
         * and type fields of this ReactionId instance.
         */
        @Override
        public int hashCode() {
            return Objects.hash(userId, messageId, emoji);
        }

        /**
         * Returns a string representation of the Reaction entity, including user ID, message ID, and emoji details.
         *
         * @return A formatted string containing the user ID, message ID, and emoji associated with this Reaction.
         */
        @Override
        public String toString() {
            return String.format("// HibernateORM Entity 'Reaction':\n  User: %s\n  Message: %s\n  Emoji: %s //\n", userId, messageId, emoji);
        }
    }
}