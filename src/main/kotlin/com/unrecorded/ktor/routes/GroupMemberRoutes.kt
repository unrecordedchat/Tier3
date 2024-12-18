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

import com.unrecorded.database.repositories.IGroupMemberRepo
import com.unrecorded.ktor.dto.AddGroupMemberRequest
import com.unrecorded.ktor.dto.GroupMemberResponse
import com.unrecorded.ktor.dto.UpdateGroupMemberRoleRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/*
 * GroupMemberRoutes.kt
 *
 * This file defines the routing logic for group-member-related operations in the Ktor application.
 *
 * General Purpose:
 * - Provide RESTful API endpoints for managing group memberships.
 * - Integrates with the `IGroupMemberRepo` interface to interact with the database, ensuring separation of routing logic and data access.
 *
 * Features:
 * - **Add Member to Group**: Add a new membership with a specific role to a group.
 * - **Get Members of Group**: Retrieve all members of a specific group.
 * - **Get Groups by User**: Retrieve all groups a user belongs to.
 * - **Update Member Role**: Change the role of a specific member within a group.
 * - **Remove Member from Group**: Remove a user's membership from a specific group.
 *
 * Extensibility:
 * - Add new group-member-related operations, such as admin-only permissions or detailed role-based access rules.
 * - Customize responses to include additional data as needed by clients.
 *
 * Endpoint Summary:
 * 1. `POST /api/group-members` - Add a new member to a group.
 * 2. `GET /api/group-members/group/{groupId}` - Get all members of a group.
 * 3. `GET /api/group-members/user/{userId}` - Get all groups a user belongs to.
 * 4. `PATCH /api/group-members/{groupId}/user/{userId}/role` - Update a member's role.
 * 5. `DELETE /api/group-members/{groupId}/user/{userId}` - Remove a member from a group.
 *
 * Usage:
 * The `groupMemberRoutes` function is attached to a `Route` object in Ktor, enabling easy integration in the Ktor application's routing module.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Defines the routing logic for all group-member-related operations in the Ktor application.
 *
 * This function sets up RESTful API endpoints for managing group memberships, including adding, fetching,
 * updating, and removing members. It interacts with the `IGroupMemberRepo` interface for database operations.
 *
 * ### Features:
 * - **Add Member to Group (POST /api/group-members)**:
 *   Creates a new membership between a user and a group, with a specified role.
 * - **Get Members of Group (GET /api/group-members/group/{groupId})**:
 *   Fetches all users in a specified group.
 * - **Get Groups by User (GET /api/group-members/user/{userId})**:
 *   Fetches all groups the specified user is a member of.
 * - **Update Member Role (PATCH /api/group-members/{groupId}/user/{userId}/role)**:
 *   Updates the role of a specific group member.
 * - **Remove Member from Group (DELETE /api/group-members/{groupId}/user/{userId})**:
 *   Removes a user from a group.
 *
 * ### Dependencies:
 * - The `IGroupMemberRepo` interface is used for interacting with the database, ensuring a clean separation
 *   of concerns between routing logic and data access.
 *
 * ### Usage Example:
 * Attach the `groupMemberRoutes` function to a `Routing` object as follows:
 * ```kotlin
 * routing {
 *     groupMemberRoutes(groupMemberRepo)
 * }
 * ```
 * Replace `groupMemberRepo` with your implementation of the `IGroupMemberRepo` interface.
 *
 * ### Endpoint Details:
 * | HTTP Method | Endpoint                                 | Description                        |
 * |-------------|------------------------------------------|------------------------------------|
 * | POST        | /api/group-members                      | Add a member to a group.          |
 * | GET         | /api/group-members/group/{groupId}      | Get all members of a group.       |
 * | GET         | /api/group-members/user/{userId}        | Get all groups a user belongs to. |
 * | PATCH       | /api/group-members/{groupId}/user/{userId}/role | Update a member's role.   |
 * | DELETE      | /api/group-members/{groupId}/user/{userId} | Remove a member from a group.    |
 *
 * @param groupMemberRepo An implementation of the `IGroupMemberRepo` interface, used for group-member data access.
 * @receiver Route The Ktor route to which the group-member endpoints are registered.
 * @author Sergiu Chirap
 * @since 0.4
 * @see IGroupMemberRepo
 */
fun Route.groupMemberRoutes(groupMemberRepo: IGroupMemberRepo) {
    route("/api/group-members") {

        // 1. Add a new member to a group (POST /api/group-members).
        post {
            val request = call.receive<AddGroupMemberRequest>()
            groupMemberRepo.addMemberToGroup(
                UUID.fromString(request.groupId),
                UUID.fromString(request.userId),
                request.role
            )
            call.respond(mapOf("status" to "success", "message" to "Member added successfully!"))
        }

        // 2. Get all members of a group (GET /api/group-members/group/{groupId}).
        get("/group/{groupId}") {
            val groupId = call.parameters["groupId"]?.let(UUID::fromString)
            if (groupId != null) {
                val members = groupMemberRepo.getMembersByGroupId(groupId)
                if (members != null) {
                    call.respond(members.map {
                        GroupMemberResponse(
                            groupId = it.id.groupId.toString(),
                            userId = it.id.userId.toString(),
                            role = it.role
                        )
                    })
                } else call.respond(emptyList<GroupMemberResponse>())
            } else call.respond(mapOf("error" to "Invalid group ID"))
        }

        // 3. Get all groups a user belongs to (GET /api/group-members/user/{userId}).
        get("/user/{userId}") {
            val userId = call.parameters["userId"]?.let(UUID::fromString)
            if (userId != null) {
                val memberships = groupMemberRepo.getGroupsByUserId(userId)
                if (memberships != null) {
                    call.respond(memberships.map {
                        GroupMemberResponse(
                            groupId = it.id.groupId.toString(),
                            userId = it.id.userId.toString(),
                            role = it.role
                        )
                    })
                } else call.respond(emptyList<GroupMemberResponse>())
            } else call.respond(mapOf("error" to "Invalid user ID"))
        }

        // 4. Update a member's role (PATCH /api/group-members/{groupId}/user/{userId}/role).
        patch("/{groupId}/user/{userId}/role") {
            val groupId = call.parameters["groupId"]?.let(UUID::fromString)
            val userId = call.parameters["userId"]?.let(UUID::fromString)
            val request = call.receive<UpdateGroupMemberRoleRequest>()
            if (groupId != null && userId != null) {
                groupMemberRepo.updateMemberRole(groupId, userId, request.newRole)
                call.respond(mapOf("status" to "success", "message" to "Role updated successfully!"))
            } else call.respond(mapOf("error" to "Invalid group ID or user ID"))
        }

        // 5. Remove a member from a group (DELETE /api/group-members/{groupId}/user/{userId}).
        delete("/{groupId}/user/{userId}") {
            val groupId = call.parameters["groupId"]?.let(UUID::fromString)
            val userId = call.parameters["userId"]?.let(UUID::fromString)
            if (groupId != null && userId != null) {
                groupMemberRepo.removeMemberFromGroup(groupId, userId)
                call.respond(mapOf("status" to "success", "message" to "Member removed successfully!"))
            } else call.respond(mapOf("error" to "Invalid group ID or user ID"))
        }
    }
}