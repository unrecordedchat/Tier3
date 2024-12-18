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
 * UserDTO.kt
 *
 * This file defines the data transfer objects (DTOs) related to user management within the Ktor application.
 * These DTOs are used for both incoming requests and outgoing responses in the user-related API endpoints.
 *
 * General Purpose:
 * - Standardize the data format for creating new users, updating user information, and responding with user details.
 * - Simplify the communication between the frontend and backend by providing a clear contract of expected/requested data.
 *
 * Features:
 * - Fully **Serializable**: All DTOs are compatible with Kotlinx Serialization to ensure seamless integration for JSON request/response handling.
 * - **Request Objects**: Define the strict structure required for API interactions when creating or updating user-related resources.
 * - **Response Objects**: Structure the returned data in user retrieval endpoints, exposing only the necessary fields to clients.
 *
 * DTOs Provided:
 * 1. `CreateUserRequest`: Represents the payload for creating a new user.
 * 2. `UserResponse`: Represents the output structure used when sending user information to API clients.
 * 3. `UpdateUsernameRequest`: Represents the payload for updating a user's username.
 * 4. `UpdateEmailRequest`: Represents the payload for updating a user's email address.
 *
 * Extensibility:
 * - Additional DTOs can be added as required for other user management functionalities (e.g., changing the password or resetting the account).
 * - Each existing DTO can be extended with optional fields if new requirements arise.
 *
 * Usage Example:
 * These DTOs are part of the user management RESTful API and enable well-defined data exchanges between clients and the server.
 * - Request (JSON to DTO conversion): Used for incoming client payloads such as user creation or updates.
 * - Response (DTO to JSON conversion): Used for returning consistent user-related data to API clients.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */


/**
 * Represents the payload required for creating a new user.
 *
 * This data transfer object (DTO) defines the structure of the request body when creating new users in the system.
 * It ensures the backend receives all the necessary attributes to register a user.
 *
 * ### Features:
 * - Fully serializable to JSON for integration with RESTful APIs.
 * - Contains essential user attributes, including credentials, email, and cryptographic keys.
 * - Validation should be applied before processing the request (e.g., ensure non-empty values and correct formats).
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "username": "john_doe",
 *     "password": "securepassword",
 *     "email": "john.doe@example.com",
 *     "publicKey": "rsa-public-key",
 *     "privateKeyEncrypted": "encrypted-private-key"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = CreateUserRequest(
 *       username = "john_doe",
 *       password = "securepassword",
 *       email = "john.doe@example.com",
 *       publicKey = "rsa-public-key",
 *       privateKeyEncrypted = "encrypted-private-key"
 *   )
 *   ```
 *
 * @property username The username of the new user. Must be unique and non-empty.
 * @property password The password of the new user. Should meet complexity requirements.
 * @property email The user's email address. Must follow a valid email format.
 * @property publicKey The user's public key for cryptographic purposes.
 * @property privateKeyEncrypted The user's encrypted private key string for secure storage.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val email: String,
    val publicKey: String,
    val privateKeyEncrypted: String
)

/**
 * Represents the response structure for user-related API results.
 *
 * This data transfer object (DTO) is used to encapsulate user information returned from
 * the server to the client. It ensures the exposure of only required and safe fields to the client while omitting sensitive data.
 *
 * ### Features:
 * - Fully serializable to JSON for usage in RESTful responses.
 * - Includes user-specific details such as `id`, `username`, `emailAddress`, and `publicKey`.
 *
 * ### Usage Example:
 * - **Client JSON Response**:
 *   ```json
 *   {
 *     "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *     "username": "john_doe",
 *     "emailAddress": "john.doe@example.com",
 *     "publicKey": "rsa-public-key"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val response = UserResponse(
 *       id = "d290f1ee-6c54-4b01-90e6-d701748f0851",
 *       username = "john_doe",
 *       emailAddress = "john.doe@example.com",
 *       publicKey = "rsa-public-key"
 *   )
 *   ```
 *
 * @property id The unique identifier for the user. Exposed as a string.
 * @property username The user's username. Must be unique and non-empty.
 * @property emailAddress The user's email address. Should follow a valid email format.
 * @property publicKey The user's public key for secure communication.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val emailAddress: String,
    val publicKey: String
)

/**
 * Represents the payload required for updating a user's username.
 *
 * This data transfer object (DTO) defines the request body structure when a user wants to update their username.
 * It ensures consistency and usability within API interactions.
 *
 * ### Features:
 * - Simple validation: Ensures the new username is provided within the body of the request.
 * - Fully serializable with JSON for integration with RESTful APIs.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "username": "new_username"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = UpdateUsernameRequest(username = "new_username")
 *   ```
 *
 * @property username The new username for the user. Must be non-empty and unique.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class UpdateUsernameRequest(
    val username: String
)

/**
 * Represents the payload required for updating a user's email address.
 *
 * This data transfer object (DTO) defines the structure of the request body used for email updates.
 * It ensures consistency in API requests.
 *
 * ### Features:
 * - Simple validation: Requires a valid email address to be included in the request body.
 * - Fully serializable with JSON for API integration.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "email": "new.email@example.com"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = UpdateEmailRequest(email = "new.email@example.com")
 *   ```
 *
 * @property email The new email address for the user. Must follow a valid email format.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class UpdateEmailRequest(
    val email: String
)