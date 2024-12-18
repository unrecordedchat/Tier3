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

import com.unrecorded.database.repositories.IMessageRepo
import com.unrecorded.ktor.dto.CreateMessageRequest
import com.unrecorded.ktor.dto.MessageResponse
import com.unrecorded.ktor.dto.UpdateMessageContentRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/*
 * MessageRoutes.kt
 *
 * This file defines the routing logic for message-related operations in the Ktor application.
 *
 * General Purpose:
 * - To provide RESTful API endpoints for managing messages within the system.
 * - Integrates with the IMessageRepo interface to interact with the database, ensuring a clean separation of concerns.
 * 
 * Features:
 * - **Create Message**: Allows the creation of both group and direct messages.
 * - **Retrieve Message by ID**: Fetches message details via its unique ID.
 * - **List Messages by Sender/Recipient**: Retrieves messages between two users.
 * - **List Messages by Group**: Fetches all messages associated with a group.
 * - **Update Message Content**: Allows modification of an existing message's contents.
 * - **Delete Message**: Supports both hard and soft deletion of messages.
 * 
 * Endpoint Summary:
 * 1. `POST /api/messages` - Create a new message.
 * 2. `GET /api/messages/{id}` - Retrieve a message by ID.
 * 3. `GET /api/messages/users/{senderId}/{recipientId}` - List direct messages between two users.
 * 4. `GET /api/messages/groups/{groupId}` - List group messages by group ID.
 * 5. `PATCH /api/messages/{id}` - Update the content of a specified message.
 * 6. `DELETE /api/messages/{id}` - Permanently delete a message.
 * 7. `POST /api/messages/{id}/softDelete` - Mark a message as deleted.
 *
 * Extensibility:
 * - Add features like message attachments, pinned messages, or reactions to messages.
 * - Integrate advanced filtering and search capabilities.
 *
 * Usage:
 * The `messageRoutes` function is attached to a `Route` object in Ktor, allowing seamless integration
 * with the application's routing module.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Defines the routing logic for all message-related operations in the Ktor application.
 *
 * This function sets up RESTful API endpoints for creating, retrieving, updating, and deleting messages,
 * as well as fetching messages exchanged between users or sent to a group. The database operations are
 * abstracted via the `IMessageRepo` interface, ensuring maintainable and testable routing logic.
 *
 * ### Features:
 * - **Create Message (POST /api/messages)**:
 *   Creates a new message, supporting both group and direct messaging scenarios.
 * - **Retrieve Message by ID (GET /api/messages/{id})**:
 *   Fetches message details by its unique identifier.
 * - **List User Messages (GET /api/messages/users/{senderId}/{recipientId})**:
 *   Retrieves all messages exchanged between two users.
 * - **List Group Messages (GET /api/messages/groups/{groupId})**:
 *   Fetches all messages sent to a specific group.
 * - **Update Message (PATCH /api/messages/{id})**:
 *   Allows modifying the content of a message.
 * - **Delete Message (DELETE /api/messages/{id})**:
 *   Permanently deletes a message from the database.
 * - **Soft Delete Message (POST /api/messages/{id}/softDelete)**:
 *   Marks a message as deleted without fully removing it from the database.
 *
 * ### Endpoint Details:
 * | HTTP Method | Endpoint                              | Description                               |
 * |-------------|---------------------------------------|-------------------------------------------|
 * | POST        | /api/messages                         | Create a new message.                    |
 * | GET         | /api/messages/{id}                    | Retrieve a message by ID.                |
 * | GET         | /api/messages/users/{senderId}/{recipientId} | List messages exchanged between users.   |
 * | GET         | /api/messages/groups/{groupId}        | List messages in a group.                |
 * | PATCH       | /api/messages/{id}                    | Update an existing message's content.    |
 * | DELETE      | /api/messages/{id}                    | Delete a message from the database.      |
 * | POST        | /api/messages/{id}/softDelete         | Soft delete a message.                   |
 *
 * ### Dependencies:
 * The function depends on an implementation of the `IMessageRepo` interface, which
 * handles all database operations related to messages.
 *
 * ### Extensibility:
 * - Additional endpoints can be added for advanced messaging features, such as analytics or threads.
 * - Security mechanisms, such as token validation (e.g., JWT), can be integrated into the routes.
 *
 * ### Usage Example:
 * Attach the `messageRoutes` function to a `Routing` object:
 * ```kotlin
 * routing {
 *     messageRoutes(messageRepo)
 * }
 * ```
 * Replace `messageRepo` with a proper implementation of the `IMessageRepo` interface.
 *
 * ### Benefits:
 * - Centralized routing logic for managing message-related operations.
 * - Clean separation between the routing layer and the database layer.
 * - Provides clear and consistent API contract for external clients.
 *
 * @param messageRepo An implementation of the `IMessageRepo` interface used to abstract all database operations.
 * @receiver Route The Ktor route to which the message-related endpoints are registered.
 * @author Sergiu Chirap
 * @since 0.4
 */
fun Route.messageRoutes(messageRepo: IMessageRepo) {
    route("/api/messages") {

        // 1. Create a new message (POST /api/messages).
        post {
            val request = call.receive<CreateMessageRequest>()
            messageRepo.createMessage(
                UUID.fromString(request.senderId),
                request.recipientId?.let(UUID::fromString),
                request.groupId?.let(UUID::fromString),
                request.isGroup,
                request.contentEncrypted
            )
            call.respond(mapOf("status" to "success", "message" to "Message created successfully!"))
        }

        // 2. Retrieve a message by its ID (GET /api/messages/{id}).
        get("/{id}") {
            val messageId = call.parameters["id"]?.let(UUID::fromString)
            if (messageId != null) {
                val message = messageRepo.getMessageById(messageId)
                if (message != null) {
                    call.respond(
                        MessageResponse(
                            id = message.id.toString(),
                            senderId = message.sender.toString(),
                            recipientId = message.recipientId?.toString(),
                            groupId = message.groupId?.toString(),
                            isGroup = message.isGroup,
                            contentEncrypted = message.contentEncrypted,
                            timestamp = message.timestamp.toString(),
                            isDeleted = message.isDeleted
                        )
                    )
                } else call.respond(mapOf("error" to "Message not found"))
            } else call.respond(mapOf("error" to "Invalid message ID"))
        }

        // 3. List all messages between two users (GET /api/messages/users/{senderId}/{recipientId}).
        get("/users/{senderId}/{recipientId}") {
            val senderId = call.parameters["senderId"]?.let(UUID::fromString)
            val recipientId = call.parameters["recipientId"]?.let(UUID::fromString)
            if (senderId != null && recipientId != null) {
                val messages = messageRepo.getAllMessagesBetweenUsers(senderId, recipientId)
                if (messages != null && messages.isNotEmpty()) {
                    call.respond(messages.map {
                        MessageResponse(
                            id = it.id.toString(),
                            senderId = it.sender.toString(),
                            recipientId = it.recipientId?.toString(),
                            groupId = it.groupId?.toString(),
                            isGroup = it.isGroup,
                            contentEncrypted = it.contentEncrypted,
                            timestamp = it.timestamp.toString(),
                            isDeleted = it.isDeleted
                        )
                    })
                } else call.respond(mapOf("status" to "success", "message" to "No messages found between the specified users"))
            } else call.respond(mapOf("error" to "Invalid sender or recipient ID"))
        }

        // 4. List all messages in a group (GET /api/messages/groups/{groupId}).
        get("/groups/{groupId}") {
            val groupId = call.parameters["groupId"]?.let(UUID::fromString)
            if (groupId != null) {
                val messages = messageRepo.getAllMessagesForGroup(groupId)
                if (messages != null && messages.isNotEmpty()) {
                    call.respond(messages.map {
                        MessageResponse(
                            id = it.id.toString(),
                            senderId = it.sender.toString(),
                            recipientId = it.recipientId?.toString(),
                            groupId = it.groupId?.toString(),
                            isGroup = it.isGroup,
                            contentEncrypted = it.contentEncrypted,
                            timestamp = it.timestamp.toString(),
                            isDeleted = it.isDeleted
                        )
                    })
                } else call.respond(mapOf("status" to "success", "message" to "No messages found for this group"))
            } else call.respond(mapOf("error" to "Invalid group ID"))
        }

        // 5. Update message content (PATCH /api/messages/{id}).
        patch("/{id}") {
            val messageId = call.parameters["id"]?.let(UUID::fromString)
            val request = call.receive<UpdateMessageContentRequest>()
            if (messageId != null) {
                messageRepo.updateMessageContent(messageId, request.newContentEncrypted)
                call.respond(mapOf("status" to "success", "message" to "Message updated successfully!"))
            } else call.respond(mapOf("error" to "Invalid message ID"))
        }

        // 6. Permanently delete a message (DELETE /api/messages/{id}).
        delete("/{id}") {
            val messageId = call.parameters["id"]?.let(UUID::fromString)
            if (messageId != null) {
                messageRepo.deleteMessage(messageId)
                call.respond(mapOf("status" to "success", "message" to "Message deleted successfully!"))
            } else call.respond(mapOf("error" to "Invalid message ID"))
        }

        // 7. Soft delete a message (POST /api/messages/{id}/softDelete).
        post("/{id}/softDelete") {
            val messageId = call.parameters["id"]?.let(UUID::fromString)
            if (messageId != null) {
                messageRepo.markAsDeleted(messageId)
                call.respond(mapOf("status" to "success", "message" to "Message marked as deleted!"))
            } else call.respond(mapOf("error" to "Invalid message ID"))
        }
    }
}