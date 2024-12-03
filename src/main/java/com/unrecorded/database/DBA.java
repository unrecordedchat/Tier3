package com.unrecorded.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * The DBA class is responsible for initializing and providing access to the Hibernate
 * {@link SessionFactory} for managing and interacting with the database.
 * <p>It also provides a method to shut down the SessionFactory, ensuring all resources are properly released.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see SessionFactory
 * @since PREVIEW
 */
public class DBA {
    /**
     * A singleton SessionFactory instance initialized via the buildSessionFactory() method.
     * This instance is utilized to establish and manage database sessions throughout the application.
     */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Builds and returns a new {@link SessionFactory} instance configured using the
     * Hibernate configuration file.
     *
     * @return A new {@link SessionFactory} instance.
     * @throws ExceptionInInitializerError if the creation of the {@link SessionFactory} fails.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Provides a singleton instance of the {@link SessionFactory} for establishing
     * and managing database sessions.
     *
     * @return The singleton {@link SessionFactory} instance.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Closes the SessionFactory, releasing all resources held by it.
     * <p>This method should be called during the application's shutdown process
     * to ensure that any resources, such as database connections, are properly
     * released.
     */
    public static void shutdown() {
        getSessionFactory().close();
    }
}