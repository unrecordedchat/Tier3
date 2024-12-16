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
import com.unrecorded.database.util.FieldValidator;
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
 * This class manages operations related to notifications, including creation, retrieval, updating, and deletion.
 *
 * <p><b>Purpose:</b> The `NotificationPSQL` class handles notification-centric CRUD (Create, Read, Update, Delete)
 * operations for a PostgreSQL database. It integrates Hibernate ORM for database access and
 * provides mechanisms for managing notifications efficiently.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Creation of notifications with required fields like user ID, type, and content.</li>
 *   <li>Efficient retrieval of notifications based on various criteria (user, read status, etc.).</li>
 *   <li>Automatic handling of timestamps and notification lifecycle management.</li>
 * </ul>
 *
 * <p>All database-related exceptions are encapsulated in {@link DataAccessException}
 * to ensure consistent error reporting.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see com.unrecorded.database.entities.ENotification
 * @see com.unrecorded.database.util.HibernateUtil
 * @since 0.3
 */
public class NotificationPSQL {

    /**
     * Creates and saves a new notification in the database.
     *
     * <p>This method requires specifying the user ID, type, content, read status, and the timestamp for the notification.
     * It ensures all necessary fields are properly populated before persisting the notification entity.</p>
     *
     * @param userId    The ID of the user receiving the notification.
     * @param type      The type/category of the notification, such as "INFO", "ALERT", or "WARNING".
     * @param content   The content/message of the notification.
     * @param isRead    The read status of the notification (true if read, false otherwise).
     * @param timestamp The timestamp when the notification was created.
     * @throws IllegalArgumentException If any field validation fails (null or invalid).
     * @throws DataAccessException      If an error occurs while persisting the notification.
     */
    public void createNotification(@NotNull UUID userId, @NotNull String type, @NotNull String content, boolean isRead, @NotNull ZonedDateTime timestamp) throws IllegalArgumentException, DataAccessException {
        FieldValidator.notificationTypeConstraints(type);
        LoggerUtil.logInfo("Creating a new notification for userId: {}", userId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            ENotification notification = new ENotification(userId, type, content, isRead, timestamp);
            session.persist(notification);
            LoggerUtil.logInfo("Notification successfully created for userId: {}", userId.toString());
            return null;
        });
    }

    /**
     * Retrieves a notification by its unique identifier.
     *
     * @param notificationId The unique ID of the notification.
     * @return The {@link ENotification} entity matching the specified ID, or {@code null} if not found.
     * @throws DataAccessException If an error occurs while querying the database.
     */
    public @Nullable ENotification getNotificationById(@NotNull UUID notificationId) throws DataAccessException {
        LoggerUtil.logInfo("Retrieving notification by ID: {}", notificationId.toString());
        return HibernateUtil.executeTransaction(false, session -> session.find(ENotification.class, notificationId));
    }

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId The unique ID of the user.
     * @return A list of {@link ENotification} entities associated with the user, or an empty list if no notifications are found.
     * @throws DataAccessException If an error occurs while querying the database.
     */
    public @Nullable List<ENotification> getNotificationsByUserId(@NotNull UUID userId) throws DataAccessException {
        LoggerUtil.logInfo("Retrieving notifications for userId: {}", userId.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ENotification> query = builder.createQuery(ENotification.class);
            Root<ENotification> root = query.from(ENotification.class);
            query.select(root).where(builder.equal(root.get("userId"), userId));
            return session.createQuery(query).getResultList();
        });
    }

    /**
     * Retrieves all unread notifications for a specific user.
     *
     * @param userId The unique ID of the user.
     * @return A list of unread {@link ENotification} entities for the user.
     * @throws DataAccessException If an error occurs while querying the database.
     */
    public @Nullable List<ENotification> getUnreadNotificationsByUserId(@NotNull UUID userId) throws DataAccessException {
        LoggerUtil.logInfo("Retrieving unread notifications for userId: {}", userId.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ENotification> query = builder.createQuery(ENotification.class);
            Root<ENotification> root = query.from(ENotification.class);
            query.select(root).where(builder.and(builder.equal(root.get("userId"), userId), builder.equal(root.get("isRead"), false)));
            return session.createQuery(query).getResultList();
        });
    }

    /**
     * Updates the read status of a notification.
     *
     * @param notificationId The unique ID of the notification to update.
     * @param isRead         The new read status (true if the notification is read, false otherwise).
     * @throws DataAccessException If an error occurs while updating the database.
     */
    public void updateNotificationReadStatus(@NotNull UUID notificationId, boolean isRead) throws DataAccessException {
        LoggerUtil.logInfo("Updating read status for notificationId: {}", notificationId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            ENotification notification = session.find(ENotification.class, notificationId);
            if (notification != null) {
                notification.setRead(isRead);
                session.merge(notification);
                LoggerUtil.logInfo("Read status updated for notificationId: {}", notificationId.toString());
            } else LoggerUtil.logWarn("No notification found with ID: " + notificationId);
            return null;
        });
    }

    /**
     * Deletes a notification by its unique identifier.
     *
     * @param notificationId The unique ID of the notification to delete.
     * @throws DataAccessException If an error occurs while deleting the notification.
     */
    public void deleteNotification(@NotNull UUID notificationId) throws DataAccessException {
        LoggerUtil.logInfo("Deleting notification with ID: {}", notificationId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            ENotification notification = session.find(ENotification.class, notificationId);
            if (notification != null) {
                session.remove(notification);
                LoggerUtil.logInfo("Notification successfully deleted with ID: {}", notificationId.toString());
            } else LoggerUtil.logWarn("No notification found with ID: " + notificationId);
            return null;
        });
    }

    /**
     * Deletes all notifications for a specific user.
     *
     * @param userId The unique ID of the user whose notifications will be deleted.
     * @throws DataAccessException If an error occurs while deleting the notifications.
     */
    public void deleteNotificationsByUserId(@NotNull UUID userId) throws DataAccessException {
        LoggerUtil.logInfo("Deleting all notifications for userId: {}", userId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ENotification> query = builder.createQuery(ENotification.class);
            Root<ENotification> root = query.from(ENotification.class);
            query.select(root).where(builder.equal(root.get("userId"), userId));
            List<ENotification> notifications = session.createQuery(query).getResultList();
            notifications.forEach(session::remove);
            LoggerUtil.logInfo("Successfully deleted {} notifications for userId: {}", String.valueOf(notifications.size()), userId.toString());
            return null;
        });
    }
}