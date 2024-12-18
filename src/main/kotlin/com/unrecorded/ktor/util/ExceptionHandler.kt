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

import com.unrecorded.ktor.dto.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

/*
 * ExceptionHandler.kt
 *
 * This file provides centralized and extensible exception-handling utilities for the Ktor application.
 *
 * The primary responsibility of this file is to define reusable exception-handling mechanisms that work seamlessly
 * with the `StatusPages` plugin in Ktor. These utilities ensure consistent and maintainable exception handling across
 * the application by enabling uniform error logging and structured error responses for both Ktor-specific and
 * database-related exceptions.
 *
 * General Purpose:
 * - Manage exceptions from Ktor workflows (e.g., invalid requests or errors during endpoint execution).
 * - Handle database-related exceptions (e.g., exceptions from ORM frameworks like Hibernate or custom database errors).
 * - Provide standardized, extensible mechanisms for mapping exceptions to HTTP status codes and response messages.
 *
 * Features:
 * - Centralized Handling: Logs exception details and returns well-defined error responses to clients in JSON format.
 * - Exception-Specific Configuration: Allows custom handling logic, messages, and HTTP status codes for any given exception type.
 * - Extensibility: The approach is fully customizable and allows adding new exception-handling logic as the application evolves.
 * - Debugging Support: Automatically logs exceptions with clear error messages, simplifying debugging in both development and production.
 *
 * Extensibility:
 * - Easily define new handling behavior by modifying or extending the `handleException` function.
 * - Map domain-specific exceptions (e.g., application or module-specific) to meaningful HTTP status codes and error responses.
 * - Extend or adapt the logic for custom data flows, such as additional log details or integration with external monitoring.
 *
 * Usage Example:
 * Integrate the `handleException` function within the `StatusPages` plugin configuration of a Ktor application:
 *
 * install(StatusPages) {
 *     handleException<IllegalArgumentException>(HttpStatusCode.BadRequest) {
 *         "Invalid input: ${it.localizedMessage}"
 *     }
 *
 *     handleException<MyCustomDatabaseException>(HttpStatusCode.InternalServerError) {
 *         "Database error: ${it.message}"
 *     }
 * }
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Handles exceptions for `StatusPages` in a unified and flexible manner.
 *
 * This inline function provides a reusable and configurable way to handle specific exceptions in the `StatusPages` plugin
 * of a Ktor application. It allows you to define custom messages and HTTP status codes for any type of exception, ensuring
 * consistent error handling across the application.
 *
 * ### Features:
 * - **Reified Exception Type**: Lets developers handle specific exception types without explicitly passing the class type.
 * - **Customizable Error Messages**: Optionally provide a custom error message for each exception or use the default one.
 * - **Centralized Logging**: Automatically logs the exception type, the error message, and the stack trace using the application's logger.
 * - **Readable Error Responses**: Sends structured error responses to the client, using an `ErrorResponse` DTO with a clear error message and HTTP status code.
 *
 * ### How It Works:
 * 1. The function is installed within the `StatusPages` plugin configuration.
 * 2. On an exception being thrown during request handling:
 *    - The exception is matched by type (using the `reified` type parameter).
 *    - A custom message is created using the `messageProvider` lambda, or the default error message is used.
 *    - Logs the exception and its message for debugging or monitoring purposes.
 *    - Sends a JSON response containing the error message and HTTP status code back to the client.
 *
 * ### Parameters:
 * - **T**: The type of exception to handle (inferred using `reified`).
 * - **statusCode**: The HTTP status code to return to the client (e.g., `HttpStatusCode.BadRequest`, `HttpStatusCode.InternalServerError`).
 * - **messageProvider** *(optional)*: A lambda that allows custom error message generation based on the exception instance.
 *   By default, use the exception's message or "Unexpected error occurred" if the message is null.
 *
 * ### Example:
 * ```kotlin
 * install(StatusPages) {
 *     handleException<IllegalArgumentException>(HttpStatusCode.BadRequest) { cause ->
 *         "Invalid input: ${cause.localizedMessage}"
 *     }
 *
 *     handleException<MyCustomException>(HttpStatusCode.Forbidden) {
 *         "Access denied: Reason - ${it.details}"
 *     }
 * }
 * ```
 *
 * #### Example Error Response:
 * If an `IllegalArgumentException` occurs, the client might receive a response in the following format:
 * ```json
 * {
 *   "error": "Invalid input: Missing required field.",
 *   "code": "400 Bad Request"
 * }
 * ```
 *
 * ### Benefits:
 * - Encourages modular and reusable exception-handling logic.
 * - Ensures proper logging for debugging and production monitoring.
 * - Returns standardized and client-friendly error messages across the entire application.
 *
 * ### Thread Safety:
 * This function is coroutine-safe and integrates seamlessly into Ktor's non-blocking, asynchronous architecture.
 *
 * @author Sergiu Chirap
 * @since 0.4
 * @param statusCode The HTTP status code to return for this type of exception.
 * @param messageProvider A lambda providing a custom error message. Defaults to the exception's message or "Unexpected error occurred."
 * @see StatusPagesConfig
 * @see StatusPagesConfig.exception
 * @see ErrorResponse
 */
inline fun <reified T : Throwable> StatusPagesConfig.handleException(statusCode: HttpStatusCode, crossinline messageProvider: (T) -> String = { it.message ?: "Unexpected error occurred." }) {
    exception<T> { call, cause ->
        val errorMessage = messageProvider(cause)
        call.application.log.error("${T::class.simpleName}: $errorMessage", cause)
        call.respond(statusCode, ErrorResponse(error = errorMessage, code = statusCode.toString()))
    }
}