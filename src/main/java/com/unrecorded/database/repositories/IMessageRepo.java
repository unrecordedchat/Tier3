package com.unrecorded.database.repositories;

import com.unrecorded.database.entities.EMessage;
import com.unrecorded.database.exceptions.DataAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining database operations for the Message entity.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since PREVIEW
 */
public interface IMessageRepo {

    /**
     * Creates a new message in the database.
     *
     * @param senderId         The UUID of the message sender. Must not be null.
     * @param recipientId      The UUID of the message recipient. Must not be null if not a group message.
     * @param isGroup          True if the message is to a group, false otherwise.
     * @param contentEncrypted The encrypted content of the message. Must not be null.
     * @throws DataAccessException If there is an issue accessing the database during message creation.
     */
    void createMessage(@NotNull UUID senderId, @Nullable UUID recipientId, boolean isGroup, @NotNull String contentEncrypted) throws DataAccessException;

    /**
     * Retrieves a message from the database by its unique identifier.
     *
     * @param messageId The unique identifier of the message to retrieve.
     * @return The EMessage object corresponding to the specified messageId, or null if no message is found.
     */
    @Nullable EMessage getMessageById(@NotNull UUID messageId);

    /**
     * Retrieves all messages exchanged between two users, ensuring they are direct messages.
     *
     * @param senderId    The UUID of the sender.
     * @param recipientId The UUID of the recipient user.
     * @return A list of EMessage objects representing messages exchanged directly between the two users.
     */
    @NotNull List<EMessage> getAllMessagesBetweenUsers(@NotNull UUID senderId, @NotNull UUID recipientId);

    /**
     * Retrieves all messages for a specified group.
     *
     * @param groupId The UUID of the group from which to retrieve messages.
     * @return A list of EMessage objects representing all messages that belong to the specified group.
     */
    @NotNull List<EMessage> getAllMessagesForGroup(@NotNull UUID groupId);
    
    /**
     * Updates the encrypted content of the specified message.
     *
     * @param messageId           The unique identifier of the message.
     * @param newContentEncrypted The new encrypted content to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    void updateMessageContent(@NotNull UUID messageId, @NotNull String newContentEncrypted) throws DataAccessException;

    /**
     * Deletes a message from the database by its unique identifier.
     *
     * @param messageId The unique identifier of the message to be deleted.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    void deleteMessage(@NotNull UUID messageId) throws DataAccessException;
}