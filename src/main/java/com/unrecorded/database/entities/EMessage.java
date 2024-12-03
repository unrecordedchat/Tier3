package com.unrecorded.database.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.time.ZonedDateTime;

/**
 * HibernateORM entity representing a message in the system.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see com.unrecorded.database.repositories.MessagePSQL MessagePSQL
 * @since PREVIEW
 */
@Entity
@Table(name = "Messages")
public class EMessage {

    /**
     * Represents the unique identifier for a message within the system.
     */
    @Id
    @Column(name = "messageId", updatable = false, nullable = false)
    @Nullable
    private UUID messageId = null;

    /**
     * Represents the unique identifier of the sender within the system.
     */
    @Column(name = "senderId", nullable = false)
    @NotNull
    private UUID senderId;

    /**
     * Represents the unique identifier of the recipient within the system.
     */
    @Column(name = "recipientId", nullable = false)
    @NotNull
    private UUID recipientId;

    /**
     * Indicates whether the message is part of a group conversation.
     */
    @Column(name = "isGroup", nullable = false)
    private boolean isGroup;

    /**
     * Stores the encrypted content of the message.
     */
    @Column(name = "contentEncrypted", nullable = false)
    @NotNull
    private String contentEncrypted;

    /**
     * Stores the timestamp of when the message was created or last modified.
     */
    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMPTZ")
    @NotNull
    private ZonedDateTime timestamp;

    /**
     * Indicates whether the message has been marked as deleted.
     */
    @Column(name = "isDeleted", nullable = false)
    private boolean isDeleted;
    

    public @Nullable UUID getMessageId() {
        return messageId;
    }

    public @NotNull UUID getSender() {
        return senderId;
    }

    public void setSender(@NotNull UUID senderId) {
        this.senderId = senderId;
    }

    public @NotNull UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(@NotNull UUID recipientId) {
        this.recipientId = recipientId;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public @NotNull String getContentEncrypted() {
        return contentEncrypted;
    }

    public void setContentEncrypted(@NotNull String contentEncrypted) {
        this.contentEncrypted = contentEncrypted;
    }

    public @NotNull ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NotNull ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}