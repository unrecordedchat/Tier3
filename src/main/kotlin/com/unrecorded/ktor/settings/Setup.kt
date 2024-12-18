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

package com.unrecorded.ktor.settings

import com.unrecorded.database.exceptions.DataAccessException
import com.unrecorded.database.repositories.*
import com.unrecorded.ktor.routes.*
import com.unrecorded.ktor.util.JsonConfig
import com.unrecorded.ktor.util.handleException
import com.unrecorded.ktor.util.healthCheck
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.ext.getKoin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/*
 * Setup.kt
 *
 * This file defines the core configuration logic for the Ktor application.
 *
 * General Purpose:
 * - To centralize the setup of essential features required for the application to function, ensuring a modular and maintainable structure.
 * - Integrates routing, dependency injection, content negotiation, and error handling into a cohesive application setup.
 * - Serves as a foundation for initializing and organizing application-level logic.
 * 
 * Features:
 * - **Content Negotiation**: Enables JSON serialization and deserialization for RESTful request and response handling.
 * - **Error Handling**: Configures a global mechanism to handle exceptions, ensuring consistent and user-friendly error responses.
 * - **Dependency Injection**: Utilizes the Koin framework to manage repositories and services efficiently and maintain flexibility.
 * - **Routing**: Registers RESTful API endpoints for various application features, including user management, groups, and messaging.
 * 
 * Setup Summary:
 * 1. `configureKtorModules()` - Orchestrates the initialization of all core functionality.
 * 2. `configureContentNegotiation()` - Sets up JSON serialization using Kotlinx Serialization.
 * 3. `configureStatusPages()` - Implements a global error-handling strategy for meaningful HTTP responses.
 * 4. `configureDI()` - Initializes the Koin dependency injection framework and binds interfaces to their implementations.
 * 5. `configureRouting()` - Declares application routes for features like users, friendships, groups, and notifications.
 * 
 * Extensibility:
 * - Add additional Ktor plugins (e.g., logging, security) to enhance the application setup.
 * - Customize existing configurations (e.g., content serialization or status pages) to tailor to specific project needs.
 * - Introduce new routes and modules systematically by extending the routing and DI mechanisms.
 *
 * Usage:
 * The `configureKtorModules()` function is the entry point for the application setup and should be invoked in the `Application.module()` function:
 *
 * fun Application.module() {
 *     configureKtorModules()
 * }
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Configures core features for the Ktor application.
 *
 * The `configureKtorModules` function serves as an orchestration layer, delegating the initialization of
 * essential features for the application. It acts as the central point for configuring core functionalities
 * required for the application to start and operate as expected.
 *
 * ### Core Features
 * This function invokes a sequence of dedicated setup methods, each responsible for a specific feature:
 * - [configureContentNegotiation]: Enables automatic JSON serialization and deserialization for request and response bodies.
 * - [configureStatusPages]: Configures a global error-handling mechanism to standardize exception responses.
 * - [configureDI]: Integrates dependency injection using the Koin framework.
 * - [configureRouting]: Sets up the application's routing for API endpoints, such as health checks and user-related routes.
 *
 * ### Purpose
 * - Consolidates all core configuration logic into a single point for easy maintainability.
 * - Ensures each feature's configuration remains modular and adheres to separation of concerns.
 * - Simplifies the initialization process within the `module` function by abstracting away core setup details.
 *
 * ### How It Works
 * This function calls the following methods in the specified order:
 * 1. [configureContentNegotiation] – Sets up content handling with JSON serialization.
 * 2. [configureStatusPages] – Installs and configures uniform error handling behavior.
 * 3. [configureDI] – Initializes dependency injection for managing services and repositories.
 * 4. [configureRouting] – Defines all active routes and endpoints for the application.
 *
 * ### Example
 * To use this function, invoke it during the application module setup:
 * ```kotlin
 * fun Application.module() {
 *     configureKtorModules()
 *     // Additional configurations can be added here
 * }
 * ```
 *
 * ### Extensibility
 * While this function handles pre-defined core setups, it is not meant to include additional configurations or custom features.
 * Extend the configuration process by adding custom setup logic to the `module` function instead of modifying this method.
 *
 * For example:
 * ```kotlin
 * fun Application.module() {
 *     configureKtorModules()
 *     configureCustomFeature()
 * }
 *
 * fun configureCustomFeature() {
 *     install(CallLogging) {
 *         level = Level.INFO
 *     }
 * }
 * ```
 *
 * ### Thread Safety
 * All feature configurations invoked within this function adhere to Ktor's coroutine-based concurrency model,
 * ensuring safe initialization even in a multithreaded environment.
 *
 * @author Sergiu Chirap
 * @since 0.4
 * @see configureContentNegotiation
 * @see configureStatusPages
 * @see configureDI
 * @see configureRouting
 */
fun Application.configureKtorModules() {
    configureContentNegotiation()
    configureStatusPages()
    configureDI()
    configureRouting()
}

/**
 * Configures the `ContentNegotiation` plugin for the Ktor application.
 *
 * This function installs and configures the ContentNegotiation plugin to enable support for automatic content serialization.
 * It uses **Kotlinx Serialization** to handle JSON-based request and response bodies, ensuring the application
 * can serialize and deserialize data effectively.
 *
 * ### Features:
 * - **Pretty Print**: Formats JSON responses for easier readability in development and debugging.
 * - **Lenient Parsing**: Allows parsing JSON with minor structural issues without throwing errors.
 * - **Unknown Key Ignoring**: Prevents errors if client-sent JSON contains extra unused keys.
 *
 * ### How It Works:
 * 1. Installs the `ContentNegotiation` Ktor plugin.
 * 2. Applies a custom configuration for **Kotlinx Serialization** via the [JsonConfig] object.
 * 3. Ensures that JSON responses and requests adhere to the application's serialization requirements.
 *
 * ### Example:
 * ```kotlin
 * fun Application.configureContentNegotiation() {
 *     install(ContentNegotiation) {
 *         json(Json { prettyPrint = true })
 *     }
 * }
 * ```
 *
 * ### Extensibility:
 * To support additional content types (e.g., XML), modify or extend this function:
 * ```kotlin
 * fun Application.configureContentNegotiation() {
 *     install(ContentNegotiation) {
 *         json(JsonConfig.json)
 *         xml() // Add support for XML
 *     }
 * }
 * ```
 *
 * @author Sergiu Chirap
 * @since 0.4
 * @see io.ktor.server.plugins.contentnegotiation.ContentNegotiation
 * @see kotlinx.serialization.json.Json
 */
fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json(JsonConfig.json)
    }
}

/**
 * Configures the `StatusPages` plugin for the Ktor application.
 *
 * This function installs and configures `StatusPages`, which intercepts exceptions or HTTP errors during request processing
 * and transforms them into user-friendly error responses. It ensures uniform error handling and logging for the server.
 *
 * ### Features:
 * - Handles common exceptions like `IllegalArgumentException`.
 * - Translates domain-specific exceptions, such as [DataAccessException], into standardized error responses.
 * - Logs exception details and returns meaningful error messages to the client.
 *
 * ### How It Works:
 * 1. Installs the `StatusPages` Ktor plugin.
 * 2. Maps custom exceptions to HTTP status codes (e.g., 400 for `IllegalArgumentException` and 500 for generic exceptions).
 * 3. Logs error details for debugging and operational visibility.
 * 4. Returns structured error responses to the client using the ErrorResponse data class.
 *
 * ### Example:
 * ```kotlin
 * fun Application.configureStatusPages() {
 *     install(StatusPages) {
 *         exception<IllegalArgumentException> { call, cause ->
 *             call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = cause.message ?: "Invalid input"))
 *         }
 *     }
 * }
 * ```
 *
 * ### Extensibility:
 * Add new exception types or customize existing ones by modifying `handleException` calls.
 * Example:
 * ```kotlin
 * handleException<MyCustomException>(HttpStatusCode.Forbidden) {
 *     "Custom error: ${it.details}"
 * }
 * ```
 *
 * @author Sergiu Chirap
 * @since 0.4
 * @see com.unrecorded.database.exceptions.DataAccessException
 * @see io.ktor.server.plugins.statuspages.StatusPages
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        handleException<IllegalArgumentException>(HttpStatusCode.BadRequest)
        handleException<DataAccessException>(HttpStatusCode.InternalServerError) { "Database error: ${it.message}" }
        handleException<Exception>(HttpStatusCode.InternalServerError) { "Unexpected error: ${it.message}" }
    }
}

/**
 * Configures Dependency Injection (DI) for the Ktor application using the `Koin` framework.
 *
 * This function integrates [Koin], a lightweight dependency injection framework, to manage the application's services,
 * repositories, and other dependencies. It ensures proper setup and lifecycle management for DI in the Ktor application.
 *
 * ### Features:
 * - Provides singleton implementations for user, message, group, and notification repositories.
 * - Uses [Koin's SLF4J logger][org.koin.logger.slf4jLogger] for logging dependency resolution.
 * - Serves as the central point for dependency management in the application.
 *
 * ### How It Works:
 * 1. Installs the `Koin` plugin within the Ktor application.
 * 2. Register the **application module** for dependency configuration.
 * 3. Logs all DI-related actions using `slf4jLogger` for operational visibility.
 *
 * ### Example:
 * ```kotlin
 * fun Application.configureDI() {
 *     install(Koin) {
 *         modules(appModule)
 *     }
 * }
 * ```
 *
 * ### Extensibility:
 * To add new dependencies:
 * 1. Define the dependency in the `appModule`.
 * 2. Use the `single`, `factory`, or `scoped` functions to register the dependency.
 *
 * Example:
 * ```kotlin
 * appModule {
 *     single<MyService> { MyServiceImpl() }
 * }
 * ```
 *
 * @author Sergiu Chirap
 * @since 0.4
 * @see org.koin.ktor.plugin.Koin
 * @see org.koin.dsl.module
 */
fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}

/**
 * Defines the Dependency Injection module for the application.
 *
 * The `appModule` contains singleton registrations for repositories in the application. Each repository represents
 * a specific aspect of data management (users, groups, messages, notifications) and is injected where needed.
 *
 * @author Sergiu Chirap
 * @since 0.4
 * @see IUserRepo
 * @see IFriendshipRepo
 * @see IGroupRepo
 * @see IGroupMemberRepo
 * @see IMessageRepo
 * @see IReactionRepo
 * @see ISessionRepo
 * @see INotificationRepo
 */
val appModule = module {
    single<IUserRepo> { UserPSQL() as IUserRepo }
    single<IFriendshipRepo> { FriendshipPSQL() as IFriendshipRepo }
    single<IGroupRepo> { GroupPSQL() as IGroupRepo }
    single<IGroupMemberRepo> { GroupMemberPSQL() as IGroupMemberRepo }
    single<IMessageRepo> { MessagePSQL() as IMessageRepo }
    single<IReactionRepo> { ReactionPSQL() as IReactionRepo }
    single<ISessionRepo> { SessionPSQL() as ISessionRepo }
    single<INotificationRepo> { NotificationPSQL() as INotificationRepo }
}

/**
 * Configures routing logic for the entire Ktor application.
 *
 * This function sets up and integrates all routing modules within the application, providing
 * RESTful API endpoints for various features including health checks, user management,
 * friendships, groups, messages, notifications, and more. It uses dependency injection
 * (DI) through Koin to manage repository dependencies for each routing module.
 *
 * ### Features:
 * - **Health Check Endpoint**:
 *   Enables basic application health monitoring with a `/health` endpoint.
 * - **User Management Routes**:
 *   Manages user-related operations like user creation, retrieval, updates, and deletion.
 * - **Friendship Management Routes**:
 *   Handles friend-related operations such as sending, accepting, and removing friends.
 * - **Group Management Routes**:
 *   Provides endpoints for creating, managing, and retrieving groups.
 * - **Group Member Management Routes**:
 *   Enables operations for adding or removing group members.
 * - **Message Management Routes**:
 *   Facilitates the creation, retrieval, and deletion of messages between users or within groups.
 * - **Reaction Management Routes**:
 *   Supports adding and managing reactions to messages.
 * - **Session Management Routes**:
 *   Manages session-related functionality such as authentication or token validation.
 * - **Notification Management Routes**:
 *   Handles operations for sending, updating, and retrieving notifications.
 *
 * ### Dependencies:
 * - Dependency Injection (DI) is managed via the `getKoin().get()` calls, ensuring seamless integration
 *   of the required repositories, which are defined within the Koin modules.
 *
 * ### Extensibility:
 * - Additional routing modules can be integrated easily by adding them to this function.
 * - Centralized routing configuration enables fine-grained control over API structure and new functionality.
 *
 * ### Example Interaction:
 * Attach this function to your `Application` instance by calling it during application startup, as follows:
 * ```kotlin
 * fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
 *
 * @JvmOverloads
 * fun Application.module(testing: Boolean = false) {
 *     configureRouting()
 * }
 * ```
 *
 * ### Endpoint Details:
 * | **Feature**                | **Endpoint**                      | **Description**                          |
 * |----------------------------|------------------------------------|------------------------------------------|
 * | Health Check               | `/health`                         | Simple health check endpoint.            |
 * | User Management            | `/api/users`                      | Endpoints for user operations.           |
 * | Friendship Management      | `/api/friendships`                | Endpoints for friend-related actions.    |
 * | Group Management           | `/api/groups`                     | Group management endpoints.              |
 * | Group Member Management    | `/api/groups/members`             | Manage group members operations.         |
 * | Message Management         | `/api/messages`                   | Endpoints for message operations.        |
 * | Reaction Management        | `/api/reactions`                  | Endpoints for reactions to messages.     |
 * | Session Management         | `/api/sessions`                   | Session-related endpoints.               |
 * | Notification Management    | `/api/notifications`              | Notification-related endpoints.          |
 *
 * ### Benefits:
 * - Provides a single-entry point for declaring all application routing logic.
 * - Streamlines the injection of required dependencies, ensuring modularity and maintainability.
 * - Promotes clear separation of concerns for each routing module.
 *
 * ### Example Request:
 * - **Health Check**:
 *   ```plaintext
 *   GET /health
 *   Response: 200 OK
 *   ```
 *
 * @receiver Application The Ktor application instance where routing is configured.
 * @see io.ktor.server.routing.routing
 * @see io.ktor.server.response.respond
 * @author Sergiu Chirap
 * @since 0.4
 */
fun Application.configureRouting() {
    routing {
        get("/health") { call.healthCheck() }
        userRoutes(getKoin().get())
        friendshipRoutes(getKoin().get())
        groupRoutes(getKoin().get())
        groupMemberRoutes(getKoin().get())
        messageRoutes(getKoin().get())
        reactionRoutes(getKoin().get())
        sessionRoutes(getKoin().get())
        notificationRoutes(getKoin().get())
    }
}