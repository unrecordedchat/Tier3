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
 * ReactionDTO.kt
 *
 * This file defines the data transfer objects (DTOs) related to reaction management within the Ktor application.
 * These DTOs are used for both incoming requests and outgoing responses in the reaction-related API endpoints.
 *
 * General Purpose:
 * - Standardize the data format for creating/deleting reactions and retrieving reaction details.
 * - Simplify the communication between frontend and backend by providing a clear contract for expected/requested data.
 *
 * Features:
 * - Fully **Serializable**: All DTOs are compatible with Kotlinx Serialization to ensure seamless integration for JSON request/response handling.
 * - **Request Objects**: Define the strict structure for handling API interactions related to reactions.
 * - **Response Objects**: Standardize the format of data returned from the server to clients in reaction-related results.
 *
 * DTOs Provided:
 * 1. `ReactionRequest`: Represents the payload for creating or deleting a reaction.
 * 2. `ReactionResponse`: Represents the structure used for sending reaction information to API clients.
 *
 * Extensibility:
 * - Additional DTOs can be added as required for advanced reaction management functionalities (e.g., bulk reactions, analytics).
 * - Each existing DTO can be extended with optional fields if new requirements arise.
 *
 * Usage Example:
 * These DTOs are part of the reaction management RESTful API and enable well-defined data exchanges between clients and the server.
 * - Request (JSON to DTO conversion): For incoming payloads such as creating or deleting reactions.
 * - Response (DTO to JSON conversion): For returning consistent reaction data to API clients.
 *
 * Author: Sergiu Chirap
 * Year: 2024
 */

/**
 * Represents the payload required for creating or deleting a reaction.
 *
 * This data transfer object (DTO) ensures the required attributes for managing reactions within the system.
 * It contains the necessary identifiers (`userId`, `messageId`) and the reaction's emoji.
 *
 * ### Features:
 * - Fully serializable to JSON, enabling seamless RESTful API integration.
 * - Designed for both creating new reactions and deleting them.
 * - Validation should ensure valid string formats and presence of a valid emoji before processing the request.
 *
 * ### Usage Example:
 * - **Client JSON Request**:
 *   ```json
 *   {
 *     "userId": "123e4567-e89b-12d3-a456-426614174000",
 *     "messageId": "987e6543-e21b-34c3-a789-126644771231",
 *     "emoji": "👍"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val request = ReactionRequest(
 *       userId = "123e4567-e89b-12d3-a456-426614174000",
 *       messageId = "987e6543-e21b-34c3-a789-126644771231",
 *       emoji = "👍"
 *   )
 *   ```
 *
 * @property userId The unique identifier of the user who reacted (as a string format UUID).
 * @property messageId The unique identifier of the message being reacted to (as a string format UUID).
 * @property emoji The emoji associated with the reaction.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class ReactionRequest(
    val userId: String,
    val messageId: String,
    val emoji: String
)

/**
 * Represents the response structure for reaction-related API results.
 *
 * This data transfer object (DTO) defines the structure of reaction data returned from the server.
 * It includes details about the reaction's `userId`, `messageId`, and `emoji`.
 *
 * ### Features:
 * - Fully serializable to JSON for seamless integration with RESTful APIs.
 * - Provides a standardized data format for frontend applications consuming reaction-related responses.
 * - Designed for exporting reaction details without including sensitive data from other entities.
 *
 * ### Usage Example:
 * - **Client JSON Response**:
 *   ```json
 *   {
 *     "userId": "123e4567-e89b-12d3-a456-426614174000",
 *     "messageId": "987e6543-e21b-34c3-a789-126644771231",
 *     "emoji": "👍"
 *   }
 *   ```
 * - **DTO Initialization**:
 *   ```kotlin
 *   val response = ReactionResponse(
 *       userId = "123e4567-e89b-12d3-a456-426614174000",
 *       messageId = "987e6543-e21b-34c3-a789-126644771231",
 *       emoji = "👍"
 *   )
 *   ```
 *
 * @property userId The unique identifier of the user who performed the reaction (as a string).
 * @property messageId The unique identifier of the message receiving the reaction (as a string).
 * @property emoji The emoji associated with the reaction.
 * @author Sergiu Chirap
 * @since 0.4
 * @see kotlinx.serialization.Serializable
 */
@Serializable
data class ReactionResponse(
    val userId: String,
    val messageId: String,
    val emoji: String
)