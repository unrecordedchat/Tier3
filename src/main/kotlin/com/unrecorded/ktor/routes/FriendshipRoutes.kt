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

import com.unrecorded.database.repositories.IFriendshipRepo
import com.unrecorded.ktor.dto.CreateFriendshipRequest
import com.unrecorded.ktor.dto.FriendshipResponse
import com.unrecorded.ktor.dto.UpdateFriendshipStatusRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/*
 * FriendshipRoutes.kt
 *
 * This file defines the routing logic for handling friendship-related operations in the Ktor application.
 *
 * General Purpose:
 * - Provides RESTful API endpoints for managing friendships between users.
 * - Integrates with the IFriendshipRepo interface to delegate database operations, ensuring separation of routing from persistence logic.
 * 
 * Features:
 * - **Create Friendship**: Establishes a new friendship relationship between two users.
 * - **Retrieve Friendship**: Fetches friendship details based on user IDs.
 * - **Retrieve All Friendships**: Lists all friendships for a given user.
 * - **Update Friendship Status**: Updates the status (e.g., pending, accepted, declined) of an existing friendship.
 * - **Delete Friendship**: Removes a friendship relationship between two users.
 * 
 * Endpoint Summary: 
 * 1. `POST /api/friendships` - Create a new friendship.
 * 2. `GET /api/friendships/{userId1}/{userId2}` - Retrieve a specific friendship.
 * 3. `GET /api/friendships/{userId}` - Retrieve all friendships associated with a user.
 * 4. `PATCH /api/friendships/{userId1}/{userId2}/status` - Update a friendship's status.
 * 5. `DELETE /api/friendships/{userId1}/{userId2}` - Delete a friendship.
 *
 * Extensibility:
 * - New features like responding to friendship requests, blocking users, or paginated friendship retrieval can be added effortlessly.
 * - Provides room for additional security or authentication mechanisms (e.g., JWT-based user validation).
 *
 * Usage:
 * The `friendshipRoutes` function is attached to a `Route` object in Ktor, allowing you to bind friendship-related routes 
 * to the application's routing system.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 * Since: 0.4
 */

/**
 * Defines the routing logic for all friendship-related operations in the Ktor application.
 *
 * This function sets up a group of RESTful API endpoints specifically for managing friendships between users.
 * These endpoints handle the creation, retrieval, update, and deletion of friendship records and provide a standard structure
 * for interacting with the friendship-related database functionality through the `IFriendshipRepo` interface.
 *
 * ### Features:
 * - **Friendship Creation (POST /api/friendships)**:
 *   Creates a new friendship between two users with an initial status (e.g., pending).
 * - **Friendship Retrieval by Composite Key (GET /api/friendships/{userId1}/{userId2})**:
 *   Retrieves the details of a friendship identified by the user IDs.
 * - **All Friendships Retrieval (GET /api/friendships/{userId})**:
 *   Retrieves all friendships associated with a given user's ID.
 * - **Friendship Status Update (PATCH /api/friendships/{userId1}/{userId2}/status)**:
 *   Updates the status of a specific friendship (e.g., from pending to accept).
 * - **Friendship Deletion (DELETE /api/friendships/{userId1}/{userId2})**:
 *   Removes the friendship between two users completely.
 *
 * ### Dependencies:
 * - The function relies on `IFriendshipRepo` to abstract the database operations, making the routes consistent and modular.
 *
 * ### Extensibility:
 * - Additional endpoints can be added for extending functionalities, such as blocking users or categorizing friendships.
 * - Security layers like role-based access control or API key validation can also be integrated seamlessly.
 *
 * ### Usage Example:
 * ```kotlin
 * routing {
 *     friendshipRoutes(friendshipRepo)
 * }
 * ```
 * Here, `friendshipRepo` should be an implementation of the `IFriendshipRepo` interface that handles friendship-related data access.
 *
 * ### Endpoint Details:
 * | HTTP Method | Endpoint                               | Description                          |
 * |-------------|----------------------------------------|--------------------------------------|
 * | POST        | /api/friendships                      | Establish a new friendship.          |
 * | GET         | /api/friendships/{userId1}/{userId2}  | Retrieve details of a friendship.    |
 * | GET         | /api/friendships/{userId}             | Retrieve all friendships for a user. |
 * | PATCH       | /api/friendships/{userId1}/{userId2}/status | Update a friendship's status.  |
 * | DELETE      | /api/friendships/{userId1}/{userId2}  | Delete a friendship relationship.    |
 *
 * ### Benefits:
 * - Simplifies friendship management by providing a unified API layer for the client.
 * - Keeps the routing logic concise, readable, and maintainable, with a clear separation from business logic or persistence logic.
 *
 * @param friendshipRepo The implementation of `IFriendshipRepo`, used for managing friendships in the database.
 * @receiver Route The Ktor route to which the friendship-related endpoints are bound.
 * @author Sergiu Chirap
 * @since 0.4
 * @see IFriendshipRepo
 */
fun Route.friendshipRoutes(friendshipRepo: IFriendshipRepo) {
    route("/api/friendships") {

        // 1. Create a new friendship (POST /api/friendships).
        post {
            val request = call.receive<CreateFriendshipRequest>()
            val userId1 = UUID.fromString(request.userId1)
            val userId2 = UUID.fromString(request.userId2)
            val status = request.status
            try {
                friendshipRepo.createFriendship(userId1, userId2, status)
                call.respond(mapOf("status" to "success", "message" to "Friendship created successfully!"))
            } catch (e: Exception) {
                call.respond(mapOf("error" to e.message))
            }
        }

        // 2. Retrieve a specific friendship by composite key (GET /api/friendships/{userId1}/{userId2}).
        get("/{userId1}/{userId2}") {
            val userId1 = call.parameters["userId1"]?.let(UUID::fromString)
            val userId2 = call.parameters["userId2"]?.let(UUID::fromString)
            if (userId1 != null && userId2 != null) {
                val friendship = friendshipRepo.getFriendship(userId1, userId2)
                if (friendship != null) {
                    call.respond(
                        FriendshipResponse(
                            userId1 = friendship.id.userId1.toString(),
                            userId2 = friendship.id.userId2.toString(),
                            status = friendship.status
                        )
                    )
                } else call.respond(mapOf("error" to "Friendship not found"))
            } else call.respond(mapOf("error" to "Invalid user IDs"))
        }

        // 3. Retrieve all friendships for a user (GET /api/friendships/{userId}).
        get("/{userId}") {
            val userId = call.parameters["userId"]?.let(UUID::fromString)
            if (userId != null) {
                val friendships = friendshipRepo.getFriendshipsForUser(userId)
                if (friendships != null && friendships.isNotEmpty()) {
                    call.respond(friendships.map {
                        FriendshipResponse(
                            userId1 = it.id.userId1.toString(),
                            userId2 = it.id.userId2.toString(),
                            status = it.status
                        )
                    })
                } else call.respond(emptyList<FriendshipResponse>())
            } else call.respond(mapOf("error" to "Invalid user ID"))
        }

        // 4. Update a friendship's status (PATCH /api/friendships/{userId1}/{userId2}/status).
        patch("/{userId1}/{userId2}/status") {
            val userId1 = call.parameters["userId1"]?.let(UUID::fromString)
            val userId2 = call.parameters["userId2"]?.let(UUID::fromString)
            val request = call.receive<UpdateFriendshipStatusRequest>()
            if (userId1 != null && userId2 != null) {
                try {
                    friendshipRepo.updateFriendshipStatus(userId1, userId2, request.status)
                    call.respond(mapOf("status" to "success", "message" to "Friendship status updated successfully!"))
                } catch (e: Exception) {
                    call.respond(mapOf("error" to e.message))
                }
            } else call.respond(mapOf("error" to "Invalid input"))
        }

        // 5. Delete a friendship by composite key (DELETE /api/friendships/{userId1}/{userId2}).
        delete("/{userId1}/{userId2}") {
            val userId1 = call.parameters["userId1"]?.let(UUID::fromString)
            val userId2 = call.parameters["userId2"]?.let(UUID::fromString)
            if (userId1 != null && userId2 != null) {
                try {
                    friendshipRepo.deleteFriendship(userId1, userId2)
                    call.respond(mapOf("status" to "success", "message" to "Friendship deleted successfully!"))
                } catch (e: Exception) {
                    call.respond(mapOf("error" to e.message))
                }
            } else call.respond(mapOf("error" to "Invalid user IDs"))
        }
    }
}