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

import com.unrecorded.database.repositories.IReactionRepo
import com.unrecorded.ktor.dto.ReactionRequest
import com.unrecorded.ktor.dto.ReactionResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/*
 * ReactionRoutes.kt
 *
 * This file defines the routing logic for reaction-related operations in the Ktor application.
 *
 * General Purpose:
 * - Provides RESTful API endpoints for managing reactions to messages within the system.
 * - Integrates with the IReactionRepo interface to interact with the database, maintaining a clean separation between routing logic and data access.
 *
 * Features:
 * - **Create Reaction**: Allows users to react to messages using emojis.
 * - **Retrieve Reactions**: Fetches individual reactions or lists of reactions associated with a message.
 * - **Delete Reaction**: Enables removing a specific reaction.
 *
 * Endpoints:
 * 1. `POST /api/reactions` - Create a new reaction to a message.
 * 2. `GET /api/reactions/{messageId}` - Retrieve all reactions for a given message.
 * 3. `DELETE /api/reactions` - Remove a specific reaction based on composite keys (userId, messageId, emoji).
 *
 * Extensibility:
 * - Additional features that can be built upon this framework include advanced reaction analytics, real-time updates, etc.
 *
 * Usage:
 * The `reactionRoutes` function is attached to the `Route` object in Ktor, allowing seamless integration
 * with the application's routing module.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Defines the routing logic for all reaction-related operations in the Ktor application.
 *
 * This function sets up RESTful API endpoints for managing reactions, including creating, retrieving,
 * and deleting reactions. The routing logic interacts with the `IReactionRepo` interface to perform database operations.
 *
 * ### Features:
 * - **Creation (POST /api/reactions)**: Allows users to add a reaction to a message.
 * - **Retrieval (GET /api/reactions/{messageId})**: Retrieves all reactions for a given message.
 * - **Deletion (DELETE /api/reactions)**: Deletes a specific reaction.
 *
 * ### Extensibility:
 * - This can be enhanced to include reaction analytics or support for bulk operations.
 *
 * ### Usage Example:
 * Attach the `reactionRoutes` function to a `Routing` object:
 * ```kotlin
 * routing {
 *     reactionRoutes(reactionRepo)
 * }
 * ```
 * Replace `reactionRepo` with an implementation of the `IReactionRepo` interface.
 *
 * @param reactionRepo An implementation of the `IReactionRepo` interface, used to access the reaction database.
 * @receiver Route The Ktor route object where the reaction-related endpoints are registered.
 * @author Sergiu Chirap
 * @since 0.4
 */
fun Route.reactionRoutes(reactionRepo: IReactionRepo) {
    route("/api/reactions") {

        // 1. Create a new reaction (POST /api/reactions).
        post {
            val request = call.receive<ReactionRequest>()
            val userId = UUID.fromString(request.userId)
            val messageId = UUID.fromString(request.messageId)
            reactionRepo.createReaction(userId, messageId, request.emoji)
            call.respond(mapOf("status" to "success", "message" to "Reaction created successfully!"))
        }

        // 2. Retrieve all reactions for a message (GET /api/reactions/{messageId}).
        get("/{messageId}") {
            val messageId = call.parameters["messageId"]?.let(UUID::fromString) ?: throw IllegalArgumentException("Invalid message ID format.")
            val reactions = reactionRepo.getReactionsForMessage(messageId)
            if (reactions.isNullOrEmpty()) call.respond(mapOf("status" to "error", "message" to "No reactions found for the provided message ID."))
            else call.respond(reactions.map {
                ReactionResponse(
                    userId = it.id.userId.toString(),
                    messageId = it.id.messageId.toString(),
                    emoji = it.id.emoji
                )
            })
        }

        // 3. Delete a reaction (DELETE /api/reactions).
        delete {
            val request = call.receive<ReactionRequest>()
            val userId = UUID.fromString(request.userId)
            val messageId = UUID.fromString(request.messageId)
            reactionRepo.deleteReaction(userId, messageId, request.emoji)
            call.respond(mapOf("status" to "success", "message" to "Reaction deleted successfully!"))
        }
    }
}