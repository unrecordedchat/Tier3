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

package com.unrecorded.database.repositories;

import com.unrecorded.database.entities.EMessage;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * This interface defines the contract for database operations related to the {@link EMessage} entity.
 * It provides CRUD operations (Create, Read, Update, Delete) for messages, as well
 * as utility methods for managing both direct and group messages.
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Supports creation of a group and direct messages with appropriate validation.</li>
 *   <li>Retrieves messages by unique identifiers, sender-recipient pairs, or groups.</li>
 *   <li>Allows message content updates and provides message deletion functionality.</li>
 * </ul>
 *
 * <h2>Note:</h2>
 * <p>All operations are designed for secure and efficient database interaction, with
 * errors encapsulated in {@link DataAccessException}.</p>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see EMessage
 * @since PREVIEW
 */
public interface IMessageRepo {

    /**
     * Creates a new message in the database.
     *
     * <p>This method handles both creating direct messages and group messages.
     * Direct messages require a recipient, while group messages require a group ID.</p>
     *
     * @param senderId         The UUID of the sender. Must not be {@code null}.
     * @param recipientId      The UUID of the recipient (required for direct messages). Can be {@code null} for group messages.
     * @param groupId          The UUID of the group (required for group messages). Can be {@code null} for direct messages.
     * @param isGroup          {@code true} if the message is for a group; {@code false} otherwise.
     * @param contentEncrypted The encrypted content of the message. Must not be {@code null}.
     * @throws IllegalArgumentException If the provided input fields fail validation or violate business rules.
     * @throws DataAccessException      If an error occurs during database interaction.
     */
    void createMessage(@NotNull UUID senderId, @Nullable UUID recipientId, @Nullable UUID groupId, boolean isGroup, @NotNull String contentEncrypted) throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves a message from the database using its unique identifier.
     *
     * <p>This method queries the database for a specific message by its {@code messageId}.
     * If no message with the provided UUID exists, the method returns {@code null}.</p>
     *
     * @param messageId The unique identifier of the message to retrieve. Must not be {@code null}.
     * @return The message entity corresponding to the specified identifier, or {@code null} if no such message exists.
     * @throws DataAccessException If an error occurs during the query operation.
     */
    @Nullable EMessage getMessageById(@NotNull UUID messageId) throws DataAccessException;

    /**
     * Retrieves all direct messages exchanged between two users.
     *
     * <p>Messages are retrieved based on the sender-recipient pair, including both directions
     * (sender-to-recipient or recipient-to-sender).
     * Only non-group ({@code isGroup = false}) messages are included in the results.</p>
     *
     * @param senderId    The UUID of the sender in the message exchange. Must not be {@code null}.
     * @param recipientId The UUID of the recipient in the message exchange. Must not be {@code null}.
     * @return A list of {@link EMessage} objects representing all direct messages exchanged between the users,
     *         or an empty list if no messages exist.
     * @throws IllegalArgumentException If the provided user IDs are invalid.
     * @throws DataAccessException      If an error occurs during the query operation.
     */
    @Nullable List<EMessage> getAllMessagesBetweenUsers(@NotNull UUID senderId, @NotNull UUID recipientId) throws IllegalArgumentException, DataAccessException;

    /**
     * Retrieves all messages associated with a specific group.
     *
     * <p>The messages are filtered by the provided {@code groupId} and are limited to group messages
     * ({@code isGroup = true}).</p>
     *
     * @param groupId The unique identifier of the group for which messages are retrieved. Must not be {@code null}.
     * @return A list of {@link EMessage} objects representing all messages in the specified group.
     *         Returns an empty list if no messages exist.
     * @throws DataAccessException If an error occurs during the query operation.
     */
    @Nullable List<EMessage> getAllMessagesForGroup(@NotNull UUID groupId) throws DataAccessException;

    /**
     * Updates the content of a specific message in the database.
     *
     * <p>This method modifies the encrypted content of the {@link EMessage} identified by its
     * {@code messageId}.
     * If the message does not exist, an exception is thrown.</p>
     *
     * @param messageId           The unique identifier of the message to be updated. Must not be {@code null}.
     * @param newContentEncrypted The updated encrypted content of the message. Must not be {@code null}.
     * @throws IllegalArgumentException If the {@code messageId} is invalid or the message does not exist.
     * @throws DataAccessException      If an error occurs during the update operation.
     */
    void updateMessageContent(@NotNull UUID messageId, @NotNull String newContentEncrypted) throws IllegalArgumentException, DataAccessException;

    /**
     * Deletes a message permanently from the database.
     *
     * <p>This method removes the {@link EMessage} identified by its {@code messageId}.
     * If the message does not exist, an exception is thrown.</p>
     *
     * @param messageId The unique identifier of the message to delete. Must not be {@code null}.
     * @throws IllegalArgumentException If the {@code messageId} is invalid or the message does not exist.
     * @throws DataAccessException      If an error occurs during the deletion operation.
     */
    void deleteMessage(@NotNull UUID messageId) throws IllegalArgumentException, DataAccessException;

    /**
     * Marks a message as deleted without physically removing it from the database.
     *
     * <p>This method updates the `deleted` flag of an {@link EMessage} to logically mark it as deleted.
     * The message remains in the database for audit or recovery purposes.</p>
     *
     * @param messageId The unique identifier of the message to be marked as deleted. Must not be {@code null}.
     * @throws IllegalArgumentException If the {@code messageId} is invalid or the message does not exist.
     * @throws DataAccessException      If an error occurs during the update operation.
     */
    void markAsDeleted(@NotNull UUID messageId) throws IllegalArgumentException, DataAccessException;
}