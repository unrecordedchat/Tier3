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

package com.unrecorded.ktor.util

import com.unrecorded.ktor.util.JsonConfig.json
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

/*
 * MiscUtils.kt
 *
 * This file provides miscellaneous utility functions and configurations used throughout the application.
 *
 * General Purpose:
 * - To define reusable components that simplify common operations in the Ktor application.
 * - To consolidate shared configurations and functionalities, improving consistency and reducing duplication.
 *
 * Features:
 * - **Health Check Utility**: Provides a lightweight endpoint helper for health monitoring.
 *   - Simplifies creating a basic health check endpoint returning a "UP" status.
 * - **JSON Configuration**: Offers a centralized configuration for JSON serialization/deserialization, ensuring consistent behavior across the application.
 *   - Includes advanced features like pretty printing, lenient parsing, and unknown key ignoring.
 *
 * Extensibility:
 * - Additional utilities can be added to this file for functionalities that are reused across multiple files or modules.
 * - The `JsonConfig` object can be extended or modified to adjust global serialization behavior for the application.
 *
 * Usage Example:
 * - `healthCheck()`: Use it in a Ktor route to return a "UP" status for health monitoring systems or testing.
 * - `JsonConfig.json`: Configure JSON serialization using the predefined settings across the application.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Handles the health check response for the Ktor application.
 *
 * This utility is designed for health monitoring systems to verify whether the application is running
 * and responding as expected. When invoked, it sends a predefined "UP" status and an HTTP 200 (OK) response.
 *
 * ### Features:
 * - Returns a simple JSON object with a status key: `"status": "UP"`.
 * - Responds with `HttpStatusCode.OK` (HTTP 200) to indicate application health.
 *
 * ### Extensibility:
 * - Can be extended to include additional health metrics, such as database connectivity or service integration status.
 *   Example:
 *   ```kotlin
 *   suspend fun ApplicationCall.healthCheck() {
 *       val response = mapOf(
 *           "status" to "UP",
 *           "database" to checkDatabaseStatus() // Custom function to check the database health.
 *       )
 *       respond(HttpStatusCode.OK, response)
 *   }
 *   ```
 *
 * ### Usage Example:
 * Include in a Ktor route to provide health check monitoring:
 * ```kotlin
 * routing {
 *     get("/health") {
 *         call.healthCheck()
 *     }
 * }
 * ```
 *
 * ### Return Value:
 * Responds to the client with a JSON object indicating the application's health status.
 *
 * @receiver ApplicationCall The Ktor application call context where the response is sent.
 * @author Sergiu Chirap
 * @since 0.4
 * @see ApplicationCall.respond
 * @see HttpStatusCode.OK
 */
suspend fun ApplicationCall.healthCheck() {
    val response = mapOf("status" to "UP")
    respond(HttpStatusCode.OK, response)
}

/**
 * Provides a centralized configuration for JSON serialization and deserialization.
 *
 * This object defines the default settings for Kotlinx Serialization used in the application. It ensures
 * that all JSON-related operations within the application follow a consistent configuration, simplifying
 * debugging and guaranteeing uniform serialization/deserialization behavior.
 *
 * ### Features:
 * - **Pretty Print**: Formats JSON for readability during development or debugging.
 * - **Lenient Parsing**: Allows parsing JSON with minor structural issues, such as mismatched quotes or extra commas.
 * - **Unknown Key Ignoring**: Prevents errors during deserialization when encountering extra keys that aren’t part of the data model.
 *
 * ### Extensibility:
 * - Additional configuration options can be added as required. For example, you could include stricter validations
 *   or custom serializers to handle specific data types.
 * - Extend the `JsonConfig` object when using it as a base for more specialized JSON configurations in specific modules:
 *   ```kotlin
 *   val customJson = Json(JsonConfig.json) {
 *       encodeDefaults = false // Custom configuration
 *   }
 *   ```
 *
 * ### Usage Example:
 * Use this configuration for serialization and deserialization tasks:
 * ```kotlin
 * val json = JsonConfig.json
 * val jsonString = json.encodeToString(dataObject)
 * val parsedObject = json.decodeFromString<MyData>(jsonString)
 * ```
 *
 * ### Benefits:
 * - Having a single source of truth for JSON behavior enhances maintainability.
 * - Ensures predictable and consistent JSON handling across different parts of the application.
 *
 * @property json The [Json] instance configured with application-wide serialization/deserialization settings.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.json.Json
 */
object JsonConfig {
    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
}