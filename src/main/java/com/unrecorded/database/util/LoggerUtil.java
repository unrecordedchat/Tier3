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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class that provides logging capabilities for the application.
 *
 * <p>This class simplifies logging by providing methods for different log levels
 * (DEBUG, INFO, WARN, ERROR) and ensures that sensitive data in log messages
 * and parameters is sanitized before being logged.</p>
 *
 * <p><b>Purpose:</b>
 * The `LoggerUtil` abstracts commonly used logging operations by utilizing SLF4J
 * and dynamically sanitizing sensitive arguments like passwords, tokens, or keys.
 * This ensures compliance with best practices in sensitive data handling for logs.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Provides overloaded methods for logging messages with or without parameters.</li>
 *   <li>Logs messages at different levels, such as DEBUG, INFO, WARN, and ERROR.</li>
 *   <li>Automatically sanitizes sensitive keywords (e.g., "password", "secret", etc.) from both
 *   log messages and values before logging.</li>
 *   <li>Supports Throwable objects for logging exceptions with stack traces.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>{@code
 * // Example usage in a repository method:
 * LoggerUtil.logInfo("User creation successful for username {}", username);
 * try {
 *     ...
 * } catch (Exception e) {
 *     LoggerUtil.logError("Error creating user", e);
 * }
 * }</pre>
 *
 * <p><b>Thread Safety:</b> Since SLF4J Loggers are thread-safe, this class can safely be accessed
 * concurrently without further synchronization.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see org.slf4j.Logger
 * @since 0.3
 */
public class LoggerUtil {

    /**
     * The logger instance used for handling logging operations within the {@code LoggerUtil} class.
     **/
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    /**
     * Logs a DEBUG-level message.
     *
     * <p>This method sanitizes the input message to guarantee that sensitive keywords
     * are safely masked before the message is logged.</p>
     *
     * @param message The message to be logged. May contain placeholders (e.g., {@code {}}).
     */
    public static void logDebug(@NotNull String message) {
        if (logger.isDebugEnabled()) logger.debug(sanitizeLogMessage(message));
    }

    /**
     * Logs a DEBUG-level message with one parameter.
     *
     * <p>The message and the parameter are sanitized to ensure sensitive
     * keywords are safely masked before logging.</p>
     *
     * @param message The message to be logged. May contain a single placeholder (e.g., {@code {}}).
     * @param param   The parameter to be injected into the message.
     */
    public static void logDebug(@NotNull String message, @NotNull String param) {
        if (logger.isDebugEnabled()) logger.debug(sanitizeLogMessage(message), sanitizeLogValue(param));
    }

    /**
     * Logs a DEBUG-level message with two parameters.
     *
     * <p>The message and both parameters are sanitized to ensure sensitive
     * keywords are safely masked before logging.</p>
     *
     * @param message The message to be logged. May contain two placeholders (e.g., {@code {} {} combo}).
     * @param param1  The first parameter to be injected into the message.
     * @param param2  The second parameter to be injected into the message.
     */
    public static void logDebug(@NotNull String message, @NotNull String param1, @NotNull String param2) {
        if (logger.isDebugEnabled())
            logger.debug(sanitizeLogMessage(message), sanitizeLogValue(param1), sanitizeLogValue(param2));
    }

    /**
     * Logs an INFO-level message.
     *
     * <p>This method sanitizes the input message to guarantee that
     * sensitive keywords are safely masked before the message is logged.</p>
     *
     * @param message The message to be logged. May contain placeholders.
     */
    public static void logInfo(@NotNull String message) {
        if (logger.isInfoEnabled()) logger.info(sanitizeLogMessage(message));
    }

    /**
     * Logs an INFO-level message with one parameter.
     *
     * <p>The message and the parameter are sanitized to ensure sensitive
     * keywords are safely masked before logging.</p>
     *
     * @param message The message to be logged. May contain a single placeholder
     *                (e.g., {@code Username {} has logged in.}).
     * @param param   The parameter to be injected into the message.
     */
    public static void logInfo(@NotNull String message, @NotNull String param) {
        if (logger.isInfoEnabled()) logger.info(sanitizeLogMessage(message), sanitizeLogValue(param));
    }

    /**
     * Logs an INFO-level message with two parameters.
     *
     * <p>The message and both parameters are sanitized to ensure sensitive
     * keywords are safely masked before logging.</p>
     *
     * @param message The message to be logged. May contain two placeholders.
     * @param param1  The first parameter to be injected into the message.
     * @param param2  The second parameter to be injected into the message.
     */
    public static void logInfo(@NotNull String message, @NotNull String param1, @NotNull String param2) {
        if (logger.isInfoEnabled())
            logger.info(sanitizeLogMessage(message), sanitizeLogValue(param1), sanitizeLogValue(param2));
    }

    /**
     * Logs a WARN-level message.
     *
     * <p>This method sanitizes the input message to guarantee that sensitive
     * keywords are safely masked before the message is logged.</p>
     *
     * @param message The message to be logged. May contain placeholders.
     */
    public static void logWarn(@NotNull String message) {
        if (logger.isWarnEnabled()) logger.warn(sanitizeLogMessage(message));
    }

    /**
     * Logs an ERROR-level message.
     *
     * <p>The message and any Throwable (e.g., exceptions) passed to this method
     * will be logged for debugging purposes. The message is sanitized for sensitive information.</p>
     *
     * @param message   The message to be logged.
     * @param throwable The exception or error to include in the log output.
     */
    public static void logError(@NotNull String message, @NotNull Throwable throwable) {
        if (logger.isErrorEnabled()) logger.error(sanitizeLogMessage(message), throwable);
    }

    /**
     * Sanitizes a log message by replacing sensitive keywords with asterisks.
     *
     * <p>Specifically, masks values like "password", "token", "secret", and "key".</p>
     *
     * @param message The message to sanitize.
     * @return A sanitized message with sensitive information masked.
     */
    private static @NotNull String sanitizeLogMessage(@Nullable String message) {
        if (message == null) return "null";
        return message.replaceAll("(?i)(password|secret|token|key)\\S*", "*****").trim();
    }

    /**
     * Sanitizes an individual parameter before injecting it into a log message.
     *
     * <p>Replaces sensitive values such as "password", "token", "secret", and "key" with "*****".</p>
     *
     * @param value The log parameter to sanitize.
     * @return A sanitized version of the parameter.
     */
    private static @NotNull String sanitizeLogValue(@NotNull String value) {
        return value.replaceAll("(?i)(password|secret|token|key)\\S*", "*****");
    }
}