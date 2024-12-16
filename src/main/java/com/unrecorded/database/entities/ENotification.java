﻿/*
 * VIA University College - School of Technology and Business
 * Software Engineering Program - 3rd Semester Project
 *
 * This work is a part of the academic curriculum for the Software Engineering program at VIA University College, Denmark.
 * It is intended only for educational and academic purposes.
 *
 * No part of this project may be reproduced or transmitted in any form or by any means, except as permitted by VIA University and the course instructor.
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
 * HibernateORM entity representing a notification in the system.
 *
 * <p>This class maps to the "notifications" table in the "unrecorded" schema, keeping track of system-generated notifications
 * for application users. Each notification contains metadata such as the type, content, and read status, along with
 * timestamps for auditing purposes.</p>
 *
 * <p><b>Note:</b> This entity plays a key role in providing timely updates and alerts to users while supporting
 * extensibility for future notification types like push notifications or external integrations.</p>
 *
 * <h2>Entity Relationships:</h2>
 * <ul>
 *   <li>Each notification is assigned to an individual user via the {@code user_id} field (foreign key).</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 1.1
 * @see com.unrecorded.database.repositories.NotificationPSQL NotificationPSQL
 * @since v0.2
 */
@Entity
@Table(name = "notifications", schema = "unrecorded")
public class ENotification {

    /**
     * Represents the unique identifier for the notification.
     * <p>This value is generated by the database.</p>
     */
    @Id
    @Column(name = "notification_id", nullable = false, updatable = false)
    @Nullable
    private UUID id;

    /**
     * Represents the unique identifier of the user receiving the notification.
     */
    @Column(name = "user_id", nullable = false)
    @NotNull
    private UUID userId;

    /**
     * Represents the type of the notification.
     */
    @Column(name = "type", nullable = false, length = 15)
    @NotNull
    private String type;

    /**
     * Represents the content of the notification.
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @NotNull
    private String content;

    /**
     * Indicates whether the notification has been read.
     */
    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    /**
     * Represents the timestamp of when the notification was created.
     */
    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMPTZ")
    @NotNull
    private ZonedDateTime timestamp;

    /**
     * Default constructor required by JPA.
     */
    public ENotification() {
    }

    /**
     * Constructs a new ENotification with the specified attributes.
     *
     * @param userId    The UUID of the user receiving the notification.
     * @param type      The type of notification.
     * @param content   The content of the notification.
     * @param isRead    The read status of the notification.
     * @param timestamp The timestamp for the notification.
     */
    public ENotification(@NotNull UUID userId, @NotNull String type, @NotNull String content, boolean isRead, @NotNull ZonedDateTime timestamp) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    /**
     * Retrieves the notification ID.
     *
     * @return The UUID of the notification.
     */
    public @Nullable UUID getId() {
        return id;
    }

    /**
     * Retrieves the user ID for whom this notification was intended.
     *
     * @return The UUID of the user.
     */
    public @NotNull UUID getUserId() {
        return userId;
    }

    /**
     * Sets the user ID for whom this notification is intended.
     *
     * @param userId The UUID of the user.
     */
    public void setUserId(@NotNull UUID userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the type of this notification.
     *
     * @return The type of the notification as a String.
     */
    public @NotNull String getType() {
        return type;
    }

    /**
     * Sets the type of this notification.
     *
     * @param type The type of the notification as a String.
     */
    public void setType(@NotNull String type) {
        this.type = type;
    }

    /**
     * Retrieves the content of this notification.
     *
     * @return The content of the notification as a String.
     */
    public @NotNull String getContent() {
        return content;
    }

    /**
     * Sets the content of this notification.
     *
     * @param content The content of the notification as a String.
     */
    public void setContent(@NotNull String content) {
        this.content = content;
    }

    /**
     * Checks if this notification has been read.
     *
     * @return True if the notification has been read, false otherwise.
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the read status of this notification.
     *
     * @param read True if the notification is read, false otherwise.
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Retrieves the timestamp of this notification.
     *
     * @return The timestamp as an ZonedDateTime when the notification was created.
     */
    public @NotNull ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if this object is equal to the specified object.
     *
     * @param o The object to be compared for equality with this object.
     * @return True if the specified object is equal to this object; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ENotification that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(type, that.type) && Objects.equals(content, that.content) && Objects.equals(timestamp, that.timestamp) && Objects.equals(isRead, that.isRead);
    }

    /**
     * Generates a hash code for this object using its identifier and properties.
     *
     * @return An integer hash code value based on the object's id and properties.
     */
    @Override
    public int hashCode() {
        return MiscUtils.hash(id, userId, type, content, isRead, timestamp);
    }

    /**
     * Returns a string representation of the Notification entity, including notification ID, user ID, type, content, read status, and timestamp.
     *
     * @return A formatted string containing the notification ID, user ID, type, content, read status, and timestamp associated with this Notification.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM Entity 'Notification':\n Id: %s\n User ID: %s\n Type: %s\n Content: %s\n Read: %b\n Timestamp: %s //\n", id, userId, type, content, isRead, timestamp);
    }
}