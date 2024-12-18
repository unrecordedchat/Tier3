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
 * GroupMemberDTOs.kt
 *
 * This file defines the data transfer objects (DTOs) related to group-member management within the Ktor application.
 * These DTOs are used for both incoming requests and outgoing responses in the group-member-related API endpoints.
 *
 * General Purpose:
 * - Standardize the data format for adding group members, updating roles, and retrieving membership details.
 * - Simplify the communication between the frontend and backend by providing a clear contract of expected/requested data.
 *
 * Features:
 * - Fully **Serializable**: All DTOs are compatible with Kotlinx Serialization to ensure seamless integration for JSON request/response handling.
 * - **Request Objects**: Define strict structures required for API interactions.
 * - **Response Objects**: Structure the returned data in group-member retrieval endpoints, exposing only the necessary fields to clients.
 *
 * DTOs Provided:
 * 1. `AddGroupMemberRequest`: Represents the payload for adding a new member to a group.
 * 2. `GroupMemberResponse`: Represents the output structure used when sending group member information to API clients.
 * 3. `UpdateGroupMemberRoleRequest`: Represents the payload for updating the role of a group member.
 *
 * Extensibility:
 * - Additional DTOs can be added for other group-member management functionalities if needed.
 * - Existing DTOs can be extended with optional fields as required.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Represents the payload required for adding a new member to a group.
 *
 * This data transfer object (DTO) defines the structure of the request body when adding a new member
 * to a group in the system. It ensures the backend receives all necessary attributes.
 *
 * ### Features:
 * - Fully serializable with JSON for integration with RESTful APIs.
 * - Contains essential attributes like the `groupId`, `userId`, and `role`.
 * - Should be validated before processing the request (e.g., non-empty values, valid UUIDs, and valid roles).
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "groupId": "c91f34b6-8b8e-42ed-bf4f-b1cc84034f96",
 *     "userId": "5c632f11-08f3-4ac5-996a-832d41ec91a3",
 *     "role": "MEMBER"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = AddGroupMemberRequest(
 *       groupId = "c91f34b6-8b8e-42ed-bf4f-b1cc84034f96",
 *       userId = "5c632f11-08f3-4ac5-996a-832d41ec91a3",
 *       role = "MEMBER"
 *   )
 *   ```
 *
 * @property groupId The unique identifier of the group. Must be a valid UUID.
 * @property userId The unique identifier of the user being added to the group. Must be a valid UUID.
 * @property role The role assigned to the user in the group (e.g., ADMIN, MEMBER). Must be a valid string.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class AddGroupMemberRequest(
    val groupId: String,
    val userId: String,
    val role: String
)

/**
 * Represents the response structure for group-member-related API results.
 *
 * This data transfer object (DTO) is used to encapsulate group membership information returned from
 * the server to the client. It ensures the exposure of only required and safe fields to the client.
 *
 * ### Features:
 * - Fully serializable with JSON for usage in RESTful responses.
 * - Includes details like `groupId`, `userId`, and `role`.
 *
 * ### Usage Example:
 * - **Client JSON Response**:
 *   ```json
 *   {
 *     "groupId": "c91f34b6-8b8e-42ed-bf4f-b1cc84034f96",
 *     "userId": "5c632f11-08f3-4ac5-996a-832d41ec91a3",
 *     "role": "ADMIN"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val response = GroupMemberResponse(
 *       groupId = "c91f34b6-8b8e-42ed-bf4f-b1cc84034f96",
 *       userId = "5c632f11-08f3-4ac5-996a-832d41ec91a3",
 *       role = "ADMIN"
 *   )
 *   ```
 *
 * @property groupId The unique identifier of the group. Exposed as a string.
 * @property userId The unique identifier of the user in the group. Exposed as a string.
 * @property role The role assigned to the user in the group (e.g., ADMIN, MEMBER). Exposed as a string.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class GroupMemberResponse(
    val groupId: String,
    val userId: String,
    val role: String
)

/**
 * Represents the payload required for updating a group member's role.
 *
 * This data transfer object (DTO) defines the request body structure for updating the role of a group member.
 * It ensures consistency in API interactions when modifying a user's membership role.
 *
 * ### Features:
 * - Simple validation: Expects a valid new role in the request body.
 * - Fully serializable with JSON for seamless API usage.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "newRole": "ADMIN"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = UpdateGroupMemberRoleRequest(newRole = "ADMIN")
 *   ```
 *
 * @property newRole The new role for the group member (e.g., ADMIN, MEMBER). Must be a valid string.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class UpdateGroupMemberRoleRequest(
    val newRole: String
)