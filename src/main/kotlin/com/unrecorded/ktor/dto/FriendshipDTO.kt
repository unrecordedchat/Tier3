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
 * FriendshipDTO.kt
 *
 * This file defines the data transfer objects (DTOs) for managing friendship-related data within the Ktor application.
 * These DTOs are used for incoming HTTP requests and outgoing HTTP responses related to friendships.
 *
 * General Purpose:
 * - Provides a standardized structure for communication between the frontend and backend in the friendship-related API.
 * - Supports request validation and ensures consistency in JSON serialization/deserialization.
 * 
 * Features:
 * - **Serializable**: All DTOs are compatible with Kotlinx Serialization to handle JSON-based APIs.
 * - **Request DTOs**: Represents the structure for creating friendships or updating friendship statuses.
 * - **Response DTOs**: Encapsulates information about friendship relationships for client consumption.
 * 
 * DTOs Provided:
 * 1. `CreateFriendshipRequest`: Represents the payload structure for creating a new friendship.
 * 2. `FriendshipResponse`: Represents the output structure for friendship-related responses.
 * 3. `UpdateFriendshipStatusRequest`: Represents the payload structure for updating the status of an existing friendship.
 *
 * Extensibility:
 * - Future DTOs can be added to support additional friendship features, such as friendship requests or blocking users.
 * - Each existing DTO can be enhanced with optional fields if the requirements evolve (e.g., timestamps for friendship status updates).
 *
 * Usage:
 * These DTOs are consumed by the friendship-related endpoints in the Ktor routing system. 
 * They act as intermediaries between the frontend payloads and backend logic for creating, retrieving, and updating friendships.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Represents the payload structure for creating a new friendship.
 *
 * This data transfer object (DTO) defines the required fields to establish a new friendship between two users.
 * It ensures that the backend receives all the necessary attributes to process and stores the friendship data.
 *
 * ### Features:
 * - **Serializable**: Fully supports JSON serialization/deserialization for RESTful APIs.
 * - **Validation Required**: The `userId1`, `userId2`, and `status` fields should be validated before processing (e.g., valid UUIDs and status).
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "userId1": "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *     "userId2": "a832f1db-7efe-4b13-bcde-1234567890ab",
 *     "status": "pending"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = CreateFriendshipRequest(
 *       userId1 = "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *       userId2 = "a832f1db-7efe-4b13-bcde-1234567890ab",
 *       status = "pending"
 *   )
 *   ```
 *
 * @property userId1 The unique identifier of the first user in the friendship. Must be a valid UUID.
 * @property userId2 The unique identifier of the second user in the friendship. Must be a valid UUID.
 * @property status The initial status of the friendship (e.g., 'FRD', 'UNK', 'PND').
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class CreateFriendshipRequest(
    val userId1: String,
    val userId2: String,
    val status: String
)

/**
 * Represents the structure of the response sent for friendship operations.
 *
 * This data transfer object (DTO) encapsulates the details of a friendship, ensuring that the client
 * receives only the relevant and safe fields when retrieving or updating friendships.
 *
 * ### Features:
 * - **Serializable**: Fully supports JSON serialization/deserialization for RESTful APIs.
 * - **Client Use Case**: Used in responses for retrieving one or more friendships related to a user.
 *
 * ### Usage Example:
 * - **Client JSON Response**:
 *   ```json
 *   {
 *     "userId1": "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *     "userId2": "a832f1db-7efe-4b13-bcde-1234567890ab",
 *     "status": "accepted"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val response = FriendshipResponse(
 *       userId1 = "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *       userId2 = "a832f1db-7efe-4b13-bcde-1234567890ab",
 *       status = "accepted"
 *   )
 *   ```
 *
 * @property userId1 The unique identifier of the first user in the friendship. Exposed as a string to the client.
 * @property userId2 The unique identifier of the second user in the friendship. Exposed as a string to the client.
 * @property status The current status of the friendship (e.g., 'FRD', 'UNK', 'PND').
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class FriendshipResponse(
    val userId1: String,
    val userId2: String,
    val status: String
)

/**
 * Represents the payload structure for updating the status of an existing friendship.
 *
 * This data transfer object (DTO) defines the strict structure of the request body when changing the
 * status of a friendship. It ensures consistency and validity in backend processing.
 *
 * ### Features:
 * - **Serializable**: Fully supports JSON serialization/deserialization for seamless integration with RESTful APIs.
 * - **Request Validation**: The `status` field should be validated against acceptable values (e.g., 'FRD', 'UNK', 'PND').
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "status": "accepted"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = UpdateFriendshipStatusRequest(
 *       status = "accepted"
 *   )
 *   ```
 *
 * @property status The new status to assign to the friendship (e.g., 'FRD', 'UNK', 'PND').
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class UpdateFriendshipStatusRequest(
    val status: String
)