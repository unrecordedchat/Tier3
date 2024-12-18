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

import com.unrecorded.database.repositories.IGroupRepo
import com.unrecorded.ktor.dto.CreateGroupRequest
import com.unrecorded.ktor.dto.GroupResponse
import com.unrecorded.ktor.dto.UpdateGroupNameRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/*
 * GroupRoutes.kt
 *
 * This file defines the routing logic for group-related operations in the Ktor application.
 *
 * General Purpose:
 * - To provide RESTful API endpoints for managing groups within the system.
 * - Integrate with the `IGroupRepo` interface to interact with the database, ensuring a clean separation
 *   of routing logic and database access.
 *
 * Features:
 * - **Create Group**: Allows creation of new groups with required attributes.
 * - **Retrieve Group**: Fetches group details by ID or by owner.
 * - **Update Group Name**: Handles updates to a group's name.
 * - **Delete Group**: Implements group removal functionality (soft delete).
 * - **Transfer Ownership**: Allows transferring a group to a new owner.
 *
 * Endpoint Summary:
 * 1. `POST /api/groups` - Create a new group.
 * 2. `GET /api/groups/{id}` - Retrieve a group by ID.
 * 3. `GET /api/groups/owner/{ownerId}` - Retrieve groups owned by the user.
 * 4. `PATCH /api/groups/{id}/name` - Update a group's name.
 * 5. `PATCH /api/groups/{id}/owner` - Transfer group ownership.
 * 6. `DELETE /api/groups/{id}` - Delete a group.
 *
 * Extensibility:
 * - Add new group-related operations such as membership management or detailed group statistics.
 * - Customize response formatting for client-specific requirements.
 *
 * Usage:
 * The `groupRoutes` function is attached to a `Route` object in Ktor, allowing seamless integration
 * with the application's routing module.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Defines the routing logic for all group-related operations in the Ktor application.
 *
 * This function sets up RESTful API endpoints for managing groups, including creating, retrieving,
 * updating, and deleting group records. It also includes functionality for transferring ownership
 * of a group.
 *
 * ### Features:
 * - **Group Creation (POST /api/groups)**:
 *   Creates a new group with required fields such as `name` and `ownerId`.
 * - **Group Retrieval by ID (GET /api/groups/{id})**:
 *   Fetches group details using its unique identifier.
 * - **Groups Retrieval by Owner (GET /api/groups/owner/{ownerId})**:
 *   Fetches all groups owned by a specific user.
 * - **Group Name Update (PATCH /api/groups/{id}/name)**:
 *   Updates a group's name.
 * - **Group Ownership Transfer (PATCH /api/groups/{id}/owner)**:
 *   Transfers a group to a new owner.
 * - **Soft Delete Group (DELETE /api/groups/{id})**:
 *   Deletes a group from the system (marks it inactive).
 *
 * ### Dependencies:
 * - Relies on `IGroupRepo` to abstract database operations, ensuring separation of concerns.
 *
 * ### Usage:
 * Attach the `groupRoutes` function to a `Routing` object as follows:
 * ```kotlin
 * routing {
 *     groupRoutes(groupRepo)
 * }
 * ```
 * Replace `groupRepo` with an implementation of the `IGroupRepo` interface.
 *
 * ### Benefits:
 * - Provides a consistent interface for group-related API operations.
 * - Simplifies back-end logic and promotes modularity in the project.
 *
 * @param groupRepo An implementation of the `IGroupRepo` interface used to access the database.
 * @receiver Route The Ktor route to which the group-related endpoints are registered.
 * @author Sergiu Chirap
 * @since 0.4
 * @see IGroupRepo
 */
fun Route.groupRoutes(groupRepo: IGroupRepo) {
    route("/api/groups") {

        // 1. Create a new group (POST /api/groups).
        post {
            val request = call.receive<CreateGroupRequest>()
            groupRepo.createGroup(request.name, UUID.fromString(request.ownerId))
            call.respond(mapOf("status" to "success", "message" to "Group created successfully!"))
        }

        // 2. Retrieve a group by its ID (GET /api/groups/{id}).
        get("/{id}") {
            val groupId = call.parameters["id"]?.let(UUID::fromString)
            if (groupId != null) {
                val group = groupRepo.getGroupById(groupId)
                if (group != null) { call.respond(GroupResponse(
                            id = group.id?.toString(),
                            name = group.name,
                            ownerId = group.ownerId.toString()
                        )
                    )
                } else call.respond(mapOf("error" to "Group not found"))
            } else call.respond(mapOf("error" to "Invalid group ID"))
        }

        // 3. Retrieve groups owned by a specific user (GET /api/groups/owner/{ownerId}).
        get("/owner/{ownerId}") {
            val ownerId = call.parameters["ownerId"]?.let(UUID::fromString)
            if (ownerId != null) {
                val groups = groupRepo.getGroupsByOwner(ownerId) ?: emptyList()
                call.respond(groups.map {
                    GroupResponse(
                        id = it.id?.toString(),
                        name = it.name,
                        ownerId = it.ownerId.toString()
                    )
                }
                )
            } else call.respond(mapOf("error" to "Invalid owner ID"))
        }

        // 4. Update a group's name (PATCH /api/groups/{id}/name).
        patch("/{id}/name") {
            val groupId = call.parameters["id"]?.let(UUID::fromString)
            val request = call.receive<UpdateGroupNameRequest>()
            if (groupId != null) {
                groupRepo.updateGroupName(groupId, request.newName)
                call.respond(mapOf("status" to "success", "message" to "Group name updated successfully!"))
            } else call.respond(mapOf("error" to "Invalid group ID"))
        }

        // 5. Transfer group ownership (PATCH /api/groups/{id}/owner).
        patch("/{id}/owner") {
            val groupId = call.parameters["id"]?.let(UUID::fromString)
            val newOwnerId = call.receive<Map<String, String>>()["newOwnerId"]?.let(UUID::fromString)
            if (groupId != null && newOwnerId != null) {
                groupRepo.updateGroupOwner(groupId, newOwnerId)
                call.respond(mapOf("status" to "success", "message" to "Group ownership transferred successfully!"))
            } else call.respond(mapOf("error" to "Invalid group ID or new owner ID"))
        }

        // 6. Delete a group (DELETE /api/groups/{id}).
        delete("/{id}") {
            val groupId = call.parameters["id"]?.let(UUID::fromString)
            if (groupId != null) {
                groupRepo.deleteGroup(groupId)
                call.respond(mapOf("status" to "success", "message" to "Group deleted successfully!"))
            } else call.respond(mapOf("error" to "Invalid group ID"))
        }
    }
}