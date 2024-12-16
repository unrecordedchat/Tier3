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
 * HibernateORM entity representing a reaction to a message within the system.
 *
 * <p>This entity maps to the "reactions" table in the "unrecorded" schema and
 * represents user interactions with messages, such as likes or other emoji reactions.</p>
 *
 * <h2>Entity Relationships:</h2>
 * <ul>
 *   <li>Each reaction is associated with a specific user and message,
 *   tracked using a composite primary key (`ReactionId`).</li>
 *   <li>Reactions reference user entities (`user_id`) and message entities (`message_id`) as foreign keys.</li>
 * </ul>
 *
 * <p><b>Composite Primary Key:</b> The `ReactionId` class combines the user ID, message ID, and emoji,
 * guaranteeing that each user can only have one type of reaction (e.g., a single emoji) per message.</p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Supports user-to-message reactions with flexibility for different types of emojis.</li>
 *   <li>Designed with auditability and extensibility for various interactive features.</li>
 *   <li>Handles composite-key-based operations seamlessly using the embedded key `ReactionId`.</li>
 * </ul>
 *
 * <p><b>Note:</b> This entity enforces cross-entity consistency via foreign key constraints in the database schema.</p>
 *
 * @author Sergiu
 * @version 1.1
 * @see com.unrecorded.database.repositories.ReactionPSQL ReactionPSQL
 * @since PREVIEW
 */
@Entity
@Table(name = "reactions", schema = "unrecorded")
public class EReaction {

    /**
     * Represents the composite primary key for the entity.
     */
    @EmbeddedId
    @NotNull
    private ReactionId id;

    /**
     * Default constructor required by JPA.
     */
    public EReaction() {
    }

    /**
     * Constructs an EReaction object with a unique identifier that associates a user, a message, and a reaction type.
     *
     * @param userId    A unique identifier for the user expressing the reaction.
     * @param messageId A unique identifier for the message receiving the reaction.
     * @param emoji     The emoji reaction being expressed.
     */
    public EReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String emoji) {
        this.id = new ReactionId(userId, messageId, emoji);
    }

    /**
     * Retrieves the unique identifier for the reaction.
     *
     * @return The {@link ReactionId} associated with this instance.
     */
    public @NotNull ReactionId getId() {
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
        if (!(o instanceof EReaction that)) return false;
        return Objects.equals(id, that.id);
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
     * Represents a composite primary key for the {@link EReaction} entity, uniquely
     * identifying a user's reaction to a specific message in the system.
     *
     * <p>The composite key is composed of three attributes:</p>
     * <ul>
     *   <li>{@link #userId}: Identifies the user reacting to the message.</li>
     *   <li>{@link #messageId}: Identifies the message receiving the reaction.</li>
     *   <li>{@link #emoji}: Specifies the exact emoji reaction used.</li>
     * </ul>
     *
     * <h2>Purpose:</h2>
     * <ul>
     *   <li>Ensures uniqueness of reactions by combining user, message, and emoji details.</li>
     *   <li>Prevents duplicate same reactions by the same user on the same message.</li>
     *   <li>Serves as a flexible design for incorporating various reaction types (e.g., emojis) easily.</li>
     * </ul>
     *
     * <h2>Usage:</h2>
     * <ul>
     *   <li>This key is embedded in the {@link EReaction} class as a unique identifier.</li>
     *   <li>Supports JPA's persistence conventions for composite primary keys through the {@link Embeddable} annotation.</li>
     * </ul>
     * 
     * <h2>Cross-Entity Constraints:</h2>
     * <p>This composite key is also linked to referential integrity constraints in the database schema,
     * ensuring valid relationships between users, messages, and their reactions.</p>
     *
     * <b>Note:</b> This composite key supports extensibility for reaction-related features and future enhancements.
     *
     * @author Sergiu Chirap
     * @version 1.0
     * @see com.unrecorded.database.entities.EReaction
     * @since PREVIEW
     */
    @Embeddable
    public static class ReactionId implements Serializable {
        /**
         * Unique identifier for the user expressing the reaction.
         */
        @Column(name = "user_id", nullable = false)
        @NotNull
        private UUID userId;

        /**
         * Unique identifier for the message being reacted to.
         */
        @Column(name = "message_id", nullable = false)
        @NotNull
        private UUID messageId;

        /**
         * Emoji representing the specific reaction.
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
            if (!(o instanceof ReactionId that)) return false;
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
            return MiscUtils.hash(userId, messageId, emoji);
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