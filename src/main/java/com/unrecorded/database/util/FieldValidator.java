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

import java.util.List;
import java.util.UUID;

/**
 * Utility class for field validation.
 *
 * <p>The {@code FieldValidator} class is a utility used to validate fields throughout the application. 
 * This includes checking constraints for usernames, emails, passwords, group names, roles, and notification types. 
 * It ensures compliance with business rules, protects against invalid input, and provides consistent validation mechanisms 
 * for entities handled by database repositories and services.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Validates user input fields such as usernames, emails, group names, and notification types.</li>
 *   <li>Enforces field-specific constraints like length limits and format requirements.</li>
 *   <li>Prevents invalid state transitions for fields such as friendship status or user links.</li>
 *   <li>Supports integration with logging utilities (e.g., {@link LoggerUtil}) to trace validation failures.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>This class is thread-safe because all methods are {@code static} and perform no state modifications.</p>
 *
 * <p><b>Note:</b> This utility is invoked primarily in repository classes like {@code UserPSQL} and is designed to throw 
 * {@link IllegalArgumentException} for invalid inputs, making it ideal for use in input validation pipelines.</p>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see LoggerUtil
 * @since 0.3
 */
public class FieldValidator {

    /**
     * Validates constraints for a username.
     *
     * <p>A valid username must be non-blank and <= 30 characters in length. 
     * If the constraints are violated, an {@link IllegalArgumentException} is thrown. 
     * This method is typically used in user-related operations such as user creation 
     * or username updates, such as those found in {@code UserPSQL}.</p>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * FieldValidator.usernameConstraints("valid_username"); // Valid input
     * FieldValidator.usernameConstraints(""); // Throws IllegalArgumentException
     * }</pre>
     *
     * @param username The username to validate.
     * @throws IllegalArgumentException If the username is blank or exceeds 30 characters in length.
     * @see LoggerUtil
     */
    public static void usernameConstraints(final @NotNull String username) throws IllegalArgumentException {
        if (username.isBlank() || username.length() > 30) {
            LoggerUtil.logWarn("Validation failed for username: " + username);
            throw new IllegalArgumentException("Invalid username.");
        }
    }

    /**
     * Validates constraints for an email address.
     *
     * <p>A valid email must be non-blank, <= 254 characters in length, contain an '@' character, 
     * and include a period ('.') to represent a proper domain. 
     * Invalid email inputs result in an {@link IllegalArgumentException}.</p>
     *
     * <h3>Constraints:</h3>
     * <ul>
     *   <li>The email must be a syntactically valid email address.</li>
     *   <li>Blank or improperly formatted emails are not allowed.</li>
     * </ul>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * FieldValidator.emailConstraints("user@example.com"); // Valid input
     * FieldValidator.emailConstraints("invalid_email");   // Throws IllegalArgumentException
     * }</pre>
     *
     * @param email The email address to validate.
     * @throws IllegalArgumentException If the email is blank, exceeds 254 characters, or is improperly formatted.
     * @see LoggerUtil
     */
    public static void emailConstraints(final @NotNull String email) throws IllegalArgumentException {
        if (email.isBlank() || email.length() > 254 || !email.contains("@") || !email.contains(".")) {
            LoggerUtil.logWarn("Validation failed for email: " + email);
            throw new IllegalArgumentException("Invalid email address.");
        }
    }

    /**
     * Validates constraints for a password.
     *
     * <p>Ensures that the password is non-blank. Additional password-specific
     * checks like length, strength, or complexity should be applied elsewhere.</p>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * FieldValidator.passwordConstraints("securePassword123"); // Valid input
     * FieldValidator.passwordConstraints("");                  // Throws IllegalArgumentException
     * }</pre>
     *
     * @param password The password to validate.
     * @throws IllegalArgumentException If the password is blank.
     */
    public static void passwordConstraints(@NotNull String password) throws IllegalArgumentException {
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank.");
        }
    }

    /**
     * Validates constraints for a friendship status.
     *
     * <p>Allowed statuses include "FRD" (Friend), "PND" (Pending), and "UNK" (Unknown). 
     * Other values are considered invalid.</p>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * FieldValidator.friendStatusConstraints("FRD"); // Valid input
     * FieldValidator.friendStatusConstraints("XYZ"); // Throws IllegalArgumentException
     * }</pre>
     *
     * @param status The friendship status to validate.
     * @throws IllegalArgumentException If the status is blank or not one of the allowed values.
     */
    public static void friendStatusConstraints(@NotNull String status) throws IllegalArgumentException {
        if (status.isBlank() || !List.of("FRD", "PND", "UNK").contains(status)) {
            throw new IllegalArgumentException("Invalid friendship status: " + status);
        }
    }

    /**
     * Validates constraints for linking two users.
     *
     * <p>This method ensures that two user IDs are not identical,
     * preventing invalid self-linking operations such as adding oneself as a friend.</p>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * FieldValidator.userLinkConstraints(UUID.randomUUID(), UUID.randomUUID()); // Valid input
     * FieldValidator.userLinkConstraints(uuid1, uuid1);                        // Throws IllegalArgumentException
     * }</pre>
     *
     * @param userId1 First user's unique ID.
     * @param userId2 Second user's unique ID.
     * @throws IllegalArgumentException If the IDs are identical.
     */
    public static void userLinkConstraints(@NotNull UUID userId1, UUID userId2) throws IllegalArgumentException {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Links cannot point to the same user.");
        }
    }

    /**
     * Validates constraints for a group name.
     *
     * <p>A valid group name must be non-blank and contain no more than 50 characters.</p>
     *
     * @param name The group name to validate.
     * @throws IllegalArgumentException If the group name is blank or exceeds 50 characters.
     */
    public static void groupNameConstraints(@NotNull String name) throws IllegalArgumentException {
        if (name.isBlank() || name.length() > 50) {
            LoggerUtil.logWarn("Validation failed for group name: " + name);
            throw new IllegalArgumentException("Invalid group name. It must not be blank and must be 50 characters or less.");
        }
    }

    /**
     * Validates constraints for a group role.
     *
     * @param role The user role in the group to validate.
     * @throws IllegalArgumentException If the role is blank or exceeds 50 characters.
     */
    public static void groupRoleConstraints(@NotNull String role) throws IllegalArgumentException {
        if (role.isBlank() || role.length() > 50) {
            LoggerUtil.logWarn("Validation failed for group role: " + role);
            throw new IllegalArgumentException("Invalid group role: " + role);
        }
    }

    /**
     * Validates constraints for an emoji.
     *
     * <p>A valid emoji must be non-blank and no longer than 4 characters.</p>
     *
     * @param emoji The emoji to validate.
     * @throws IllegalArgumentException If the emoji is blank or exceeds 4 characters.
     */
    public static void emojiConstraints(@NotNull String emoji) throws IllegalArgumentException {
        if (emoji.isBlank() || emoji.length() > 4) {
            LoggerUtil.logWarn("Validation failed for emoji: " + emoji);
            throw new IllegalArgumentException("Invalid emoji. It must not be blank and must be 4 characters or less.");
        }
    }

    /**
     * Validates constraints for a notification type.
     *
     * <p>A valid notification type must be non-blank and no longer than 15 characters.</p>
     *
     * @param type The notification type to validate.
     * @throws IllegalArgumentException If the type is blank or exceeds 15 characters.
     */
    public static void notificationTypeConstraints(@NotNull String type) throws IllegalArgumentException {
        if (type.isBlank() || type.length() > 15) {
            LoggerUtil.logWarn("Validation failed for notification type: " + type);
            throw new IllegalArgumentException("Invalid notification type: " + type);
        }
    }
}