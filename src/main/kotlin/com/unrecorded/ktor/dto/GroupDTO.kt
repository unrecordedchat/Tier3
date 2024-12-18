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
 * GroupDTO.kt
 *
 * This file defines the data transfer objects (DTOs) related to group management within the Ktor application.
 * These DTOs are used for both incoming requests and outgoing responses in the group-related API endpoints.
 *
 * General Purpose:
 * - Standardize the data format for creating, updating, and retrieving group information.
 * - Simplify communication between the backend and frontend by clearly defining data structures.
 *
 * Features:
 * - Fully **Serializable**: All DTOs are compatible with Kotlinx Serialization for seamless JSON request/response handling.
 * - **Request Objects**: Specify required structures for group creation and updates.
 * - **Response Objects**: Encapsulate returned group data in a consistent format.
 *
 * DTOs Provided:
 * 1. `CreateGroupRequest`: Represents the payload for creating a new group.
 * 2. `UpdateGroupNameRequest`: Represents the payload for updating a group's name.
 * 3. `GroupResponse`: Represents the structure of a group in API responses.
 *
 * Extensibility:
 * - Additional DTOs can be introduced for more group-related features (e.g., membership management).
 * - Existing DTOs are extendable as new requirements arise, such as optional fields.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Represents the payload required for creating a new group.
 *
 * This data transfer object (DTO) defines the request body structure for group creation in the system.
 * It ensures that the necessary attributes, such as the group name and owner ID, are present.
 *
 * ### Features:
 * - Fully serializable to JSON for RESTful API integration.
 * - Strictly requires the `name` (group name) and `ownerId` (UUID of the group owner).
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "name": "Study Group",
 *     "ownerId": "d2fbc485-91bd-4164-a8f7-4286a983a7b2"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = CreateGroupRequest(
 *       name = "Study Group",
 *       ownerId = "d2fbc485-91bd-4164-a8f7-4286a983a7b2"
 *   )
 *   ```
 *
 * @property name The name of the group to be created. Must be non-empty and follow naming rules.
 * @property ownerId The unique identifier (UUID) of the user creating/owning the group.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class CreateGroupRequest(
    val name: String,
    val ownerId: String
)

/**
 * Represents the payload required for updating a group's name.
 *
 * This data transfer object (DTO) is defined for the request to update the `name` of a specific group.
 * It ensures consistency in API requests when altering group names.
 *
 * ### Features:
 * - Simple validation: Requires a valid new name to be provided in the request.
 * - Fully serializable to JSON for integration with RESTful APIs.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "newName": "Updated Study Group"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = UpdateGroupNameRequest(newName = "Updated Study Group")
 *   ```
 *
 * @property newName The updated name for the group. Must conform to group naming rules.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class UpdateGroupNameRequest(
    val newName: String
)

/**
 * Represents the response structure for group-related API results.
 *
 * This data transfer object (DTO) is used to encapsulate group information when returning data to the client.
 * It ensures that only the relevant and safe fields are exposed.
 *
 * ### Features:
 * - Fully serializable to JSON for seamless integration with RESTful API responses.
 * - Encapsulates essential details such as the group ID, name, and owner ID.
 *
 * ### Usage Example:
 * - **Client JSON Response**:
 *   ```json
 *   {
 *     "id": "47985eef-8e64-4a44-9deb-72b34327ceea",
 *     "name": "Study Group",
 *     "ownerId": "d2fbc485-91bd-4164-a8f7-4286a983a7b2"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val response = GroupResponse(
 *       id = "47985eef-8e64-4a44-9deb-72b34327ceea",
 *       name = "Study Group",
 *       ownerId = "d2fbc485-91bd-4164-a8f7-4286a983a7b2"
 *   )
 *   ```
 *
 * @property id The unique identifier for the group. Exposed as a string.
 * @property name The name of the group.
 * @property ownerId The unique identifier of the group owner. Exposed as a string.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class GroupResponse(
    val id: String?,
    val name: String,
    val ownerId: String
)