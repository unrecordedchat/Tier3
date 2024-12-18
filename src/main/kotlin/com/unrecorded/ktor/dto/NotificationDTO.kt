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

package com.unrecorded.ktor.dto

import kotlinx.serialization.Serializable

/*
 * NotificationDTO.kt
 *
 * This file defines the data transfer objects (DTOs) related to notification management within the Ktor application.
 * These DTOs are used for incoming requests as well as for outgoing responses in the notification-related API endpoints.
 *
 * General Purpose:
 * - Standardize the data format for creating, updating, and retrieving notifications.
 * - Enable seamless communication between the client and server by establishing a clear contract for data exchange.
 *
 * Features:
 * - Fully **Serializable**: All DTOs are compatible with Kotlinx Serialization to support consistent JSON serialization/deserialization.
 * - **Request Objects**: Represent incoming payloads for creating new notifications or updating existing ones.
 * - **Response Objects**: Define the data structure used to return notification details to API clients.
 *
 * DTOs Provided:
 * 1. `CreateNotificationRequest`: Represents the payload for creating a new notification.
 * 2. `UpdateNotificationReadStatusRequest`: Represents the payload for updating the read status of a notification.
 * 3. `NotificationResponse`: Represents the response object for returning notification details to clients.
 *
 * Extensibility:
 * - Additional DTOs can be introduced to support new features, such as priority-based notifications or push notification tokens.
 * - Existing DTOs can be extended with optional fields as new requirements evolve.
 *
 * Usage:
 * These DTOs are an integral part of the notifications API, ensuring well-defined interactions between API consumers and the backend.
 * - **Requests**: Map incoming JSON payloads to DTOs for validation and processing.
 * - **Responses**: Send structured notification data back to clients in a predictable format.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Represents the request payload for creating a new notification.
 *
 * This data transfer object (DTO) ensures that the required attributes are provided when clients create new notifications.
 * It facilitates a clear contract for communication with the API while enabling strict validation of fields.
 *
 * ### Features:
 * - Fully serializable with Kotlinx Serialization for JSON integration.
 * - Captures all necessary details for creating a notification, including user association, type, content, and read status.
 * - Ensures correctness by requiring all fields with appropriate data types.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *       "userId": "123e4567-e89b-12d3-a456-426614174000",
 *       "type": "Info",
 *       "content": "Your password has been changed.",
 *       "isRead": false,
 *       "timestamp": "2024-01-20T15:45:00+01:00"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = CreateNotificationRequest(
 *       userId = "123e4567-e89b-12d3-a456-426614174000",
 *       type = "Info",
 *       content = "Your password has been changed.",
 *       isRead = false,
 *       timestamp = "2024-01-20T15:45:00+01:00"
 *   )
 *   ```
 *
 * @property userId The unique identifier of the user associated with the notification.
 * @property type The type of notification (e.g., "Info", "Alert", or "Success").
 * @property content The content or message of the notification, usually a brief and informative text.
 * @property isRead Specifies whether the notification has been read (`true`) or not (`false`).
 * @property timestamp The time when the notification was created, in ISO-8601 format (e.g., `2024-01-20T15:45:00+01:00`).
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class CreateNotificationRequest(
    val userId: String,
    val type: String,
    val content: String,
    val isRead: Boolean,
    val timestamp: String
)

/**
 * Represents the request payload for updating the read status of a notification.
 *
 * This DTO defines only the `isRead` field, simplifying the API for updating an existing notification's status.
 * It helps ensure updates to notifications are performed efficiently with minimal overhead.
 *
 * ### Features:
 * - Serializable with Kotlinx for smooth JSON handling.
 * - Designed for partial updates to the `isRead` field of a notification.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *       "isRead": true
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = UpdateNotificationReadStatusRequest(isRead = true)
 *   ```
 *
 * @property isRead Represents the updated read status. `true` for read, `false` for unread.
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class UpdateNotificationReadStatusRequest(
    val isRead: Boolean
)

/**
 * Represents the structure of a notification returned by the API.
 *
 * This data transfer object (DTO) is used to encapsulate notification details for API responses. It ensures that the response
 * contains only the necessary and client-readable fields for a notification, while omitting any sensitive internal data.
 *
 * ### Features:
 * - Fully serializable with Kotlinx Serialization for consistent JSON responses.
 * - Includes key attributes like `id`, `userId`, `type`, and `content`, ensuring clarity for clients.
 * - Helps maintain consistency across notification endpoints in terms of response structure.
 *
 * ### Usage Example:
 * - **Client JSON Response**:
 *   ```json
 *   {
 *       "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *       "userId": "123e4567-e89b-12d3-a456-426614174000",
 *       "type": "Info",
 *       "content": "Your password has been changed.",
 *       "isRead": true,
 *       "timestamp": "2024-01-20T15:45:00+01:00"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val response = NotificationResponse(
 *       id = "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *       userId = "123e4567-e89b-12d3-a456-426614174000",
 *       type = "Info",
 *       content = "Your password has been changed.",
 *       isRead = true,
 *       timestamp = "2024-01-20T15:45:00+01:00"
 *   )
 *   ```
 *
 * @property id The unique identifier of the notification, returned as a string. Can be null for certain cases.
 * @property userId The unique identifier of the user associated with the notification.
 * @property type The type of notification, providing context (e.g., "Alert", "Info").
 * @property content The content or body of the notification. Examples include status updates or informational messages.
 * @property isRead The read status of the notification. `true` if it has been read, otherwise `false`.
 * @property timestamp The timestamp (in ISO-8601 format) indicating when the notification was created.
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class NotificationResponse(
    val id: String?,
    val userId: String,
    val type: String,
    val content: String,
    val isRead: Boolean,
    val timestamp: String
)