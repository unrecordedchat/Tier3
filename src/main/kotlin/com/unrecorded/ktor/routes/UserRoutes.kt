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

import com.unrecorded.database.repositories.IUserRepo
import com.unrecorded.ktor.dto.CreateUserRequest
import com.unrecorded.ktor.dto.UpdateEmailRequest
import com.unrecorded.ktor.dto.UpdateUsernameRequest
import com.unrecorded.ktor.dto.UserResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/*
 * UserRoutes.kt
 *
 * This file defines the routing logic for user-related operations in the Ktor application.
 *
 * General Purpose:
 * - To provide RESTful API endpoints for managing users within the system.
 * - Integrates with the IUserRepo interface to interact with the database, ensuring a clean separation between routing logic and data access.
 * 
 * Features:
 * - **Create User**: Allows creation of new users with required attributes.
 * - **Retrieve User**: Fetches user details by ID or username.
 * - **Update User Information**: Handles updates to username or email address.
 * - **Delete User**: Implements user removal functionality.
 * - **Password Verification**: Supports password validation for authentication logic.
 * 
 * Endpoint Summary: 
 * 1. `POST /api/users` - Create a new user.
 * 2. `GET /api/users/{id}` - Retrieve a user by ID.
 * 3. `GET /api/users/username/{username}` - Retrieve a user by username.
 * 4. `PATCH /api/users/{id}/username` - Update a user's username.
 * 5. `PATCH /api/users/{id}/email` - Update a user's email address.
 * 6. `DELETE /api/users/{id}` - Delete a user.
 * 7. `POST /api/users/verifyPassword` - Verify user password validity.
 *
 * Extensibility:
 * - Add new user-related operations, such as account recovery, user permissions management, or profile handling.
 * - Integrate response customization for client-specific formats or add additional security layers (e.g., JWT).
 * 
 * Usage:
 * The `userRoutes` function is attached to the `Route` object in Ktor, allowing seamless integration
 * with the application's routing module.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Defines the routing logic for all user-related operations in the Ktor application.
 *
 * This function sets up RESTful API endpoints for managing users, including creating, retrieving, updating,
 * and deleting user records. It also provides a password verification endpoint to support authentication mechanisms.
 * The routing logic interacts with the `IUserRepo` interface to perform database operations.
 *
 * ### Features:
 * - **User Creation (POST /api/users)**:
 *   Creates a new user with required fields such as username, password, email, and encryption keys.
 * - **User Retrieval by ID (GET /api/users/{id})**:
 *   Fetches a user's details using their unique identifier.
 * - **User Retrieval by Username (GET /api/users/username/{username})**:
 *   Fetches a user's details using their username.
 * - **Username Update (PATCH /api/users/{id}/username)**:
 *   Updates a user's username.
 * - **Email Update (PATCH /api/users/{id}/email)**:
 *   Updates a user's email address.
 * - **User Deletion (DELETE /api/users/{id})**:
 *   Deletes a user from the system using their ID.
 * - **Password Verification (POST /api/users/verifyPassword)**:
 *   Checks whether a username-password pair is valid.
 *
 * ### Dependencies:
 * - The function relies on `IUserRepo` to abstract all database operations, ensuring a clean separation
 *   of concerns between routing and data access.
 *
 * ### Extensibility:
 * - Additional endpoints can be added to enhance user management capabilities (e.g., user role management).
 * - Security layers (e.g., OAuth/JWT token validation) can be directly integrated into routes.
 *
 * ### Usage Example:
 * Attach the `userRoutes` function to a `Routing` object as follows:
 * ```kotlin
 * routing {
 *     userRoutes(userRepo)
 * }
 * ```
 * Replace `userRepo` with an implementation of the `IUserRepo` interface.
 *
 * ### Endpoint Details:
 * | HTTP Method | Endpoint                          | Description                    |
 * |-------------|-----------------------------------|--------------------------------|
 * | POST        | /api/users                        | Create a new user.            |
 * | GET         | /api/users/{id}                   | Retrieve a user by ID.        |
 * | GET         | /api/users/username/{username}    | Retrieve a user by username.  |
 * | PATCH       | /api/users/{id}/username          | Update a user's username.     |
 * | PATCH       | /api/users/{id}/email             | Update a user's email.        |
 * | DELETE      | /api/users/{id}                   | Delete a user by ID.          |
 * | POST        | /api/users/verifyPassword         | Validate user credentials.    |
 *
 * ### Example Interaction:
 * - **Create User Request** (`POST /api/users`):
 *   Request Body:
 *   ```json
 *   {
 *       "username": "john_doe",
 *       "password": "securepassword",
 *       "email": "john.doe@example.com",
 *       "publicKey": "public_key_here",
 *       "privateKeyEncrypted": "encrypted_private_key_here"
 *   }
 *   ```
 *   Response:
 *   ```json
 *   {
 *       "status": "success",
 *       "message": "User created successfully!"
 *   }
 *   ```
 *
 * ### Benefits:
 * - Provides a consistent interface for user-related API operations.
 * - Simplifies database interaction and promotes clean, modular code design.
 *
 * @param userRepo An implementation of the `IUserRepo` interface, used to access the user database.
 * @receiver Route The Ktor route to which the user-related endpoints are registered.
 * @author Sergiu Chirap
 * @since 0.4
 * @see IUserRepo
 */
fun Route.userRoutes(userRepo: IUserRepo) {
    route("/api/users") {
        
        // 1. Create a new user (POST /api/users).
        post {
            val request = call.receive<CreateUserRequest>()
            userRepo.createUser(request.username, request.password, request.email, request.publicKey, request.privateKeyEncrypted)
            call.respond(mapOf("status" to "success", "message" to "User created successfully!"))
        }

        // 2. Retrieve a user by their ID (GET /api/users/{id}).
        get("/{id}") {
            val userId = call.parameters["id"]?.let(UUID::fromString)
            if (userId != null) {
                val user = userRepo.getUserById(userId)
                if (user != null) {
                    call.respond(
                        UserResponse(
                            id = user.id.toString(),
                            username = user.username,
                            emailAddress = user.email,
                            publicKey = user.publicKey
                        )
                    )
                } else {
                    call.respond(mapOf("error" to "User not found"))
                }
            } else {
                call.respond(mapOf("error" to "Invalid ID"))
            }
        }

        // 3. Retrieve a user by their username (GET /api/users/username/{username}).
        get("/username/{username}") {
            val username = call.parameters["username"]
            if (username != null) {
                val user = userRepo.getUserByUsername(username)
                if (user != null) {
                    call.respond(
                        UserResponse(
                            id = user.id.toString(),
                            username = user.username,
                            emailAddress = user.email,
                            publicKey = user.publicKey
                        )
                    )
                } else {
                    call.respond(mapOf("error" to "User not found"))
                }
            } else call.respond(mapOf("error" to "Invalid username"))
        }

        // 4. Update a username (PATCH /api/users/{id}/username).
        patch("/{id}/username") {
            val userId = call.parameters["id"]?.let(UUID::fromString)
            val request = call.receive<UpdateUsernameRequest>()
            if (userId != null) {
                userRepo.updateUsername(userId, request.username)
                call.respond(mapOf("status" to "success", "message" to "Username updated successfully!"))
            } else call.respond(mapOf("error" to "Invalid ID"))
        }

        // 5. Update an email address (PATCH /api/users/{id}/email).
        patch("/{id}/email") {
            val userId = call.parameters["id"]?.let(UUID::fromString)
            val request = call.receive<UpdateEmailRequest>()
            if (userId != null) {
                userRepo.updateEmail(userId, request.email)
                call.respond(mapOf("status" to "success", "message" to "Email address updated successfully!"))
            } else call.respond(mapOf("error" to "Invalid ID"))
        }

        // 6. Delete a user by their ID (DELETE /api/users/{id}).
        delete("/{id}") {
            val userId = call.parameters["id"]?.let(UUID::fromString)
            if (userId != null) {
                userRepo.deleteUser(userId)
                call.respond(mapOf("status" to "success", "message" to "User deleted successfully!"))
            } else call.respond(mapOf("error" to "Invalid ID"))
        }

        // 7. Verify a password (POST /api/users/verifyPassword).
        post("/verifyPassword") {
            val request = call.receive<Map<String, String>>()
            val username = request["username"] ?: return@post call.respond(mapOf("error" to "Missing username"))
            val password = request["password"] ?: return@post call.respond(mapOf("error" to "Missing password"))
            val isValid = userRepo.verifyPassword(username, password)
            call.respond(mapOf("status" to "success", "valid" to isValid))
        }
    }
}