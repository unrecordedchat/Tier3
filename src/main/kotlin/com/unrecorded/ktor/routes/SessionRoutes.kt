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

import com.unrecorded.database.repositories.ISessionRepo
import com.unrecorded.ktor.dto.CreateSessionRequest
import com.unrecorded.ktor.dto.SessionResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.ZonedDateTime
import java.util.*

/*
 * SessionRoutes.kt
 *
 * This file manages routing logic for session-related operations in a Ktor-based backend application.
 *
 * General Purpose:
 * - To handle RESTful API endpoints related to user sessions, including session creation, retrieval, and deletion.
 * - Interacts with the `ISessionRepo` interface to perform database operations while maintaining a separation of concerns.
 *
 * Features:
 * - **Session Management**:
 *   - Create, retrieve, and delete user sessions.
 *   - Support for managing expired sessions to maintain system health and performance.
 *
 * Endpoint Summary:
 * 1. `POST /api/sessions` - Creates a new session.
 * 2. `GET /api/sessions/{id}` - Retrieves a session by its ID.
 * 3. `GET /api/sessions/user/{userId}` - Retrieves all sessions for a specific user.
 * 4. `GET /api/sessions/token/{token}` - Retrieves a session by its token value.
 * 5. `DELETE /api/sessions/{id}` - Deletes a session by its ID.
 * 6. `DELETE /api/sessions/expired` - Deletes all expired sessions.
 *
 * Extensibility:
 * - New operations can be added, such as listing all active sessions or implementing session time extensions.
 * - Integration with security measures (e.g., token-based authentication) can be applied to enhance the session lifecycle logic.
 *
 * Usage:
 * The `sessionRoutes` function is attached to a `Route` object for seamless integration with the routing module in a Ktor application.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Sets up routing for all session-related operations in the Ktor application.
 *
 * The `sessionRoutes` function provides RESTful API endpoints for managing user sessions, such as creating,
 * retrieving, and deleting sessions. It also defines routes specifically for managing expired sessions.
 * This ensures clean routing logic and adherence to proper REST principles.
 *
 * ### Features:
 * - **Session Creation (POST /api/sessions)**:
 *   Creates a new session associated with a user, along with a unique token and expiration date.
 * - **Session Retrieval by ID (GET /api/sessions/{id})**:
 *   Fetches a session's details using its unique identifier.
 * - **Session Retrieval by User ID (GET /api/sessions/user/{userId})**:
 *   Retrieves all active sessions associated with a specific user.
 * - **Session Retrieval by Token (GET /api/sessions/token/{token})**:
 *   Fetches session details using the token assigned to the session.
 * - **Session Deletion by ID (DELETE /api/sessions/{id})**:
 *   Deletes a specific session using its unique identifier.
 * - **Expired Sessions Deletion (DELETE /api/sessions/expired)**:
 *   Deletes all expired sessions to maintain efficient system performance.
 *
 * ### Dependencies:
 * - The function interacts with the `ISessionRepo` interface for all session-related database operations,
 *   ensuring separation of routing logic from database access.
 *
 * ### Extensibility:
 * - Easily extendable for additional session-related operations, such as session renewal or forced session expiration.
 * - Security enhancements like token-based authentication or IP-based session limitations can be added.
 *
 * ### Usage Example:
 * Integrate `sessionRoutes` into the application's routing module:
 * ```kotlin
 * routing {
 *     sessionRoutes(sessionRepo)
 * }
 * ```
 * Replace `sessionRepo` with an instance of `ISessionRepo` (e.g., containing session-related database logic).
 *
 * ### Endpoint Overview:
 * | HTTP Method | Endpoint                          | Description                     |
 * |-------------|-----------------------------------|---------------------------------|
 * | POST        | /api/sessions                     | Create a new session.           |
 * | GET         | /api/sessions/{id}                | Retrieve a session by ID.       |
 * | GET         | /api/sessions/user/{userId}       | Retrieve all sessions for user. |
 * | GET         | /api/sessions/token/{token}       | Retrieve a session by token.    |
 * | DELETE      | /api/sessions/{id}                | Delete a session by ID.         |
 * | DELETE      | /api/sessions/expired             | Delete all expired sessions.    |
 *
 * @param sessionRepo Dependency injected repository interface for session operations.
 * @receiver Route The Ktor routing object where session endpoints are registered.
 * @author Sergiu Chirap
 * @since 0.4
 */
fun Route.sessionRoutes(sessionRepo: ISessionRepo) {
    route("/api/sessions") {

        // 1. Create a new session (POST /api/sessions).
        post {
            val request = call.receive<CreateSessionRequest>()
            val userId = UUID.fromString(request.userId)
            val expiresAt = ZonedDateTime.parse(request.expiresAt)
            sessionRepo.createSession(userId, request.token, expiresAt)
            call.respond(mapOf("status" to "success", "message" to "Session created successfully!"))
        }

        // 2. Retrieve a session by its ID (GET /api/sessions/{id}).
        get("/{id}") {
            val id = call.parameters["id"]?.let(UUID::fromString)
            if (id != null) {
                val session = sessionRepo.getSessionById(id)
                if (session != null) call.respond(
                    SessionResponse(
                        id = session.id.toString(),
                        userId = session.userId.toString(),
                        token = session.token,
                        expiresAt = session.expiresAt.toString()
                    )
                )
                else call.respond(mapOf("error" to "Session not found"))
            } else call.respond(mapOf("error" to "Invalid session ID"))
        }

        // 3. Retrieve all sessions for a user (GET /api/sessions/user/{userId}).
        get("/user/{userId}") {
            val userId = call.parameters["userId"]?.let(UUID::fromString)
            if (userId != null) {
                val sessions = sessionRepo.getSessionsByUserId(userId)
                if (sessions != null) {
                    call.respond(sessions.map {
                        SessionResponse(
                            id = it.id?.toString(),
                            userId = it.userId.toString(),
                            token = it.token,
                            expiresAt = it.expiresAt.toString()
                        )
                    })
                } else call.respond(emptyList<SessionResponse>())
            } else call.respond(mapOf("error" to "Invalid user ID"))
        }

        // 4. Retrieve a session by its token (GET /api/sessions/token/{token}).
        get("/token/{token}") {
            val token = call.parameters["token"]
            if (!token.isNullOrEmpty()) {
                val session = sessionRepo.getSessionByToken(token)
                if (session != null) call.respond(
                    SessionResponse(
                        id = session.id?.toString(),
                        userId = session.userId.toString(),
                        token = session.token,
                        expiresAt = session.expiresAt.toString()
                    )
                )
                else call.respond(mapOf("error" to "Session not found"))
            } else call.respond(mapOf("error" to "Token must be provided"))
        }

        // 5. Delete a session by its ID (DELETE /api/sessions/{id}).
        delete("/{id}") {
            val sessionId = call.parameters["id"]?.let(UUID::fromString) ?: throw IllegalArgumentException("Invalid session ID format.")
            sessionRepo.deleteSession(sessionId)
            call.respond(mapOf("status" to "success", "message" to "Session deleted successfully!"))
        }

        // 6. Delete all expired sessions (DELETE /api/sessions/expired).
        delete("/expired") {
            val currentTime = ZonedDateTime.now()
            val deleted = sessionRepo.deleteExpiredSessions(currentTime)
            call.respond(mapOf("status" to "success", "message" to if (deleted) "Expired sessions deleted." else "No expired sessions to delete."))
        }
    }
}