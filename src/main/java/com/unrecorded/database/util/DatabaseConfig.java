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

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A utility class for retrieving database connection configuration details from environment variables.
 *
 * <p><b>Purpose:</b> The {@code DatabaseConfig} class is designed to manage the retrieval of database
 * connection-related details such as the database URL, username, and password
 * in a secure and centralized manner. By relying on environment variables, it ensures
 * that sensitive credentials are not hardcoded into the source code, enhancing security practices.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Provides methods to dynamically retrieve the database URL, user credentials, and password from
 *       predefined environment variables.</li>
 *   <li>Ensures mandatory environment variables are provided by throwing helpful exceptions when the environment
 *       is misconfigured.</li>
 *   <li>Encapsulates error handling and validation for missing or malformed environment variables.</li>
 * </ul>
 *
 * <h2>Environment Variable Requirements:</h2>
 * <ul>
 *   <li><b>UnDB_NAME:</b> The name of the database to connect to. Should be configured to hold a valid database name.</li>
 *   <li><b>UnDB_USER:</b> The username for database authentication.</li>
 *   <li><b>UnDB_PASSWORD:</b> The password for the database user.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>The methods in this class are thread-safe, as they rely on immutable environment variables for configuration
 * and do not perform any mutable state operations.</p>
 *
 * <h2>Error Handling:</h2>
 * <ul>
 *   <li>If any required environment variable is missing, an {@link IllegalStateException} is thrown with
 *       a descriptive error message.</li>
 * </ul>
 *
 * <h2>Example Usage:</h2>
 * <pre>{@code
 * String databaseUrl = DatabaseConfig.getDatabaseUrl();
 * String databaseUser = DatabaseConfig.getDatabaseUser();
 * String databasePassword = DatabaseConfig.getDatabasePassword();
 * }</pre>
 *
 * <p>This class is a foundational component of the application's database connection setup and is
 * expected to be used during the initialization phase of the database connection layer.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since PREVIEW
 */
public class DatabaseConfig {

    /**
     * Retrieves the database URL from the environment variables.
     *
     * <p>This method constructs the database URL dynamically based on the value of the {@code UnDB_NAME}
     * environment variable, which specifies the database name to use.
     * The format of the database URL is predefined as {@code jdbc:postgresql://localhost:5432/{UnDB_NAME}}.</p>
     *
     * <h3>Behavior:</h3>
     * <ul>
     *   <li>Reads the {@code UnDB_NAME} environment variable to determine the target database name.</li>
     *   <li>Throws an {@link IllegalStateException} if the {@code UnDB_NAME} variable is not set.</li>
     *   <li>Constructs and returns the complete database URL in the required JDBC format.</li>
     * </ul>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * String databaseUrl = DatabaseConfig.getDatabaseUrl();
     * System.out.println(databaseUrl); // Output: jdbc:postgresql://localhost:5432/myDatabase
     * }</pre>
     *
     * <h3>Exceptions:</h3>
     * <ul>
     *   <li>An {@link IllegalStateException} is thrown if the {@code UnDB_NAME} environment variable is not set,
     *       with a descriptive error message.</li>
     * </ul>
     *
     * @return The database URL as a {@link String}, e.g., {@code jdbc:postgresql://localhost:5432/myDatabase}.
     * @throws IllegalStateException If the {@code UnDB_NAME} environment variable is not configured.
     */
    public static @NotNull String getDatabaseUrl() {
        return Optional.ofNullable(System.getenv("UnDB_NAME")).map(dbName -> "jdbc:postgresql://localhost:5432/" + dbName).orElseThrow(() -> new IllegalStateException("UnDB_NAME is not set"));
    }

    /**
     * Retrieves the username for database authentication from the environment variables.
     *
     * <p>This method reads the {@code UnDB_USER} environment variable and returns its value. 
     * It ensures that a valid username is provided by throwing an exception if the variable is missing.</p>
     *
     * <h3>Behavior:</h3>
     * <ul>
     *   <li>Attempts to read the value of the {@code UnDB_USER} environment variable.</li>
     *   <li>Throws an {@link IllegalStateException} if the {@code UnDB_USER} variable is not set.</li>
     * </ul>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * String databaseUser = DatabaseConfig.getDatabaseUser();
     * System.out.println(databaseUser); // Output: myDatabaseUser
     * }</pre>
     *
     * <h3>Exceptions:</h3>
     * <ul>
     *   <li>An {@link IllegalStateException} is thrown if the {@code UnDB_USER} environment variable is not set,
     *       with a descriptive error message.</li>
     * </ul>
     *
     * @return The database username as a {@link String}.
     * @throws IllegalStateException If the {@code UnDB_USER} environment variable is not configured.
     */
    public static @NotNull String getDatabaseUser() {
        return Optional.ofNullable(System.getenv("UnDB_USER")).orElseThrow(() -> new IllegalStateException("UnDB_USER is not set"));
    }

    /**
     * Retrieves the password for database authentication from the environment variables.
     *
     * <p>This method reads the value of the {@code UnDB_PASSWORD} environment variable and returns it securely.
     * It ensures that a valid password is configured for the database user.</p>
     *
     * <h3>Behavior:</h3>
     * <ul>
     *   <li>Reads the {@code UnDB_PASSWORD} environment variable.</li>
     *   <li>Throws an {@link IllegalStateException} if the {@code UnDB_PASSWORD} variable is not set.</li>
     * </ul>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * String databasePassword = DatabaseConfig.getDatabasePassword();
     * System.out.println(databasePassword); // Output: mySecurePassword
     * }</pre>
     *
     * <h3>Exceptions:</h3>
     * <ul>
     *   <li>An {@link IllegalStateException} is thrown if the {@code UnDB_PASSWORD} environment variable is not set,
     *       with a descriptive error message.</li>
     * </ul>
     *
     * <p><b>Security Note:</b> Avoid printing or logging the returned password value to prevent exposing sensitive
     * credentials in logs or output.</p>
     *
     * @return The database password as a {@link String}.
     * @throws IllegalStateException If the {@code UnDB_PASSWORD} environment variable is not configured.
     */
    public static @NotNull String getDatabasePassword() {
        return Optional.ofNullable(System.getenv("UnDB_PASSWORD")).orElseThrow(() -> new IllegalStateException("UnDB_PASSWORD is not set"));
    }
}