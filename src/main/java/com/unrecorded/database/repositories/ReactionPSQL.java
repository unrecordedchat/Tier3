package com.unrecorded.database.repositories;

import com.unrecorded.database.DBA;
import com.unrecorded.database.entities.EReaction;
import com.unrecorded.database.entities.EReaction.ReactionId;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.exceptions.TypeOfDAE;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * This class manages operations related to reactions, including creation, retrieval, updating, and deletion.
 * <p>Uses HibernateORM to save in a PostgreSQL database.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see com.unrecorded.database.entities.EReaction EReaction
 * @see com.unrecorded.database.DBA DBA
 * @since PREVIEW
 */
public class ReactionPSQL implements IReactionRepo {

    /**
     * Logger for recording events and debugging information within the ReactionPSQL class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ReactionPSQL.class);

    /**
     * Creates a new reaction in the database.
     *
     * @param userId    The UUID of the user. Must not be null.
     * @param messageId The UUID of the message. Must not be null.
     * @param type      The type of the reaction. Must not be null.
     * @throws DataAccessException If there is an issue accessing the database during reaction creation.
     */
    public void createReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String type) throws DataAccessException {
        if (logger.isDebugEnabled())
            logger.debug("Creating reaction: userId={}, messageId={}, type={}", userId, messageId, type);
        EReaction reaction = new EReaction(userId, messageId, type);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(reaction);
                transaction.commit();
                logger.info("Reaction created successfully: {}", reaction);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while creating reaction.", e);
                throw new DataAccessException(TypeOfDAE.INS, "Error while creating reaction.", e, false, false);
            }
        }
    }

    /**
     * Retrieves a reaction from the database by its composite key.
     *
     * @param userId    The UUID of the user.
     * @param messageId The UUID of the message.
     * @param type      The type of the reaction.
     * @return The EReaction object corresponding to the specified composite key, or null if no reaction is found.
     */
    public EReaction getReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String type) {
        if (logger.isDebugEnabled())
            logger.debug("Retrieving reaction: userId={}, messageId={}, type={}", userId, messageId, type);
        try (Session session = DBA.getSessionFactory().openSession()) {
            ReactionId id = new ReactionId(userId, messageId, type);
            return session.find(EReaction.class, id);
        }
    }

    /**
     * Deletes a reaction from the database by its composite key.
     *
     * @param userId    The UUID of the user.
     * @param messageId The UUID of the message.
     * @param type      The type of the reaction.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    public void deleteReaction(@NotNull UUID userId, @NotNull UUID messageId, @NotNull String type) throws DataAccessException {
        if (logger.isDebugEnabled())
            logger.debug("Deleting reaction: userId={}, messageId={}, type={}", userId, messageId, type);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EReaction reaction = getReaction(userId, messageId, type);
                if (reaction != null) {
                    session.remove(reaction);
                    transaction.commit();
                    logger.info("Reaction deleted successfully: {}", reaction);
                } else logger.warn("Reaction not found: userId={}, messageId={}, type={}", userId, messageId, type);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while deleting reaction.", e);
                throw new DataAccessException(TypeOfDAE.DEL, "Error while deleting reaction.", e, false, false);
            }
        }
    }

    /**
     * Retrieves all reactions for a specific message.
     *
     * @param messageId The UUID of the message.
     * @return A list of EReaction objects corresponding to the specified message.
     */
    public @NotNull List<EReaction> getReactionsForMessage(@NotNull UUID messageId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving reactions for messageId: {}", messageId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.createQuery("FROM EReaction WHERE messageId = :messageId", EReaction.class)
                    .setParameter("messageId", messageId)
                    .list();
        }
    }
}