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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * HibernateORM entity representing a message in the system.
 *
 * <p>This class maps to the "messages" table in the "unrecorded" schema and handles message exchanges
 * within the application.
 * It supports tracking user-to-user messages as well as messages sent within groups.
 * Additional features include soft deletion, timestamps for tracking, and encrypted content to ensure security.</p>
 *
 * <h2>Entity Relationships:</h2>
 * <ul>
 *   <li>Each message has a foreign key reference to the sender (`sender_id`) and
 *   an optional recipient (`recipient_id`) or group (`group_id`).</li>
 *   <li>Soft deletion is managed via the `deleted_sender` and `deleted_recipient` fields, enabling soft deletion tracking for audit purposes.</li>
 *   <li>Messages are associated with group conversations when the `is_group` flag is set to {@code true}.</li>
 * </ul>
 *
 * <p><b>Note:</b>
 * The schema enforces constraints to ensure either a recipient or group is set for each message, but not both.</p>
 *
 * @author Sergiu Chirap
 * @version 1.2
 * @see com.unrecorded.database.repositories.MessagePSQL MessagePSQL
 * @since PREVIEW
 */
@Entity
@Table(name = "messages", schema = "unrecorded")
public class EMessage {

    /**
     * Represents the unique identifier for a message within the system.
     * <p>This is generated by the database.</p>
     */
    @Id
    @Column(name = "message_id", updatable = false, nullable = false)
    @Nullable
    private UUID id = null;

    /**
     * Represents the unique identifier of the sender within the system.
     */
    @Column(name = "sender_id")
    @Nullable
    private UUID senderId;

    /**
     * Represents the unique identifier of the sender marked as deleted.
     * <p>Used for soft deletion semantics.</p>
     */
    @Column(name = "deleted_sender")
    @Nullable
    private UUID deletedSender;

    /**
     * Represents the unique identifier of the recipient within the system.
     * <p>Can be null when the message is sent to a group.</p>
     */
    @Column(name = "recipient_id")
    @Nullable
    private UUID recipientId;

    /**
     * Represents the unique identifier of the recipient marked as deleted.
     * <p>Used for soft deletion semantics.</p>
     */
    @Column(name = "deleted_recipient")
    @Nullable
    private UUID deletedRecipient;

    /**
     * Represents the unique identifier of the group to which the message is sent.
     * <p>Can be null when the message is sent to an individual.</p>>
     */
    @Column(name = "group_id")
    @Nullable
    private UUID groupId;

    /**
     * Indicates whether the message is part of a group conversation.
     * <p>Defaults to false.</p>>
     */
    @Column(name = "is_group", nullable = false)
    private boolean isGroup;

    /**
     * Stores the encrypted content of the message.
     */
    @Column(name = "content_encrypted", nullable = false)
    @NotNull
    private String contentEncrypted;

    /**
     * Stores the timestamp of when the message was created or last modified.
     * <p>Managed by database.</p>
     */
    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMPTZ")
    @NotNull
    private ZonedDateTime timestamp;

    /**
     * Indicates whether the message has been marked as deleted.
     * <p>Defaults to false.</p>
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * Default constructor required by JPA.
     */
    public EMessage() {
    }

    /**
     * Constructs a new EMessage instance with the specified details.
     *
     * @param senderId         The unique identifier of the sender.
     * @param recipientId      The unique identifier of the recipient (nullable for group messages).
     * @param groupId          The unique identifier of the group (nullable for individual messages).
     * @param isGroup          Whether the message is part of a group conversation.
     * @param contentEncrypted The encrypted content of the message.
     */
    public EMessage(@NotNull UUID senderId, @Nullable UUID recipientId, @Nullable UUID groupId, boolean isGroup, @NotNull String contentEncrypted) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.groupId = groupId;
        this.isGroup = isGroup;
        this.contentEncrypted = contentEncrypted;
    }

    /**
     * Retrieves the unique identifier.
     *
     * @return The {@code UUID} representing the unique identifier.
     */
    public @Nullable UUID getId() {
        return id;
    }

    /**
     * Retrieves the unique identifier of the sender.
     *
     * @return The {@code UUID} representing the sender's unique identifier.
     */
    public @Nullable UUID getSender() {
        return senderId;
    }

    /**
     * Sets the sender's unique identifier.
     *
     * @param senderId The {@code UUID} representing the sender's ID that will be set
     */
    public void setSender(@Nullable UUID senderId) {
        this.senderId = senderId;
    }

    /**
     * Retrieves the {@code UUID} of the deleted sender if available.
     *
     * @return The {@code UUID} of the deleted sender.
     */
    public @Nullable UUID getDeletedSender() {
        return deletedSender;
    }

    /**
     * Sets the deleted sender's unique identifier.
     *
     * @param deletedSender The {@code UUID} of the sender marked as deleted.
     */
    public void setDeletedSender(@Nullable UUID deletedSender) {
        this.deletedSender = deletedSender;
    }

    /**
     * Retrieves the unique identifier of the recipient.
     *
     * @return The {@code UUID} corresponding to the recipient's ID.
     */
    public @Nullable UUID getRecipientId() {
        return recipientId;
    }

    /**
     * Sets the recipient's unique identifier.
     *
     * @param recipientId The {@code UUID} of the recipient.
     */
    public void setRecipientId(@Nullable UUID recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * Retrieves the unique identifier of a deleted recipient.
     *
     * @return The {@code UUID} of the recipient if it has been deleted.
     */
    public @Nullable UUID getDeletedRecipient() {
        return deletedRecipient;
    }

    /**
     * Sets the deleted recipient identifier.
     *
     * @param deletedRecipient The {@code UUID} of the deleted recipient (nullable if no recipient is marked as deleted).
     */
    public void setDeletedRecipient(@Nullable UUID deletedRecipient) {
        this.deletedRecipient = deletedRecipient;
    }

    /**
     * Retrieves the unique identifier of the group.
     *
     * @return The {@code UUID} of the group (nullable if it is a direct message).
     */
    public @Nullable UUID getGroupId() {
        return groupId;
    }

    /**
     * Sets the unique identifier for the group.
     *
     * @param groupId The {@code UUID} of the group (nullable if it is a direct message).
     */
    public void setGroupId(@Nullable UUID groupId) {
        this.groupId = groupId;
    }

    /**
     * Determines if the current message is for a group.
     *
     * @return True if the message is for a group, false otherwise.
     */
    public boolean isGroup() {
        return isGroup;
    }

    /**
     * Sets the group status for the current message.
     *
     * @param group A boolean indicating if the message is for a group.
     */
    public void setGroup(boolean group) {
        isGroup = group;
    }

    /**
     * Retrieves the encrypted content.
     *
     * @return The encrypted content in string format.
     */
    public @NotNull String getContentEncrypted() {
        return contentEncrypted;
    }

    /**
     * Sets the encrypted content.
     *
     * @param contentEncrypted The encrypted content to set.
     */
    public void setContentEncrypted(@NotNull String contentEncrypted) {
        this.contentEncrypted = contentEncrypted;
    }

    /**
     * Retrieves the current timestamp.
     *
     * @return A {@code ZonedDateTime} representing the current timestamp.
     */
    public @NotNull ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if the current object is marked as deleted.
     *
     * @return True if the message is marked as deleted; false otherwise.
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Marks the current object as deleted by setting the isDeleted flag to true.
     */
    public void setDeleted() {
        isDeleted = true;
    }

    /**
     * Compares this EMessage object to the specified object to determine if they are equal.
     *
     * @param o The object to compare with this EMessage instance.
     * @return True if the specified object is equal to this EMessage, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EMessage that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(senderId, that.senderId) && Objects.equals(recipientId, that.recipientId) && Objects.equals(groupId, that.groupId) && Objects.equals(isGroup, that.isGroup) && Objects.equals(contentEncrypted, that.contentEncrypted) && Objects.equals(timestamp, that.timestamp) && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(deletedSender, that.deletedSender) && Objects.equals(deletedRecipient, that.deletedRecipient);
    }

    /**
     * Computes the hash code for this object using its significant attributes.
     *
     * @return An integer representing the hash code of this object.
     */
    @Override
    public int hashCode() {
        return MiscUtils.hash(id, senderId, deletedSender, recipientId, deletedRecipient, groupId, isGroup, contentEncrypted, timestamp, isDeleted);
    }

    /**
     * Returns a string representation of the Message entity.
     *
     * @return A formatted string containing message details.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM %sEntity 'Message':\n Id: %s\n Sender: %s\n Timestamp: %s\n", isDeleted ? "Soft-Deleted " : "", id, senderId, timestamp) + (isGroup ? String.format(" Group: %s\n", groupId) : String.format(" Recipient: %s\n", recipientId));
    }
}