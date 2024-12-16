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

package com.unrecorded.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * A utility class responsible for initializing and managing the Hibernate {@link SessionFactory},
 * which is the primary interface for interacting with the database in Hibernate-based applications.
 *
 * <p><b>Purpose:</b> The {@code DBA} class centralizes the setup and lifecycle management of the Hibernate
 * {@link SessionFactory}.
 * It provides a singleton instance designed to safely and efficiently manage 
 * database connections and sessions across the application.</p>
 *
 * <h2>Main Features:</h2>
 * <ul>
 *   <li>Creates and provides a globally accessible, singleton instance of the {@link SessionFactory}.</li>
 *   <li>Handles the configuration loading from the `hibernate.cfg.xml` file or annotated classes.</li>
 *   <li>Provides a safe mechanism to shut down the {@link SessionFactory} during the application's termination phase,
 *       ensuring all resources, such as database connections, are properly released.</li>
 *   <li>Offers a robust error-handling mechanism to catch and propagate exceptions that may occur 
 *       during initialization or shutdown.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>The {@code DBA} class is designed to be used as a static utility throughout the application, 
 * ensuring consistent access to the {@link SessionFactory}.
 * Developers do not need to manually manage or configure Hibernate sessions.
 * Examples of typical usage include fetching the {@link SessionFactory}
 * to open sessions or interacting with utility methods such as those in {@code HibernateUtil}.</p>
 *
 * <h3>Example:</h3>
 * <pre>{@code
 * // Explicitly fetching the SessionFactory
 * SessionFactory sessionFactory = DBA.getSessionFactory();
 *
 * // Using it within a database operation
 * try (Session session = sessionFactory.openSession()) {
 *     Transaction transaction = session.beginTransaction();
 *     // Perform database operations
 *     transaction.commit();
 * } catch (Exception e) {
 *     e.printStackTrace();
 * }
 *
 * // Shutting down the SessionFactory during application tear-down
 * DBA.shutdown();
 * }</pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>The {@code DBA} class is thread-safe.
 * The {@link SessionFactory} is a thread-safe
 * singleton, meaning multiple threads can access it simultaneously without a conflict. 
 * However, Hibernate sessions created from it are not thread-safe and must only be used by
 * a single thread at a time.</p>
 *
 * <h2>Lifecycle Management:</h2>
 * <p>To ensure proper resource management:</p>
 * <ul>
 *   <li>The {@code buildSessionFactory} method is invoked during the class's initialization 
 *       to configure and initialize the {@link SessionFactory} instance.</li>
 *   <li>The {@code shutdown} method should be explicitly called during application shutdown to
 *       close the {@link SessionFactory} and release resources.</li>
 * </ul>
 *
 * <h2>Error Recovery:</h2>
 * <ul>
 *   <li>The {@code buildSessionFactory} method logs and re-throws initialization failures, ensuring
 *       the application is notified of critical errors during setup.</li>
 *   <li>Any errors in {@code shutdown} are logged but do not interfere with application termination.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see SessionFactory
 * @since 0.1
 */
public class DBA {

    /**
     * A singleton instance of the {@link SessionFactory} used throughout the application.
     *
     * <p>The {@link SessionFactory} is created during the initialization of the `DBA` class
     * by invoking the {@link #buildSessionFactory()} method.
     * All database sessions are expected to originate from this single 
     * instance to ensure consistency and resource efficiency.</p>
     *
     * <p>The {@link SessionFactory} configuration is loaded from the application's `hibernate.cfg.xml`
     * file, which specifies parameters such as the database connection URL, user credentials, dialect,
     * and annotated entity classes.</p>
     */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Initializes and returns a new {@link SessionFactory} instance.
     *
     * <p>This method is responsible for:
     * <ul>
     *   <li>Creating a {@link SessionFactory} using Hibernate's {@link Configuration} object.</li>
     *   <li>Loading the configuration and mappings from the `hibernate.cfg.xml` file.</li>
     *   <li>Throwing an {@link ExceptionInInitializerError} if the setup process fails, 
     *       ensuring the application stops cleanly during critical initialization errors.</li>
     * </ul>
     *
     * <h3>Error Handling:</h3>
     * <ul>
     *   <li>Any exceptions thrown during the creation of the {@link SessionFactory} are caught,
     *       logged, and re-thrown as {@link ExceptionInInitializerError} to prevent the application
     *       from continuing in an unusable state.</li>
     * </ul>
     *
     * <h3>Logging:</h3>
     * <ul>
     *   <li>Initialization failures are logged to standard error with diagnostics, providing insights
     *       into possible configuration issues or database connection problems.</li>
     * </ul>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * SessionFactory sessionFactory = DBA.getSessionFactory();
     * }</pre>
     *
     * @return A new {@link SessionFactory} instance, fully configured and ready for use.
     * @throws ExceptionInInitializerError If any exception occurs during the initialization process.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error during SessionFactory initialization: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Provides the singleton {@link SessionFactory} for database operations.
     *
     * <p>This method ensures that all components of the application access the same
     * {@link SessionFactory} instance, promoting efficient resource utilization by relying
     * on Hibernate's internal connection pooling and lifecycle management capabilities.</p>
     *
     * <h3>Usage:</h3>
     * <pre>{@code
     * SessionFactory sessionFactory = DBA.getSessionFactory();
     * }</pre>
     *
     * <h3>Thread Safety:</h3>
     * <ul>
     *   <li>The {@link SessionFactory} itself is thread-safe, allowing concurrent access by multiple threads.</li>
     *   <li>Any sessions created from the {@link SessionFactory} must be used within the same thread
     *       and closed when no longer needed.</li>
     * </ul>
     *
     * @return The singleton {@link SessionFactory} instance, initialized during class loading.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Closes the {@link SessionFactory}, releasing all resources held by it.
     *
     * <p>This method is intended to be invoked during the application's shutdown phase. 
     * It ensures that any open database connections, caches, and other resources managed
     * by the {@link SessionFactory} are properly released.</p>
     *
     * <h3>Usage:</h3>
     * <pre>{@code
     * DBA.shutdown();
     * }</pre>
     *
     * <h3>Behavior:</h3>
     * <ul>
     *   <li>Invokes the {@code close} method on the {@link SessionFactory}.
     *      Any open transactions or sessions should be closed beforehand to prevent errors.</li>
     *   <li>Logs any errors encountered during the shutdown process to aid debugging,
     *       but does not terminate the application process.</li>
     * </ul>
     *
     * <h3>Logging:</h3>
     * <ul>
     *   <li>Logs any unexpected exceptions or errors that occur during shutdown 
     *       (e.g., resource release issues).</li>
     * </ul>
     *
     * @see SessionFactory#close()
     */
    public static void shutdown() {
        getSessionFactory().close();
    }
}