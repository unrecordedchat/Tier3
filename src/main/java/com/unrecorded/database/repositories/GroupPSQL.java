package com.unrecorded.database.repositories;

import com.unrecorded.database.DBA;
import com.unrecorded.database.entities.EGroup;
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
 * This class manages operations related to groups, such as creation, retrieval, updating, and deletion.
 * <p>Uses HibernateORM to save in a PostgreSQL database.</p>
 *
 * @version 1.0
 * @since PREVIEW
 */
public class GroupPSQL implements IGroupRepo {

    /**
     * Logger for recording events and debugging information within the GroupPSQL class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GroupPSQL.class);

    /**
     * Creates a new group in the database.
     *
     * @param name The name of the group. Must not be null.
     * @param ownerId The UUID of the owner user. Must not be null.
     * @throws DataAccessException If there is an issue accessing the database during group creation.
     */
    public void createGroup(@NotNull String name, @NotNull UUID ownerId) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Creating group: name={}, ownerId={}", name, ownerId);
        EGroup group = new EGroup(name, ownerId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(group);
                transaction.commit();
                logger.info("Group created successfully: {}", group);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while creating group.", e);
                throw new DataAccessException(TypeOfDAE.INS, "Error while creating group.", e, false, false);
            }
        }
    }

    /**
     * Retrieves a group from the database by its UUID.
     *
     * @param groupId The UUID of the group.
     * @return The EGroup object corresponding to the specified UUID, or null if no group is found.
     */
    public EGroup getGroup(@NotNull UUID groupId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving group with groupId: {}", groupId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.find(EGroup.class, groupId);
        }
    }

    /**
     * Deletes a group from the database by its UUID.
     *
     * @param groupId The UUID of the group.
     * @throws DataAccessException If there is an issue with the database deletion.
     */
    public void deleteGroup(@NotNull UUID groupId) throws DataAccessException {
        if (logger.isDebugEnabled()) logger.debug("Deleting group with groupId: {}", groupId);
        try (Session session = DBA.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                EGroup group = getGroup(groupId);
                if (group != null) {
                    session.remove(group);
                    transaction.commit();
                    logger.info("Group deleted successfully: {}", group);
                } else logger.warn("Group not found for deletion: groupId={}", groupId);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Exception while deleting group.", e);
                throw new DataAccessException(TypeOfDAE.DEL, "Error while deleting group.", e, false, false);
            }
        }
    }

    /**
     * Retrieves all groups from the database.
     *
     * @return A list of all EGroup objects in the database.
     */
    public @NotNull List<EGroup> getAllGroups() {
        if (logger.isDebugEnabled()) logger.debug("Retrieving all groups");
        try (Session session = DBA.getSessionFactory().openSession()) {
            return session.createQuery("FROM EGroup", EGroup.class).list();
        }
    }
}