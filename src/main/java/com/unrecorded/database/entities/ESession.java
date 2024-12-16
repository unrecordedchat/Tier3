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
 * HibernateORM entity representing a user session in the system.
 *
 * <p>This entity maps to the "sessions" table in the "unrecorded" schema
 * and handles the lifecycle of user sessions, including session authentication and expiration.</p>
 *
 * <h2>Entity Purpose:</h2>
 * <ul>
 *   <li>Stores session-level details per user, such as tokens and expiration timestamps.</li>
 *   <li>Supports secure user authentication workflows through session validation.</li>
 * </ul>
 *
 * <h2>Entity Relationships:</h2>
 * <ul>
 *   <li>Each session is uniquely associated with a user (`user_id`) in the system.</li>
 *   <li>The session relationship is enforced via a foreign key constraint at the database level.</li>
 *   <li>Tokens (`token`) are assigned to uniquely identify a validated user session.</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Tracks session identifiers (`session_id`) as unique database-generated UUIDs.</li>
 *   <li>Includes automatic expiration logic through the `expires_at` timestamp.</li>
 *   <li>Designed to handle secure, scalable authentication and user management workflows.</li>
 * </ul>
 *
 * <p><b>Note:</b> The session expiration is application-specific and may involve periodic validation of the `expires_at` timestamp during active workflows.</p>
 *
 * @version 1.1
 * @author Sergiu
 * @see com.unrecorded.database.repositories.SessionPSQL SessionPSQL
 * @since v0.2
 */
@Entity
@Table(name = "sessions", schema = "unrecorded")
public class ESession {

    /**
     * Represents the unique identifier for a session.
     * <p>This value is used for database operations and is generated automatically.</p>
     */
    @Id
    @Column(name = "session_id", nullable = false, updatable = false)
    @Nullable
    private UUID id;

    /**
     * Represents the unique identifier for the user associated with this session.
     * <p>This value acts as a foreign key, linking this session to a specific user in the system.</p>
     */
    @Column(name = "user_id", nullable = false)
    @NotNull
    private UUID userId;

    /**
     * Represents the token assigned to this session.
     * <p>This value is used to authenticate the session during system interactions.</p>
     */
    @Column(name = "token", nullable = false)
    @NotNull
    private String token;

    /**
     * Represents the expiration time of the session.
     * <p>Once this timestamp is reached, the session is considered invalid.</p>
     */
    @Column(name = "expires_at", nullable = false)
    @NotNull
    private ZonedDateTime expiresAt;

    /**
     * Default constructor required by JPA.
     */
    public ESession() {
    }

    /**
     * Constructs a new ESession with the specified user ID, token, and expiration time.
     *
     * @param userId    The unique identifier for the user, which cannot be null.
     * @param token     The session token, which cannot be null.
     * @param expiresAt The expiration time of the session, which cannot be null.
     */
    public ESession(@NotNull UUID userId, @NotNull String token, @NotNull ZonedDateTime expiresAt) {
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    /**
     * Retrieves the session ID.
     *
     * @return The UUID representing the session ID.
     */
    public @Nullable UUID getId() {
        return id;
    }

    /**
     * Retrieves the user ID associated with this session.
     *
     * @return The UUID representing the user ID.
     */
    public @NotNull UUID getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with this session.
     *
     * @param userId The UUID representing the user ID to be set.
     */
    public void setUserId(@NotNull UUID userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the session token.
     *
     * @return The token of the session as a String.
     */
    public @NotNull String getToken() {
        return token;
    }

    /**
     * Sets the session token.
     *
     * @param token The token of the session as a String.
     */
    public void setToken(@NotNull String token) {
        this.token = token;
    }

    /**
     * Retrieves the expiration time of the session.
     *
     * @return The OffsetDateTime representing the expiration time.
     */
    public @NotNull ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Sets the expiration time of the session.
     *
     * @param expiresAt The OffsetDateTime representing the expiration time to be set.
     */
    public void setExpiresAt(@NotNull ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
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
        if (!(o instanceof ESession that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(token, that.token) && Objects.equals(expiresAt, that.expiresAt);
    }

    /**
     * Generates a hash code for this object using its identifier and properties.
     *
     * @return An integer hash code value based on the object's id and properties.
     */
    @Override
    public int hashCode() {
        return MiscUtils.hash(id, userId, token, expiresAt);
    }

    /**
     * Returns a string representation of the Session entity, including session ID, user ID, token, and expiration time.
     *
     * @return A formatted string containing the session ID, user ID, token, and expiration time associated with this Session.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM Entity 'Session':\n Session ID: %s\n User ID: %s\n Token: %s\n Expires At: %s //\n", id, userId, token, expiresAt);
    }
}