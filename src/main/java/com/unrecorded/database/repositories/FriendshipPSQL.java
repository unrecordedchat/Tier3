package com.unrecorded.database.repositories;

import com.unrecorded.database.DBA;
import com.unrecorded.database.entities.EFriendship;
import com.unrecorded.database.exceptions.DataAccessException;
import com.unrecorded.database.exceptions.TypeOfDAE;
import jdk.jfr.Description;
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
 * This class manages operations related to friendships, including creation, retrieval, updating, and deletion.
 * <p>Uses HibernateORM to save in a PostgreSQL database.</p>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see DBA
 * @since PREVIEW
 */
public class FriendshipPSQL implements IFriendshipRepo {

    /**
     * Logger for recording events and debugging information within the FriendshipPSQL class.
     */
    private static final Logger logger = LoggerFactory.getLogger(FriendshipPSQL.class);

    /**
     * Creates a new friendship in the database.
     *
     * @param userId1 The UUID of the first user.
     * @param userId2 The UUID of the second user.
     * @param status  The status of the friendship.
     * @throws DataAccessException If there is an issue accessing the database during friendship creation.
     */
    public void createFriendship(@NotNull UUID userId1, @NotNull UUID userId2, @NotNull String status) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Creating friendship between: {} and {}", userId1, userId2);
        EFriendship friendship = new EFriendship(userId1, userId2, status);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(friendship);
                transaction.commit();
                logger.info("Friendship created successfully between: {} and {}", userId1, userId2);
            } catch (ConstraintViolationException e) {
                if (transaction != null) transaction.rollback();
                logger.error("ConstraintViolationException while creating friendship.", e);
                throw new DataAccessException(TypeOfDAE.INS, "Error while creating friendship.", e, false, false);
            }
        }
    }

    /**
     * Retrieves a friendship from the database by its composite key.
     *
     * @param userId1 The UUID of the first user.
     * @param userId2 The UUID of the second user.
     * @return The EFriendship object corresponding to the specified composite key, or null if no friendship is found.
     */
    public @Nullable EFriendship getFriendship(@NotNull UUID userId1, @NotNull UUID userId2) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving friendship between: {} and {}", userId1, userId2);
        try (Session session = DBA.getSessionFactory().openSession()) {
            EFriendship.FriendshipId id = new EFriendship.FriendshipId(userId1, userId2);
            return session.find(EFriendship.class, id);
        }
    }

    /**
     * Updates the status of a specified friendship.
     *
     * @param userId1   The UUID of the first user.
     * @param userId2   The UUID of the second user.
     * @param newStatus The new status to be set.
     * @throws DataAccessException If there is an issue with the database update.
     */
    public void updateFriendshipStatus(@NotNull UUID userId1, @NotNull UUID userId2, @NotNull String newStatus) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Updating friendship status between: {} and {}", userId1, userId2);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EFriendship friendship = getFriendship(userId1, userId2);
                if (friendship != null) {
                    friendship.setStatus(newStatus);
                    session.merge(friendship);
                    transaction.commit();
                    logger.info("Friendship status updated successfully between: {} and {}", userId1, userId2);
                } else logger.warn("Friendship not found between: {} and {}", userId1, userId2);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while updating friendship status between: {} and {}", userId1, userId2, e);
                throw new DataAccessException(TypeOfDAE.UPD, "Error while updating friendship status.", e, false, false);
            }
        }
    }

    /**
     * Deletes a friendship from the database by its composite key.
     *
     * @param userId1 The UUID of the first user.
     * @param userId2 The UUID of the second user.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    public void deleteFriendship(@NotNull UUID userId1, @NotNull UUID userId2) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Deleting friendship between: {} and {}", userId1, userId2);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EFriendship friendship = getFriendship(userId1, userId2);
                if (friendship != null) {
                    session.remove(friendship);
                    transaction.commit();
                    logger.info("Friendship deleted successfully between: {} and {}", userId1, userId2);
                } else logger.warn("Friendship not found between: {} and {}", userId1, userId2);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while deleting friendship between: {} and {}", userId1, userId2, e);
                throw new DataAccessException(TypeOfDAE.DEL, "Error while deleting friendship.", e, false, false);
            }
        }
    }

    /**
     * Retrieves all friendships for a specific user.
     *
     * @param userId The UUID of the user whose friendships are to be retrieved.
     * @return A list of EFriendship objects corresponding to the specified userId, or an empty list if no friendships are found.
     */
    public @NotNull List<EFriendship> getFriendshipsForUser(@NotNull UUID userId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving friendships for userId: {}", userId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.createQuery("FROM EFriendship WHERE userId1 = :userId OR userId2 = :userId", EFriendship.class).setParameter("userId", userId).list();
        }
    }
    
    /**
     * Retrieves all friendships in the database.
     *
     * @return A list of all EFriendship objects stored in the database.
     * @throws DataAccessException If there is an issue accessing the database during retrieval.
     */
    @Description("Administration")
    public @NotNull List<EFriendship> getAllFriendships() throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Retrieving all friendships from the database.");
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.createQuery("FROM EFriendship", EFriendship.class).list();
        } catch (Exception e) {
            logger.error("Exception while retrieving all friendships.", e);
            throw new DataAccessException(TypeOfDAE.GNL, "Error while retrieving all friendships.", e, false, false);
        }
    }
}