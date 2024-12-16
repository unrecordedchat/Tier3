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

package com.unrecorded.database.util;

import com.unrecorded.database.DBA;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.exceptions.TypeOfDAE;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Utility class designed for managing Hibernate-based database operations within the system.
 *
 * <p>This class provides streamlined methods for executing transactional and non-transactional
 * database operations. By handling the creation, transaction management, and closure of Hibernate sessions,
 * it abstracts much of the boilerplate code required for database interaction.</p>
 *
 * <p><b>Purpose:</b> The `HibernateUtil` class is a safe and reusable utility core to the system’s
 * data access layer. It accommodates both read and write operations by managing database sessions,
 * rolling back transactions when necessary, and interpreting exceptions as domain-level errors
 * for consistency throughout the application.</p>
 *
 * <h2>Main Features:</h2>
 * <ul>
 *   <li>Abstracts session and transaction management for Hibernate-based operations.</li>
 *   <li>Supports both transactional (write) and non-transactional (read) database interactions.</li>
 *   <li>Handles error propagation by encapsulating persistence-related errors as {@link DataAccessException}.</li>
 *   <li>Leverages {@link LoggerUtil} for structured and secure logging of operations and exceptions.</li>
 *   <li>Ensures consistent exception handling, including transaction rollbacks on failure.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>{@code
 * // Example 1: Fetch a single user by ID (non-transactional)
 * UUID userId = ...;
 * EUser user = HibernateUtil.executeTransaction(false, session ->
 *     session.get(EUser.class, userId)
 * );
 *
 * // Example 2: Save a new user (transactional)
 * HibernateUtil.executeTransaction(true, session -> {
 *     EUser newUser = new EUser(...);
 *     session.persist(newUser);
 *     return null;
 * });
 * }</pre>
 *
 * <h2>Error Handling:</h2>
 * <ul>
 *   <li>During a transactional operation, in case of failure, the transaction is automatically rolled back.</li>
 *   <li>Any unexpected Hibernate `PersistenceException` or runtime errors are re-thrown as {@link DataAccessException}.</li>
 *   <li>The {@code DataAccessException} includes details about the error type ({@link TypeOfDAE}) and
 *       provides diagnostic information about the exception.</li>
 * </ul>
 *
 * <p><b>Thread Safety:</b> This utility class is inherently thread-safe, as each method invocation
 * creates and manages its own distinct Hibernate session instance.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see com.unrecorded.database.exceptions.DataAccessException DataAccessException
 * @since 0.3
 */
public class HibernateUtil {

    /**
     * Executes a database action within an optional transaction.
     *
     * <p>This method provides a uniform way to interact with the database, abstracting away
     * the details of session management and transaction handling. Depending on the value of
     * {@code requiresTransaction}, a transaction is either created or omitted.</p>
     *
     * <h3>Description:</h3>
     * <ul>
     *   <li>If {@code requiresTransaction} is {@code true}, the method begins a transaction
     *       before executing the provided action.</li>
     *   <li>The caller must provide a {@link Function} that defines the database operation to
     *       perform within the session. This function is applied to an active {@link Session}.</li>
     *   <li>The session is automatically closed when the operation completes, even in the case of an exception.</li>
     * </ul>
     *
     * <h3>Typical Use Cases:</h3>
     * <ul>
     *   <li><b>Non-transactional:</b> Fetch or query data from the database without modifying it
     *       (e.g., retrieving an entity).</li>
     *   <li><b>Transactional:</b> Perform create, update, or delete operations that require transactional guarantees.</li>
     * </ul>
     *
     * <h3>Behavior:</h3>
     * <ul>
     *   <li>If the provided action completes successfully:
     *     <ul>
     *       <li>The result of the action is returned to the caller.</li>
     *       <li>The transaction (if started) is committed.</li>
     *     </ul>
     *   </li>
     *   <li>If the action throws an exception:
     *     <ul>
     *       <li>The transaction (if started) is rolled back.</li>
     *       <li>The exception is logged, and a {@link DataAccessException} is propagated to the caller.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <h3>Logging:</h3>
     * <ul>
     *   <li>Logs informational messages at the start and end of successful operations.</li>
     *   <li>Logs error details, including stack traces, in the case of failure using {@link LoggerUtil}.</li>
     * </ul>
     *
     * <h4>Example:</h4>
     * <pre>{@code
     * // Non-transactional example: fetching a user
     * EUser user = HibernateUtil.executeTransaction(false, session -> {
     *     return session.get(EUser.class, userId);
     * });
     *
     * // Transactional example: creating a new entity
     * HibernateUtil.executeTransaction(true, session -> {
     *     EUser newUser = new EUser(...);
     *     session.persist(newUser);
     *     return null;
     * });
     * }</pre>
     *
     * @param requiresTransaction Indicates whether a transaction should be started
     *                            for the operation (true for write operations, false for read-only).
     * @param action              A {@link Function} that accepts the Hibernate {@link Session} and performs
     *                            the desired database operation. The function may return {@code null}.
     * @param <T>                 The return type of the action, e.g., an entity, a collection, or {@code null}.
     * @return The result of the action as defined by the {@code action} function, or {@code null} if the function
     *         has no result.
     * @throws DataAccessException If any database error occurs during the operation, including:
     *                             <ul>
     *                               <li>Hibernate {@link PersistenceException}</li>
     *                               <li>Unexpected runtime exceptions</li>
     *                             </ul>
     * @see LoggerUtil
     * @see DataAccessException
     */
    @Nullable
    public static <T> T executeTransaction(boolean requiresTransaction, Function<Session, T> action) throws DataAccessException {
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = null;
            if (requiresTransaction) transaction = session.beginTransaction();
            try {
                T result = action.apply(session);
                if (transaction != null) transaction.commit();
                return result;
            } catch (PersistenceException e) {
                if (transaction != null) transaction.rollback();
                LoggerUtil.logError("Database operation failed.", e);
                throw new DataAccessException(TypeOfDAE.GNL, "An unexpected persistence-related error occurred during the transaction.", e, true, true);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                LoggerUtil.logError("Unexpected error occurred during the database transaction.", e);
                throw new DataAccessException(TypeOfDAE.GNL, "Unexpected error occurred in database transaction.", e, false, false);
            }
        }
    }
}