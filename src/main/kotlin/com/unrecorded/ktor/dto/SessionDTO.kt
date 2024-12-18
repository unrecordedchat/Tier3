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
 * SessionDTO.kt
 *
 * This file defines the data transfer objects (DTOs) related to session management within the Ktor application. 
 * These DTOs standardize the payload and response structure for session-related API endpoints, simplifying communication
 * between the client and server.
 *
 * General Purpose:
 * - Define structured data formats for creating, retrieving, and responding to session-related API interactions.
 * - Promote consistency and clarity in data exchange for all session operations.
 * 
 * Features:
 * - Fully **Serializable**: Both request and response DTOs are compatible with Kotlinx Serialization for JSON integration.
 * - **Request Objects**: Enforce proper structure and validation of client input when creating new sessions.
 * - **Response Objects**: Restrict exposed fields when sending session data to clients.
 *
 * DTOs Provided:
 * 1. `CreateSessionRequest`: Represents the payload for creating a new session.
 * 2. `SessionResponse`: Represents the output structure used when sending session details to API clients.
 * 
 * Extensibility:
 * - Extend the existing DTOs with optional fields based on future requirements, such as auditing metadata.
 * - Add additional DTOs for advanced session workflows (e.g., bulk session operations).
 *
 * Usage Example:
 * - Request DTO: Used to deserialize incoming JSON requests for creating a session.
 * - Response DTO: Used to serialize session data in JSON responses sent to clients.
 * 
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Represents the payload required for creating a new session.
 *
 * This data transfer object (DTO) defines the structure of the request body when a client initiates the creation
 * of a session. It ensures clarity and consistency during this process by enforcing the required attributes such as
 * `userId`, `token`, and `expiresAt` timestamp.
 *
 * ### Features:
 * - Fully serializable for seamless JSON processing.
 * - Input validation should occur before processing to ensure valid UUIDs and timestamps.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "userId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *     "token": "example-session-token",
 *     "expiresAt": "2024-12-31T23:59:59Z"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = CreateSessionRequest(
 *       userId = "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *       token = "example-session-token",
 *       expiresAt = "2024-12-31T23:59:59Z"
 *   )
 *   ```
 *
 * @property userId A string representation of the user ID (UUID) linked to the session. Cannot be null or empty.
 * @property token Unique token associated with the session. This should be securely generated and stored.
 * @property expiresAt ISO-8601 formatted string representing when the session expires.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class CreateSessionRequest(
    val userId: String,
    val token: String,
    val expiresAt: String
)

/**
 * Represents the response structure for session-related API results.
 *
 * This data transfer object (DTO) encapsulates all necessary information about a session when it is sent
 * back to the client. This ensures that sensitive or irrelevant fields are excluded from the server response.
 *
 * ### Features:
 * - Fully serializable for seamless JSON generation in API responses.
 * - Includes key session details such as session ID, associated user ID, token, and expiry timestamp.
 * - Safe design: Excludes implementation-specific fields (e.g., database-level fields) to prevent leaks.
 *
 * ### Usage Example:
 * - **Client JSON Response**:
 *   ```json
 *   {
 *     "id": "b290f1ee-6c54-4b01-90e6-d701748f0859",
 *     "userId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *     "token": "example-session-token",
 *     "expiresAt": "2024-12-31T23:59:59Z"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val response = SessionResponse(
 *       id = "b290f1ee-6c54-4b01-90e6-d701748f0859",
 *       userId = "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *       token = "example-session-token",
 *       expiresAt = "2024-12-31T23:59:59Z"
 *   )
 *   ```
 *
 * @property id The unique identifier (UUID) of the session. Exposed as a string and nullable for flexibility.
 * @property userId A string representation of the user ID (UUID) linked to the session.
 * @property token Unique token associated with the session.
 * @property expiresAt ISO-8601 formatted string representing the expiration timestamp of the session.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class SessionResponse(
    val id: String?,
    val userId: String,
    val token: String,
    val expiresAt: String
)