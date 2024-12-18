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

package com.unrecorded.ktor

import com.unrecorded.ktor.settings.configureKtorModules
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * Starts the embedded Ktor server using the Netty engine.
 *
 * This function serves as the entry point for the **Unrecorded** Ktor application.
 * It initializes the server, applies the application module configurations, and blocks the main thread
 * until the server is stopped. It acts as the starting point for managing requests and delegating
 * them to the configured modules.
 *
 * ### Purpose:
 * - Launch the Ktor web server.
 * - Initialize all application-specific configurations through the [Application.module] function.
 * - Block the thread (via `start(wait = true)`) to ensure the server runs continuously.
 *
 * ### How It Works:
 * 1. Registers the [Application.module] function to load all the server's configurations (e.g., DI, routing).
 * 2. Use the **Netty** engine to handle incoming HTTP requests and responses.
 * 3. Prevents the program from terminating by waiting until the server stops.
 *
 * ### Features:
 * - Modular configuration design through the `Application.module`.
 * - Highly extensible for adding more configurations or third-party integrations.
 * - Centralized entry point for the web server lifecycle.
 *
 * ### Example:
 * ```kotlin
 * fun main() {
 *     embeddedServer(Netty, module = Application::module).start(wait = true)
 * }
 * ```
 *
 * ### Extensibility:
 * Additional server behaviors, configurations, or modules can be added by extending the [Application.module].
 * Add new feature configurations in the module and start a new server instance of `Netty`.
 *
 * ### Thread Safety:
 * The server lifecycle and request handling are fully managed by Ktor, which uses coroutines
 * to provide a thread-safe environment for concurrent requests.
 *
 * @author Sergiu Chirap
 * @since 0.4
 * @see Application.module
 */
fun main() {
    embeddedServer(Netty, module = Application::module).start(wait = true)
}

/**
 * Configures the main module for the Ktor application.
 *
 * The `module` function acts as the central configuration entry point for the Ktor application. While the core
 * application features such as dependency injection, routing, content negotiation, and status pages are set up
 * in [configureKtorModules], **new configurations** or additional features should be integrated directly into
 * this function.
 *
 * ### Core Features Delegation
 * The `module` function delegates the initialization of essential application configurations to the
 * [configureKtorModules] function. This includes:
 * - **Content Negotiation**: Enables JSON serialization and deserialization.
 * - **Status Pages**: Ensures structured error handling and exception responses.
 * - **Dependency Injection (DI)**: Provides DI for repositories, services, and other dependencies.
 * - **Routing**: Configures core API endpoints such as health checks and user routes.
 *
 * ### Adding New Configurations
 * To add new features or integrations beyond the core features, use this function directly. For example:
 * ```kotlin
 * fun Application.module() {
 *     configureKtorModules() // Initialize core features
 *     configureLogging() // Add a new custom feature
 *     configureCustomMetrics() // Another custom feature
 * }
 *
 * fun configureLogging() {
 *     install(CallLogging) {
 *         level = Level.INFO
 *     }
 * }
 * ```
 *
 * ### Extensibility
 * - New feature configurations should be defined as separate functions (e.g., `configureCustomMetrics`) and invoked here.
 * - This allows the `module` function to remain clean and focused on application-level orchestration.
 *
 * ### Why Use This Function for New Features?
 * The `module` function provides a clear, centralized point for adding new configurations without touching
 * foundational setups handled by [configureKtorModules]. This promotes better maintainability and reduces
 * potential issues with core features.
 *
 * ### Thread Safety
 * All operations conducted within this function, as well as those within [configureKtorModules], are thread-safe,
 * relying on Ktor's coroutine-based architecture.
 *
 * ### Example
 * ```kotlin
 * fun Application.module() {
 *     configureKtorModules() // Initialize core features
 *     configureMyNewFeature() // Integrate a custom feature here
 * }
 * ```
 * 
 * @author Sergiu Chirap
 * @since 0.4
 * @see configureKtorModules
 */
fun Application.module() {
    configureKtorModules()
    println("Application started successfully!")
}