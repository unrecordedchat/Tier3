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
import com.unrecorded.database.util.FieldValidator;
import com.unrecorded.database.util.HibernateUtil;
import com.unrecorded.database.util.LoggerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * This class manages operations related to messages, including creation, retrieval, updating, and deletion.
 *
 * <p><b>Purpose:</b> The `MessagePSQL` class handles message-centric CRUD operations
 * (Create, Read, Update, Delete) for a PostgreSQL database.
 * It integrates Hibernate ORM for database access and transaction management,
 * ensuring input validation and secure data handling across operations.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Field validation to ensure compliance with business rules and database constraints.</li>
 *   <li>Efficient retrieval and manipulation of message data using Hibernate queries.</li>
 *   <li>Logging of operations and diagnostics for traceability and debugging.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>This class is stateless and uses thread-safe utilities like {@link HibernateUtil},
 * making it safe for use in multithreaded environments.</p>
 *
 * <h3>Note:</h3>
 * <p>All database-related exceptions are encapsulated in {@link DataAccessException}
 * to ensure consistent error reporting.</p>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see EMessage
 * @see HibernateUtil
 * @see LoggerUtil
 * @since PREVIEW
 */
public class MessagePSQL implements IMessageRepo {

    /**
     * Creates and saves a new message in the database.
     *
     * <p>This method validates input fields and ensures group-specific and direct message-specific rules are followed.
     * It persists the message entity using Hibernate ORM and logs relevant information about the process.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Validate the {@code senderId}, {@code recipientId}, and other constraints via {@link FieldValidator}.</li>
     *   <li>Check if the message type (group or direct) meets business rules.</li>
     *   <li>Create a new {@link EMessage} object representing the message.</li>
     *   <li>Save the message object with Hibernate ORM via {@link HibernateUtil}.</li>
     * </ol>
     *
     * @param senderId         The UUID of the sender. Must not be {@code null}.
     * @param recipientId      The UUID of the message recipient. Can be {@code null} for group messages.
     * @param groupId          The UUID of the group to which the message belongs. Can be {@code null} for private messages.
     * @param isGroup          A boolean indicating if this is a group message.
     * @param contentEncrypted The encrypted content of the message.
     * @throws IllegalArgumentException If validation fails for input fields or message constraints.
     * @throws DataAccessException      If an error occurs while persisting the message.
     */
    @Override
    public void createMessage(
            @NotNull UUID senderId,
            @Nullable UUID recipientId,
            @Nullable UUID groupId,
            boolean isGroup,
            @NotNull String contentEncrypted
    ) throws IllegalArgumentException, DataAccessException {
        FieldValidator.userLinkConstraints(senderId, recipientId);
        LoggerUtil.logInfo("Creating a new message from senderId: {}", senderId.toString());
        if (!isGroup && recipientId == null) {
            throw new IllegalArgumentException("Direct messages must have a recipient.");
        }
        if (isGroup && groupId == null) {
            throw new IllegalArgumentException("Group messages must include a group ID.");
        }
        HibernateUtil.executeTransaction(true, session -> {
            EMessage message = new EMessage(senderId, recipientId, groupId, isGroup, contentEncrypted);
            assert message.getId() != null;
            LoggerUtil.logDebug("Prepared message entity for persistence: {}", message.getId().toString());
            session.persist(message);
            LoggerUtil.logInfo("Message created successfully with senderId: {}", senderId.toString());
            return null;
        });
    }

    /**
     * Retrieves a message entity from the database using its unique identifier (UUID).
     *
     * <p>This method queries the database for an {@link EMessage} entity using the provided {@code messageId}.
     * If no message is found, it returns {@code null}.
     * The retrieval is performed within a read-only transaction for improved performance and consistency.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Log the retrieval attempt with the provided {@code messageId}.</li>
     *   <li>Start a read-only transaction for the query.</li>
     *   <li>Use {@link HibernateUtil} to retrieve the entity with the specified {@code messageId}.</li>
     *   <li>If a message is found, log success; otherwise, log a warning.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * UUID messageId = UUID.randomUUID();
     * EMessage message = messageRepo.getMessageById(messageId);
     * }</pre>
     *
     * @param messageId The UUID of the message to retrieve.
     * @return The {@link EMessage} entity corresponding to the given ID, or {@code null} if not found.
     * @throws DataAccessException If an error occurs while performing the query.
     */
    @Override
    public @Nullable EMessage getMessageById(@NotNull UUID messageId) throws DataAccessException {
        LoggerUtil.logInfo("Getting message by ID: {}", messageId.toString());
        return HibernateUtil.executeTransaction(false, session -> {
            EMessage message = session.get(EMessage.class, messageId);
            if (message != null) {
                LoggerUtil.logDebug("Message retrieved successfully. ID: {}", messageId.toString());
            } else {
                LoggerUtil.logWarn("Message not found. ID: " + messageId);
            }
            return message;
        });
    }
    
    /**
     * Retrieves all direct messages exchanged between two users.
     *
     * <p>This method queries the database for {@link EMessage} entities representing all
     * direct messages (non-group messages) sent between the sender and recipient,
     * regardless of direction (sender-to-recipient or recipient-to-sender).</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Validate the user IDs using {@link FieldValidator}.</li>
     *   <li>Log the retrieval attempt with the user IDs.</li>
     *   <li>Start a read-only transaction and execute a query to fetch matching messages.</li>
     *   <li>Return all matching messages as a list.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * UUID senderId = UUID.randomUUID();
     * UUID recipientId = UUID.randomUUID();
     * List<EMessage> messages = messageRepo.getAllMessagesBetweenUsers(senderId, recipientId);
     * }</pre>
     *
     * @param senderId   The UUID of one of the users involved in the message exchange. Must not be null.
     * @param recipientId The UUID of the other user involved in the message exchange. Must not be null.
     * @return A list of {@link EMessage} entities representing all direct messages exchanged between the users,
     * or {@code null} if none exist.
     * @throws IllegalArgumentException If the user IDs fail validation.
     * @throws DataAccessException      If there is an issue while querying the database.
     */
    @Override
    public @Nullable List<EMessage> getAllMessagesBetweenUsers(@NotNull UUID senderId, @NotNull UUID recipientId
    ) throws IllegalArgumentException, DataAccessException {
        FieldValidator.userLinkConstraints(senderId, recipientId);
        LoggerUtil.logInfo("Retrieving all direct messages between sender: {} and recipient: {}", senderId.toString(), recipientId.toString());
        return HibernateUtil.executeTransaction(false, session -> session.createQuery("FROM EMessage WHERE isGroup = false " + "AND ((senderId = :senderId AND recipientId = :recipientId) " + "OR (recipientId = :senderId AND senderId = :recipientId))", EMessage.class).setParameter("senderId", senderId).setParameter("recipientId", recipientId).list());
    }

    /**
     * Retrieves all messages associated with a specific group, identified by its unique group ID.
     *
     * <p>This method fetches a list of {@link EMessage} entities from the database where the
     * group ID matches the provided parameter, and the message is marked as a group message
     * ({@code isGroup = true}).
     * The retrieval is performed in a read-only transaction to ensure efficient and consistent data access.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Log the operation to retrieve messages for the specified group ID.</li>
     *   <li>Begin a read-only transaction using {@link HibernateUtil}.</li>
     *   <li>Execute an HQL query to get all matching {@link EMessage} records where {@code groupId} equals the provided ID
     *   and {@code isGroup} is {@code true}.</li>
     *   <li>Return the list of matching messages or an empty list if none are found.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * UUID groupId = UUID.randomUUID();
     * List<EMessage> groupMessages = messageRepo.getAllMessagesForGroup(groupId);
     * }</pre>
     *
     * @param groupId The unique identifier of the group for which messages are retrieved.
     * @return A list of {@link EMessage} entities belonging to the specified group or an empty list if no messages are found.
     * @throws DataAccessException If there is a failure when querying the database.
     */
    @Override
    public @Nullable List<EMessage> getAllMessagesForGroup(@NotNull UUID groupId) throws DataAccessException {
        LoggerUtil.logInfo("Retrieving all messages for group ID: {}", groupId.toString());
        return HibernateUtil.executeTransaction(false, session -> session.createQuery("FROM EMessage WHERE groupId = :groupId AND isGroup = true", EMessage.class).setParameter("groupId", groupId).list());
    }

    /**
     * Updates the content of a specific message in the database.
     *
     * <p>This method modifies the encrypted content of the {@link EMessage} entity with the
     * specified {@code messageId}.
     * If the message corresponding to the ID does not exist in the database, an exception will be thrown.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Log the update attempt for the specific message ID.</li>
     *   <li>Start a writing transaction using {@link HibernateUtil}.</li>
     *   <li>Retrieve the {@link EMessage} by its unique ID.</li>
     *   <li>If the message does not exist, log a warning and throw an {@link IllegalArgumentException}.</li>
     *   <li>Update the {@code contentEncrypted} property of the retrieved message with the new value.</li>
     *   <li>Save the changes to the database using {@code session.merge()}.</li>
     *   <li>Log success of the operation.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * UUID messageId = UUID.randomUUID();
     * String newContent = "NewEncryptedContent";
     * messageRepo.updateMessageContent(messageId, newContent);
     * }</pre>
     *
     * @param messageId          The unique identifier of the message to update.
     * @param newContentEncrypted The new encrypted content for the message.
     * @throws IllegalArgumentException If the message does not exist in the database.
     * @throws DataAccessException      If an error occurs during the update operation.
     */
    @Override
    public void updateMessageContent(@NotNull UUID messageId, @NotNull String newContentEncrypted) throws DataAccessException {
        LoggerUtil.logInfo("Updating message content. Message ID: {}", messageId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EMessage message = session.get(EMessage.class, messageId);
            if (message == null) {
                LoggerUtil.logWarn("Message not found. Message ID: " + messageId);
                throw new IllegalArgumentException("Message does not exist.");
            }
            message.setContentEncrypted(newContentEncrypted);
            session.merge(message);
            LoggerUtil.logInfo("Message content updated successfully. Message ID: {}", messageId.toString());
            return null;
        });
    }

    /**
     * Deletes a message permanently from the database.
     *
     * <p>This method removes the {@link EMessage} entity with the specified {@code messageId} from the database.
     * If the message does not exist, a warning is logged, and an {@link IllegalArgumentException} is thrown.
     * The deletion is carried out within a writing transaction, ensuring data consistency.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Log the attempt to delete the message with the specified ID.</li>
     *   <li>Start a writing transaction using {@link HibernateUtil}.</li>
     *   <li>Retrieve the {@link EMessage} by its unique ID.</li>
     *   <li>If the message does not exist:
     *     <ul>
     *       <li>Log a warning about the missing entity.</li>
     *       <li>Throw an {@link IllegalArgumentException} to indicate the failure.</li>
     *     </ul>
     *   </li>
     *   <li>Use {@code session.remove()} to permanently delete the entity from the database.</li>
     *   <li>Log the successful completion of the operation.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * UUID messageId = UUID.randomUUID();
     * try {
     *     messageRepo.deleteMessage(messageId);
     * } catch (IllegalArgumentException e) {
     *     System.err.println("Message not found: " + e.getMessage());
     * }
     * }</pre>
     *
     * @param messageId The unique identifier of the message to delete.
     * @throws IllegalArgumentException If the message does not exist in the database.
     * @throws DataAccessException      If an error occurs during the deletion operation.
     */
    @Override
    public void deleteMessage(@NotNull UUID messageId) throws DataAccessException {
        LoggerUtil.logInfo("Deleting message by ID: {}", messageId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EMessage message = session.get(EMessage.class, messageId);
            if (message == null) {
                LoggerUtil.logWarn("Message not found for deletion. Message ID: " + messageId);
                throw new IllegalArgumentException("Message does not exist.");
            }
            session.remove(message);
            LoggerUtil.logInfo("Message deleted successfully. ID: {}", messageId.toString());
            return null;
        });
    }

    /**
     * Marks a message as deleted without removing it from the database.
     *
     * <p>This method sets a "deleted" flag on the {@link EMessage} entity to logically
     * delete the message while retaining it for audit or recovery purposes.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Log the deletion attempt with the provided {@code messageId}.</li>
     *   <li>Start a writing transaction to modify the message state.</li>
     *   <li>Retrieve the message entity by its {@code messageId}.</li>
     *   <li>Set the entity's "deleted" flag and merge the state back into persistence.</li>
     *   <li>Log success or warnings based on the operation outcome.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * UUID messageId = UUID.randomUUID();
     * messageRepo.markAsDeleted(messageId);
     * }</pre>
     *
     * @param messageId The unique identifier of the message to mark as deleted.
     * @throws DataAccessException If there is an error during the update operation.
     */
    @Override
    public void markAsDeleted(@NotNull UUID messageId) throws DataAccessException {
        LoggerUtil.logInfo("Marking message ID: {} as deleted.", messageId.toString());
        HibernateUtil.executeTransaction(true, session -> {
            EMessage message = session.get(EMessage.class, messageId);
            if (message != null) {
                message.setDeleted();
                session.merge(message);
                LoggerUtil.logInfo("Message successfully marked as deleted. ID: {}", messageId.toString());
            } else LoggerUtil.logWarn("Message not found for deletion. ID: " + messageId);
            return null;
        });
    }
}