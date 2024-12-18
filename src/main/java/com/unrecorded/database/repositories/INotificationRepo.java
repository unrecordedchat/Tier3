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

import com.unrecorded.database.entities.ENotification;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Interface defining notification-related database operations.
 *
 * <p>The {@code INotificationRepo} interface specifies methods for interacting with the
 * {@link ENotification} entity, including CRUD (Create, Read, Update, Delete) functionality
 * and additional notification-specific operations.
 * Implementations should ensure appropriate validation, error handling, and secure data management.</p>
 *
 * <p>All database-related exceptions should be wrapped in {@link DataAccessException}
 * for uniform error reporting.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>CRUD operations for managing notification data in the database.</li>
 *   <li>Support for querying notifications by user ID, timestamp, and read status.</li>
 *   <li>Thread-safe operation for use in multithreaded environments.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since 0.3
 */
public interface INotificationRepo {

    /**
     * Creates a new notification in the database.
     *
     * <p>This method validates input fields and persists the {@link ENotification} entity in the database.</p>
     *
     * @param userId The ID of the user receiving the notification.
     * @param type The type/category of the notification, such as "INFO", "ALERT", or "WARNING".
     * @param content The content/message of the notification.
     * @param isRead The read status of the notification (true if read, false otherwise).
     * @param timestamp The timestamp when the notification was created.
     * @throws IllegalArgumentException If input validation fails.
     * @throws DataAccessException If an error occurs while persisting the notification in the database.
     */
    void createNotification(@NotNull UUID userId, @NotNull String type, @NotNull String content, boolean isRead, @NotNull ZonedDateTime timestamp) throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves a notification by its unique identifier.
     *
     * @param notificationId The unique ID of the notification.
     * @return The {@link ENotification} entity matching the specified ID, or {@code null} if not found.
     * @throws DataAccessException If an issue occurs while querying the database.
     */
    @Nullable ENotification getNotificationById(@NotNull UUID notificationId) throws DataAccessException;

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId The unique ID of the user.
     * @return A list of {@link ENotification} entities associated with the user, or an empty list if no notifications are found.
     * @throws DataAccessException If an error occurs while querying the database.
     */
    @Nullable List<ENotification> getNotificationsByUserId(@NotNull UUID userId) throws DataAccessException;

    /**
     * Retrieves all unread notifications for a specific user.
     *
     * @param userId The unique ID of the user.
     * @return A list of unread {@link ENotification} entities for the user.
     * @throws DataAccessException If an error occurs while querying the database.
     */
    @Nullable List<ENotification> getUnreadNotificationsByUserId(@NotNull UUID userId) throws DataAccessException;

    /**
     * Updates the read status of a notification.
     *
     * @param notificationId The unique ID of the notification to update.
     * @param isRead The new read status (true if read, false otherwise).
     * @throws DataAccessException If an error occurs while updating the notification in the database.
     */
    void updateNotificationReadStatus(@NotNull UUID notificationId, boolean isRead) throws DataAccessException;

    /**
     * Deletes a notification by its unique identifier.
     *
     * @param notificationId The unique ID of the notification to delete.
     * @throws DataAccessException If an error occurs while deleting the notification from the database.
     */
    void deleteNotification(@NotNull UUID notificationId) throws DataAccessException;

    /**
     * Deletes all notifications for a specific user.
     *
     * @param userId The unique ID of the user whose notifications will be deleted.
     * @throws DataAccessException If an error occurs while deleting the notifications.
     */
    void deleteNotificationsByUserId(@NotNull UUID userId) throws DataAccessException;
}