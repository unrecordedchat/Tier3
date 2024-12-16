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
import com.unrecorded.database.util.HibernateUtil;
import com.unrecorded.database.util.LoggerUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This class manages operations related to user sessions, including creation, retrieval, updating, and deletion.
 *
 * <p><b>Purpose:</b> The `SessionPSQL` class handles session-centric CRUD (Create, Read, Update, Delete)
 * operations for a PostgreSQL database.
 * It integrates Hibernate ORM for database transactions and provides a secure mechanism for managing user sessions.</p>
 *
 * <h2>Main Features:</h2>
 * <ul>
 *   <li>Creation and management of user sessions using secure tokens.</li>
 *   <li>Ensures session validity through expiration checks.</li>
 *   <li>Efficient session retrieval by user ID or token.</li>
 *   <li>Field-specific queries tailored for session management workflows.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>This class uses thread-safe utilities like {@link HibernateUtil},
 * making it safe for use in multithreaded environments.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see ESession
 * @see HibernateUtil
 * @since 0.3
 */
public class SessionPSQL implements ISessionRepo {

    /**
     * Creates and saves a new session in the database.
     *
     * <p>This method validates session expiration timestamps and ensures data integrity before saving.
     * It uses Hibernate ORM to persist the session entity to the database.</p>
     *
     * @param userId    The unique identifier (UUID) of the user associated with this session.
     * @param token     The authentication token for the session.
     * @param expiresAt The expiration timestamp for the session.
     * @throws IllegalArgumentException If any of the input parameters are invalid.
     * @throws DataAccessException      If an issue occurs while persisting the session.
     */
    public void createSession(@NotNull UUID userId, @NotNull String token, @NotNull ZonedDateTime expiresAt) throws IllegalArgumentException, DataAccessException {
        LoggerUtil.logInfo("Creating a session for userId: {}, token: {}", userId.toString(), token);
        HibernateUtil.executeTransaction(true, session -> {
            ESession newSession = new ESession(userId, token, expiresAt);
            session.persist(newSession);
            LoggerUtil.logInfo("Session successfully created for userId: {}", userId.toString());
            return null;
        });
    }

    /**
     * Retrieves a session by its unique identifier (session ID).
     *
     * @param sessionId The unique identifier (UUID) of the session to retrieve.
     * @return The {@link ESession} entity corresponding to the session ID, or {@code null} if not found.
     * @throws DataAccessException If an issue occurs while querying the database.
     */
    public @Nullable ESession getSessionById(@NotNull UUID sessionId) throws DataAccessException {
        LoggerUtil.logInfo("Retrieving session by sessionId: {}", sessionId.toString());
        return HibernateUtil.executeTransaction(false, session -> session.find(ESession.class, sessionId));
    }

    /**
     * Retrieves sessions associated with a specific user.
     *
     * @param userId The unique identifier (UUID) of the user.
     * @return A list of {@link ESession} entities associated with the user.
     * Returns an empty list if no sessions are found.
     * @throws DataAccessException If an issue occurs while querying the database.
     */
    public @Nullable List<ESession> getSessionsByUserId(@NotNull UUID userId) throws DataAccessException {
        LoggerUtil.logInfo("Retrieving all sessions for userId: {}", userId.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ESession> query = builder.createQuery(ESession.class);
            Root<ESession> root = query.from(ESession.class);
            query.select(root).where(builder.equal(root.get("userId"), userId));
            List<ESession> sessions = session.createQuery(query).getResultList();
            LoggerUtil.logInfo("Found {} sessions for userId: {}", String.valueOf(sessions.size()), userId.toString());
            return sessions;
        });
    }

    /**
     * Retrieves a session using its token.
     *
     * @param token The session token.
     * @return The {@link ESession} entity corresponding to the token, or {@code null} if not found.
     * @throws DataAccessException If an issue occurs during the query process.
     */
    public @Nullable ESession getSessionByToken(@NotNull String token) throws DataAccessException {
        LoggerUtil.logInfo("Retrieving session by token.");
        return HibernateUtil.executeTransaction(false, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ESession> query = builder.createQuery(ESession.class);
            Root<ESession> root = query.from(ESession.class);
            query.select(root).where(builder.equal(root.get("token"), token));
            return session.createQuery(query).uniqueResult();
        });
    }

    /**
     * Deletes a session by its unique identifier (session ID).
     *
     * @param sessionId The unique identifier (UUID) of the session to delete.
     * @throws DataAccessException If an error occurs while deleting the session.
     */
    public void deleteSession(@NotNull UUID sessionId) throws DataAccessException {
        LoggerUtil.logInfo("Deleting session with sessionId: {}", sessionId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            ESession sessionEntity = session.find(ESession.class, sessionId);
            if (sessionEntity != null) {
                session.remove(sessionEntity);
                LoggerUtil.logInfo("Successfully deleted session with sessionId: {}", sessionId.toString());
            } else LoggerUtil.logWarn("No session found with sessionId: " + sessionId);
            return null;
        });
    }

    /**
     * Deletes all expired sessions from the database.
     *
     * @param currentTime The current timestamp to check for expiration.
     * @return The number of sessions deleted.
     * @throws DataAccessException If an issue occurs during the deletion process.
     */
    public boolean deleteExpiredSessions(@NotNull ZonedDateTime currentTime) throws DataAccessException {
        LoggerUtil.logInfo("Deleting expired sessions for current time: {}", currentTime.toString());
        return Boolean.TRUE.equals(HibernateUtil.executeTransaction(true, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ESession> query = builder.createQuery(ESession.class);
            Root<ESession> root = query.from(ESession.class);
            query.select(root).where(builder.lessThan(root.get("expiresAt"), currentTime));
            List<ESession> expiredSessions = session.createQuery(query).getResultList();
            expiredSessions.forEach(session::remove);
            LoggerUtil.logInfo("Successfully deleted {} expired sessions.", String.valueOf(expiredSessions.size()));
            return !expiredSessions.isEmpty();
        }));
    }
}