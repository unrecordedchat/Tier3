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

package com.unrecorded.ktor.dto

import kotlinx.serialization.Serializable

/*
 * ErrorResponse.kt
 *
 * This file defines the `ErrorResponse` data transfer object (DTO) used to represent standardized error messages throughout the application.
 *
 * General Purpose:
 * - To encapsulate error information in a structured format suitable for client responses.
 * - Improves client experience by consistently formatting errors.
 * - Provides additional metadata (e.g., timestamp) to aid in debugging and tracing issues.
 *
 * Features:
 * - **Serializable**: Fully compatible with Kotlinx Serialization, allowing seamless integration with JSON or other serialized formats.
 * - **Customizable Error Fields**: Includes fields for the error message, status code, and timestamp information.
 * - **Timestamped Errors**: Automatically captures the current timestamp when an error is constructed, providing precise information on when the error occurred.
 *
 * Extensibility:
 * - This DTO can be extended or used as a base for more specific error models (e.g., module-specific or feature-specific error responses).
 * - Can be adapted to include additional attributes, such as a detailed description, error resolution steps, or debugging links.
 *
 * Usage Example:
 * - Use `ErrorResponse` to provide structured responses for errors in both application and API contexts.
 * - Example JSON response:
 * ```json
 * {
 *   "error": "Invalid input parameter.",
 *   "code": "400 Bad Request",
 *   "timestamp": 1689349281234
 * }
 * ```
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */


/**
 * Represents a standardized structure for error responses sent to clients.
 *
 * This class encapsulates information about an error encountered by the application, making it easier
 * to provide consistent, meaningful, and structured error messages in API or application responses.
 * It integrates a simple format with extensibility options for further customizations.
 *
 * ### Features:
 * - **Serializable**: The class is annotated with `@Serializable`, enabling proper serialization/deserialization
 *   with Kotlinx Serialization or other supported libraries.
 * - **Error Message Field (`error`)**: A clear and concise human-readable message describing the error.
 * - **Optional Status Code (`code`)**: Allows representation of an HTTP status code as a string (e.g., `"400 Bad Request"`).
 *   The field is nullable, letting developers omit it when not applicable.
 * - **Timestamp (`timestamp`)**: Automatically initializes with the system's current time in milliseconds when the object is created.
 *   Useful for debugging and tracing.
 *
 * ### Extensibility:
 * - Developers can add additional properties or define subclassed DTOs with more specific attributes if required.
 *   For example:
 *   ```kotlin
 *   @Serializable
 *   data class DetailedErrorResponse(
 *       val error: String,
 *       val code: String? = null,
 *       val timestamp: Long = System.currentTimeMillis(),
 *       val details: List<String>? = null // Additional field for detailed information
 *   )
 *   ```
 * - Can be integrated into error-handling middleware for consistent formatting of error responses.
 *
 * ### Usage Example:
 * ```kotlin
 * val errorResponse = ErrorResponse(
 *     error = "Invalid input parameter.",
 *     code = "400 Bad Request"
 * )
 * ```
 * The resulting JSON for the above object:
 * ```json
 * {
 *   "error": "Invalid input parameter.",
 *   "code": "400 Bad Request",
 *   "timestamp": 1689349281234
 * }
 * ```
 *
 * ### Benefits:
 * - Simplifies the process of creating standardized error responses.
 * - Ensures consistency across API responses, improving client application handling.
 * - Provides immediate context (timestamp) for debugging purposes.
 *
 * @author Sergiu Chirap
 * @since 0.4
 * @property error A human-readable description of the error. Mandatory.
 * @property code The HTTP status code or other recognizable error code as a string. Optional.
 * @property timestamp The time when the error occurred, represented as a Unix timestamp in milliseconds. Defaults to the current system time.
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class ErrorResponse(
    val error: String,
    val code: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)