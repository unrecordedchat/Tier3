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

package com.unrecorded.ktor.routes

import com.unrecorded.database.repositories.INotificationRepo
import com.unrecorded.ktor.dto.CreateNotificationRequest
import com.unrecorded.ktor.dto.NotificationResponse
import com.unrecorded.ktor.dto.UpdateNotificationReadStatusRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.ZonedDateTime
import java.util.*

/*
 * NotificationRoutes.kt
 *
 * This file manages routing logic for notification-related operations in a Ktor-based backend application.
 *
 * General Purpose:
 * - Provides RESTful API endpoints to handle notifications, including creating, retrieving, updating,
 *   and deleting notification records.
 * - Integrates with the `INotificationRepo` repository for data access, ensuring modular and maintainable code.
 *
 * Features:
 * - **Notification Management**:
 *   - Create new notifications for users.
 *   - Retrieve notifications by ID or user, with support for filtering unread notifications.
 *   - Update the read status of notifications and delete notifications as needed.
 *
 * Endpoint Summary:
 * 1. `POST /api/notifications` - Create a new notification.
 * 2. `GET /api/notifications/{id}` - Retrieve a notification by ID.
 * 3. `GET /api/notifications/user/{userId}` - Retrieve all notifications for a specific user.
 * 4. `GET /api/notifications/user/{userId}/unread` - Retrieve unread notifications for a user.
 * 5. `PATCH /api/notifications/{id}/readStatus` - Update the read status of a notification.
 * 6. `DELETE /api/notifications/{id}` - Delete a notification by ID.
 * 7. `DELETE /api/notifications/user/{userId}` - Delete all notifications for a specific user.
 *
 * Extensibility:
 * - Additional functionality can be added, such as batch processing notifications or support for real-time notifications.
 * - Features like priority-based filtering, grouping, or time-based cleanup can be introduced in the future.
 *
 * Usage:
 * The `notificationRoutes` function is attached to a `Route` object in Ktor, enabling seamless integration
 * with the main routing module.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Defines the routing logic for all notification-related operations in the Ktor application.
 *
 * The `notificationRoutes` function provides RESTful API endpoints to create, retrieve,
 * update, and delete user notifications. The routes also handle filtering based on read/unread status.
 * The function relies on `INotificationRepo` for database interactions, ensuring
 * clean separation between application logic and data access.
 *
 * ### Features:
 * - **Notification Creation** (POST /api/notifications):
 *   Allows creating a new notification for a user, with details like type, content, timestamp, and read status.
 * - **Notification Retrieval by ID** (GET /api/notifications/{id}):
 *   Fetches a single notification by its unique identifier.
 * - **Notification Retrieval by User** (GET /api/notifications/user/{userId}):
 *   Retrieves all notifications associated with a specific user.
 * - **Unread Notification Retrieval** (GET /api/notifications/user/{userId}/unread):
 *   Lists all unread notifications for a specific user.
 * - **Read Status Update** (PATCH /api/notifications/{id}/readStatus):
 *   Updates the read/unread status of a notification by its identifier.
 * - **Notification Deletion by ID** (DELETE /api/notifications/{id}):
 *   Deletes a notification using its identifier.
 * - **Delete All User Notifications** (DELETE /api/notifications/user/{userId}):
 *    Delete all notifications for a specific user.
 *
 * ### Dependencies:
 * - This function relies on `INotificationRepo` for all notification-related database operations.
 * - Centralized validation and error handling ensure consistent and maintainable routes.
 *
 * ### Extensibility:
 * - New endpoints can be introduced to support advanced notification features (e.g., bulk actions, filtering by type).
 * - Real-time or push notification integration could extend the functionality of this module.
 *
 * ### Usage Example:
 * The `notificationRoutes` function can be integrated as follows:
 * ```kotlin
 * routing {
 *     notificationRoutes(notificationRepo)
 * }
 * ```
 * Replace `notificationRepo` with a concrete implementation of `INotificationRepo`.
 *
 * ### Endpoint Summary:
 * | HTTP Method | Endpoint                                | Description                            |
 * |-------------|-----------------------------------------|----------------------------------------|
 * | POST        | /api/notifications                     | Create a new notification.            |
 * | GET         | /api/notifications/{id}                | Retrieve a notification by its ID.    |
 * | GET         | /api/notifications/user/{userId}       | Retrieve notifications for a user.    |
 * | GET         | /api/notifications/user/{userId}/unread | Retrieve unread notifications.         |
 * | PATCH       | /api/notifications/{id}/readStatus     | Update notification's read status.    |
 * | DELETE      | /api/notifications/{id}                | Delete a notification by ID.          |
 * | DELETE      | /api/notifications/user/{userId}       | Delete all notifications for a user.  |
 *
 * ### Request/Response Examples:
 * - **Create Notification** (`POST /api/notifications`):
 *   Request Body:
 *   ```json
 *   {
 *       "userId": "123e4567-e89b-12d3-a456-426614174000",
 *       "type": "Info",
 *       "content": "Your profile has been updated successfully.",
 *       "isRead": false,
 *       "timestamp": "2024-01-20T15:45:00+01:00"
 *   }
 *   ```
 *   Response:
 *   ```json
 *   {
 *       "status": "success",
 *       "message": "Notification created successfully!"
 *   }
 *   ```
 *
 * - **Retrieve Unread Notifications** (`GET /api/notifications/user/{userId}/unread`):
 *   Response:
 *   ```json
 *   [
 *       {
 *           "id": "64be46b3-2d8b-4205-9615-f438dbefc0a1",
 *           "userId": "123e4567-e89b-12d3-a456-426614174000",
 *           "type": "Alert",
 *           "content": "Password change required.",
 *           "isRead": false,
 *           "timestamp": "2024-01-19T10:30:00+01:00[Europe/Paris]"
 *       },
 *       ...
 *   ]
 *   ```
 *
 * ### Benefits:
 * - Provides a structured and consistent API for notification management.
 * - Modular design allows for scalable addition of new features.
 *
 * @param notificationRepo An implementation of the `INotificationRepo` interface for database operations.
 * @receiver Route The Ktor route to which the notification endpoints are registered.
 * @author Sergiu Chirap
 * @since 0.4
 */

fun Route.notificationRoutes(notificationRepo: INotificationRepo) {
    route("/api/notifications") {

        // 1. Create a new notification (POST /api/notifications).
        post {
            val request = call.receive<CreateNotificationRequest>()
            notificationRepo.createNotification(UUID.fromString(request.userId), request.type, request.content, request.isRead, ZonedDateTime.parse(request.timestamp))
            call.respond(mapOf("status" to "success", "message" to "Notification created successfully!"))
        }

        // 2. Retrieve a notification by ID (GET /api/notifications/{id}).
        get("/{id}") {
            val notificationId = call.parameters["id"]?.let(UUID::fromString)
            if (notificationId != null) {
                val notification = notificationRepo.getNotificationById(notificationId)
                if (notification != null) call.respond(
                    NotificationResponse(
                        id = notification.id?.toString(),
                        userId = notification.userId.toString(),
                        type = notification.type,
                        content = notification.content,
                        isRead = notification.isRead,
                        timestamp = notification.timestamp.toString()
                    )
                )
                else call.respond(mapOf("error" to "Notification not found"))
            } else call.respond(mapOf("error" to "Invalid notification ID"))
        }

        // 3. Retrieve all notifications for a specific user (GET /api/notifications/user/{userId}).
        get("/user/{userId}") {
            val userId = call.parameters["userId"]?.let(UUID::fromString)
            if (userId != null) {
                val notifications = notificationRepo.getNotificationsByUserId(userId)
                if (notifications != null) call.respond(
                    notifications.map {
                        NotificationResponse(
                            id = it.id?.toString(),
                            userId = it.userId.toString(),
                            type = it.type,
                            content = it.content,
                            isRead = it.isRead,
                            timestamp = it.timestamp.toString()
                        )
                    }
                )
                else call.respond(emptyList<NotificationResponse>())
            } else call.respond(mapOf("error" to "Invalid user ID"))
        }

        // 4. Retrieve all unread notifications for a user (GET /api/notifications/user/{userId}/unread).
        get("/user/{userId}/unread") {
            val userId = call.parameters["userId"]?.let(UUID::fromString)
            if (userId != null) {
                val unreadNotifications = notificationRepo.getUnreadNotificationsByUserId(userId)
                if (unreadNotifications != null) call.respond(
                    unreadNotifications.map {
                        NotificationResponse(
                            id = it.id?.toString(),
                            userId = it.userId.toString(),
                            type = it.type,
                            content = it.content,
                            isRead = it.isRead,
                            timestamp = it.timestamp.toString()
                        )
                    }
                )
                else call.respond(emptyList<NotificationResponse>())
            } else call.respond(mapOf("error" to "Invalid user ID"))
        }

        // 5. Mark a notification as read/unread (PATCH /api/notifications/{id}/readStatus).
        patch("/{id}/readStatus") {
            val notificationId = call.parameters["id"]?.let(UUID::fromString)
            val updateRequest = call.receive<UpdateNotificationReadStatusRequest>()
            if (notificationId != null) {
                notificationRepo.updateNotificationReadStatus(notificationId, updateRequest.isRead)
                val updatedNotification = notificationRepo.getNotificationById(notificationId)
                if (updatedNotification != null) call.respond(UpdateNotificationReadStatusRequest(isRead = updatedNotification.isRead))
                else call.respond(mapOf("error" to "Failed to update notification"))
            } else call.respond(mapOf("error" to "Invalid notification ID"))
        }

        // 6. Delete a notification by ID (DELETE /api/notifications/{id}).
        delete("/{id}") {
            val notificationId = call.parameters["id"]?.let(UUID::fromString)
            if (notificationId != null) {
                notificationRepo.deleteNotification(notificationId)
                call.respond(mapOf("status" to "success", "message" to "Notification deleted successfully!"))
            } else call.respond(mapOf("error" to "Invalid notification ID"))
        }

        // 7. Delete all notifications for a user (DELETE /api/notifications/user/{userId}).
        delete("/user/{userId}") {
            val userId = call.parameters["userId"]?.let(UUID::fromString)
            if (userId != null) {
                notificationRepo.deleteNotificationsByUserId(userId)
                call.respond(mapOf("status" to "success", "message" to "All user notifications deleted successfully!"))
            } else call.respond(mapOf("error" to "Invalid user ID"))
        }
    }
}