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
 * MessageDTO.kt
 *
 * This file defines the data transfer objects (DTOs) used for managing messages within the Ktor application.
 * These DTOs ensure consistent communication between the server and client while handling message-related operations.
 *
 * General Purpose:
 * - Provide a standardized contract for defining and sharing message data within the system.
 * - Facilitate integration between the server and API clients by formalizing the request and response structures.
 *
 * Features:
 * - Fully **Serializable**: Compatible with Kotlinx Serialization for seamless handling of JSON request/response data.
 * - **Request Objects**: Define the strict structure required for API input when creating or updating messages.
 * - **Response Objects**: Structure the data returned to clients, exposing only safe and necessary fields.
 * 
 * DTOs Provided:
 * 1. `CreateMessageRequest`: Represents the payload for creating a new message (direct or group).
 * 2. `MessageResponse`: Represents the structure for sending message details in API responses.
 * 3. `UpdateMessageContentRequest`: Represents the payload for updating the encrypted content of a message.
 *
 * Extensibility:
 * - Additional DTOs can be added for advanced messaging functionalities (e.g., attachments, reactions, or pinned messages).
 * - DTOs can be extended with optional fields as new features or requirements arise.
 *
 * Usage Example:
 * These DTOs are designed for RESTful API interactions, simplifying client-server communication based on the messaging feature.
 * - **Request Objects**: Used for creating or updating a message (JSON deserialization).
 * - **Response Objects**: Used for sending consistent message data to the client (JSON serialization).
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Represents the payload required for creating a message.
 *
 * This data transfer object (DTO) defines the structure of the request body for creating a new message,
 * supporting both direct and group messaging scenarios.
 *
 * ### Features:
 * - **Serializable**: Fully supports JSON for integration with RESTful APIs.
 * - Contains the fields necessary for message creation, including sender, recipient or group, and content.
 * - Optional `recipientId` for direct messages (`isGroup` is false) and `groupId` for group messages (`isGroup` is true).
 *
 * ### Usage Example:
 * - **Creating a direct message**:
 *   ```json
 *   {
 *     "senderId": "uuid-sender",
 *     "recipientId": "uuid-recipient",
 *     "isGroup": false,
 *     "contentEncrypted": "encrypted-message-content"
 *   }
 *   ```
 * - **Creating a group message**:
 *   ```json
 *   {
 *     "senderId": "uuid-sender",
 *     "groupId": "uuid-group",
 *     "isGroup": true,
 *     "contentEncrypted": "encrypted-message-content"
 *   }
 *   ```
 *
 * @property senderId The unique identifier of the message sender. Must not be null or empty.
 * @property recipientId The unique identifier of the message recipient (for direct messages). Nullable for group messages.
 * @property groupId The unique identifier of the group (for group messages). Nullable for direct messages.
 * @property isGroup A flag indicating whether the message is for a group (`true`) or a direct message (`false`).
 * @property contentEncrypted The encrypted content of the message. Must not be null or empty.
 * @author Sergiu Chirap
 * @since 0.4
 */
@Serializable
data class CreateMessageRequest(
    val senderId: String,
    val recipientId: String? = null,
    val groupId: String? = null,
    val isGroup: Boolean,
    val contentEncrypted: String
)

/**
 * Represents the structure for the response containing message details.
 *
 * This DTO is used to encapsulate message information returned from the server to the client,
 * ensuring that only the necessary and safe fields are exposed.
 *
 * ### Features:
 * - Fully serializable to JSON for use in RESTful responses.
 * - Includes essential message metadata such as timestamp, sender, and recipient/group.
 * - Omits sensitive information while retaining encrypted message content.
 *
 * ### Usage Example:
 * - **Client JSON Response**:
 *   ```json
 *   {
 *     "id": "uuid-message",
 *     "senderId": "uuid-sender",
 *     "recipientId": "uuid-recipient",
 *     "groupId": "uuid-group",
 *     "isGroup": true,
 *     "contentEncrypted": "encrypted-message-content",
 *     "timestamp": "2024-05-13T14:55:30",
 *     "isDeleted": false
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val response = MessageResponse(
 *       id = "uuid-message",
 *       senderId = "uuid-sender",
 *       recipientId = "uuid-recipient",
 *       groupId = "uuid-group",
 *       isGroup = true,
 *       contentEncrypted = "encrypted-message-content",
 *       timestamp = "2024-05-13T14:55:30",
 *       isDeleted = false
 *   )
 *   ```
 *
 * @property id The unique identifier for the message.
 * @property senderId The unique identifier of the sender.
 * @property recipientId The unique identifier of the recipient (if a direct message). Nullable for group messages.
 * @property groupId The unique identifier of the group (if a group message). Nullable for direct messages.
 * @property isGroup True if the message is in a group, false if it's a direct message.
 * @property contentEncrypted The encrypted content of the message.
 * @property timestamp The timestamp when the message was sent. Represented as a string.
 * @property isDeleted Boolean flag indicating whether the message has been marked as deleted.
 * @author Sergiu Chirap
 * @since 0.4
 */
@Serializable
data class MessageResponse(
    val id: String,
    val senderId: String,
    val recipientId: String? = null,
    val groupId: String? = null,
    val isGroup: Boolean,
    val contentEncrypted: String,
    val timestamp: String,
    val isDeleted: Boolean
)

/**
 * Represents the payload required to update a message's content.
 *
 * This data transfer object (DTO) defines the structure of the request body used for updating
 * the content of a message. It ensures that only the new, encrypted content is provided for update.
 *
 * ### Features:
 * - Simple and focused: Contains only the necessary data field for updating a message's content.
 * - Fully serializable with JSON for API integration.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "newContentEncrypted": "new-encrypted-message-content"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = UpdateMessageContentRequest(newContentEncrypted = "new-encrypted-message-content")
 *   ```
 *
 * @property newContentEncrypted The new, encrypted content to update the message with.
 * @author Sergiu Chirap
 * @since 0.4
 */
@Serializable
data class UpdateMessageContentRequest(
    val newContentEncrypted: String
)