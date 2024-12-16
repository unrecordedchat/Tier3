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

import com.unrecorded.database.entities.ESession;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Interface defining session-related database operations.
 *
 * <p>The {@code ISessionRepo} interface specifies methods for interacting with
 * {@link ESession} entities, providing CRUD functionality and session-management-specific
 * operations.</p>
 *
 * <h2>Main Features:</h2>
 * <ul>
 *   <li>CRUD operations to manage session entities in the database.</li>
 *   <li>Session lookup by user, token, or expiration status.</li>
 *   <li>Utility methods for session validation and cleanup.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>All implementations must ensure thread-safe operations for use in concurrent environments.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since 0.3
 */
public interface ISessionRepo {

    /**
     * Creates a new session in the database.
     *
     * @param userId    The unique identifier (UUID) of the user associated with this session.
     * @param token     The authentication token for the session.
     * @param expiresAt The expiration timestamp for the session.
     * @throws IllegalArgumentException If validation fails for any input parameter.
     * @throws DataAccessException      If an issue occurs while saving the session to the database.
     */
    void createSession(@NotNull UUID userId, @NotNull String token, @NotNull ZonedDateTime expiresAt)
            throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves a session using the session's unique identifier (UUID).
     *
     * @param sessionId The unique identifier of the session.
     * @return The {@link ESession} corresponding to the session ID, or {@code null} if not found.
     * @throws DataAccessException If an issue occurs during the lookup process.
     */
    @Nullable ESession getSessionById(@NotNull UUID sessionId) throws DataAccessException;

    /**
     * Retrieves all sessions associated with a specific user.
     *
     * @param userId The unique identifier (UUID) of the user.
     * @return A list of {@link ESession} entities associated with the user.
     * Returns an empty list if no sessions are found.
     * @throws DataAccessException If an issue occurs during the lookup process.
     */
    @Nullable List<ESession> getSessionsByUserId(@NotNull UUID userId) throws DataAccessException;

    /**
     * Retrieves a session using its token.
     *
     * @param token The token associated with the session.
     * @return The {@link ESession} entity corresponding to the token, or {@code null} if not found.
     * @throws DataAccessException If an issue occurs during the lookup process.
     */
    @Nullable ESession getSessionByToken(@NotNull String token) throws DataAccessException;

    /**
     * Deletes a session using its unique identifier.
     *
     * @param sessionId The unique identifier (UUID) of the session to be deleted.
     * @throws DataAccessException If an issue occurs while deleting the session.
     */
    void deleteSession(@NotNull UUID sessionId) throws DataAccessException;

    /**
     * Deletes all expired sessions based on the provided current timestamp.
     *
     * @param currentTime The current time used to check for expired sessions.
     * @return {@code true} if any sessions were deleted, or {@code false} otherwise.
     * @throws DataAccessException If an issue occurs while deleting expired sessions.
     */
    boolean deleteExpiredSessions(@NotNull ZonedDateTime currentTime) throws DataAccessException;
}