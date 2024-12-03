package com.unrecorded.database.repositories;

import com.unrecorded.database.DBA;
import com.unrecorded.database.entities.EMessage;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.exceptions.TypeOfDAE;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * This class manages operations related to messages, including creation, retrieval, updating, and deletion.
 * <p>Uses HibernateORM to save in a PostgreSQL database.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see DBA
 * @since PREVIEW
 */
public class MessagePSQL implements IMessageRepo {

    /**
     * Logger for recording events and debugging information within the MessagePSQL class.
     */
    private static final Logger logger = LoggerFactory.getLogger(MessagePSQL.class);

    /**
     * Creates a new message in the database.
     *
     * @param senderId         The UUID of the message sender. Must not be null.
     * @param recipientId      The UUID of the message recipient. Must not be null if not a group message.
     * @param isGroup          True if the message is to a group, false otherwise.
     * @param contentEncrypted The encrypted content of the message. Must not be null.
     * @throws DataAccessException If there is an issue accessing the database during message creation.
     */
    public void createMessage(@NotNull UUID senderId, @Nullable UUID recipientId, boolean isGroup, @NotNull String contentEncrypted) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Creating message from senderId: {}", senderId);
        EMessage message = new EMessage(senderId, recipientId, isGroup, contentEncrypted);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(message);
                transaction.commit();
                logger.info("Message created successfully from senderId: {}", senderId);
            } catch (ConstraintViolationException e) {
                if (transaction != null) transaction.rollback();
                logger.error("ConstraintViolationException while creating message.", e);
                throw new DataAccessException(TypeOfDAE.INS, "Error while creating message.", e, false, false);
            } catch (PersistenceException e) {
                if (transaction != null) transaction.rollback();
                logger.error("PersistenceException while creating message", e);
                throw new DataAccessException(TypeOfDAE.GNL, "There was an unexpected persistence-related error while creating the message.", e, true, true);
            }
        }
    }

    /**
     * Retrieves all messages exchanged between two users, ensuring they are direct messages.
     *
     * @param senderId    The UUID of the sender.
     * @param recipientId The UUID of the recipient user.
     * @return A list of EMessage objects representing messages exchanged directly between the two users.
     */
    public @NotNull List<EMessage> getAllMessagesBetweenUsers(@NotNull UUID senderId, @NotNull UUID recipientId) {
        if (logger.isDebugEnabled())
            logger.debug("Retrieving all messages between senderId: {} and recipientId: {}", senderId, recipientId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.createQuery("FROM EMessage WHERE isGroup = false AND ((senderId = :senderId AND recipientId = :recipientId) OR (senderId = :recipientId AND recipientId = :senderId))", EMessage.class).setParameter("senderId", senderId).setParameter("recipientId", recipientId).list();
        }
    }

    /**
     * Retrieves all messages for a specified group.
     *
     * @param groupId The UUID of the group from which to retrieve messages.
     * @return A list of EMessage objects representing all messages that belong to the specified group.
     */
    public @NotNull List<EMessage> getAllMessagesForGroup(@NotNull UUID groupId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving all messages for groupId: {}", groupId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.createQuery("FROM EMessage WHERE recipientId = :groupId AND isGroup = true", EMessage.class).setParameter("groupId", groupId).list();
        }
    }

    /**
     * Retrieves a message from the database by its unique identifier.
     *
     * @param messageId The unique identifier of the message to retrieve.
     * @return The EMessage object corresponding to the specified messageId, or null if no message is found.
     */
    public @Nullable EMessage getMessageById(@NotNull UUID messageId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving message by ID: {}", messageId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.find(EMessage.class, messageId);
        }
    }

    /**
     * Updates the encrypted content of the specified message.
     *
     * @param messageId           The unique identifier of the message.
     * @param newContentEncrypted The new encrypted content to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    public void updateMessageContent(@NotNull UUID messageId, @NotNull String newContentEncrypted) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Updating content for messageId: {}", messageId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EMessage message = session.get(EMessage.class, messageId);
                if (message != null) {
                    message.setContentEncrypted(newContentEncrypted);
                    session.merge(message);
                    transaction.commit();
                    logger.info("Message content updated successfully for messageId: {}", messageId);
                } else logger.warn("Message not found for messageId: {}", messageId);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while updating message content for messageId: {}", messageId, e);
                throw new DataAccessException(TypeOfDAE.UPD, "Error while updating message content.", e, false, false);
            }
        }
    }

    /**
     * Deletes a message from the database by its unique identifier.
     *
     * @param messageId The unique identifier of the message to be deleted.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    public void deleteMessage(@NotNull UUID messageId) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Deleting message by ID: {}", messageId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EMessage message = session.get(EMessage.class, messageId);
                if (message != null) {
                    session.remove(message);
                    transaction.commit();
                    logger.info("Message deleted successfully for messageId: {}", messageId);
                } else logger.warn("Message not found for messageId: {}", messageId);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while deleting message for messageId: {}", messageId, e);
                throw new DataAccessException(TypeOfDAE.DEL, "Error while deleting message.", e, false, false);
            }
        }
    }
}