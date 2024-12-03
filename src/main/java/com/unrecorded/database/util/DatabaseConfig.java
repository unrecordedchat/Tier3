package com.unrecorded.database.util;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The DatabaseConfig class provides methods to retrieve database connection
 * details such as the database URL, user, and password from environment variables.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since PREVIEW
 */
public class DatabaseConfig {

    /**
     * Retrieves the database URL from the environment variables.
     *
     * @return The database URL in the format "jdbc:postgresql://localhost:5432/{DB_NAME}".
     * @throws IllegalStateException If the environment variable "DB_NAME" is not set.
     */
    public static @NotNull String getDatabaseUrl() {
        return Optional.ofNullable(System.getenv("DB_NAME"))
                .map(dbName -> "jdbc:postgresql://localhost:5432/" + dbName)
                .orElseThrow(() -> new IllegalStateException("DB_NAME is not set"));
    }

    /**
     * Retrieves the database user from the environment variables.
     *
     * @return The database user.
     * @throws IllegalStateException If the environment variable "DB_USER" is not set.
     */
    public static @NotNull String getDatabaseUser() {
        return Optional.ofNullable(System.getenv("DB_USER"))
                .orElseThrow(() -> new IllegalStateException("DB_USER is not set"));
    }

    /**
     * Retrieves the database password from the environment variables.
     *
     * @return The database password.
     * @throws IllegalStateException If the environment variable "DB_PASSWORD" is not set.
     */
    public static @NotNull String getDatabasePassword() {
        return Optional.ofNullable(System.getenv("DB_PASSWORD"))
                .orElseThrow(() -> new IllegalStateException("DB_PASSWORD is not set"));
    }
}