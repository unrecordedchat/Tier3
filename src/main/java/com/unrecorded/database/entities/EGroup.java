package com.unrecorded.database.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * HibernateORM entity representing a group in the system.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see com.unrecorded.database.repositories.IGroupRepo IGroupRepo
 * @since PREVIEW
 */
@Entity
@Table(name = "Groups", schema = "unrecorded")
public class EGroup {

    /**
     * Represents the unique identifier for a user. This identifier is automatically
     * generated using a UUID strategy, ensuring global uniqueness.
     * <p>This field cannot be updated once set.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "groupId", nullable = false, updatable = false)
    @NotNull
    private UUID groupId;

    /**
     * Represents the name of the group within the system.
     */
    @Column(name = "groupName", nullable = false)
    @NotNull
    private String name;

    /**
     * Represents the unique identifier for the administrator of the group.
     */
    @Column(name = "adminId", nullable = false)
    @NotNull
    private UUID ownerId;

    /**
     * Default constructor required by JPA.
     */
    public EGroup() {
    }

    /**
     * Constructs a new EGroup with the specified name and owner ID.
     *
     * @param name The name of the group, which cannot be null
     * @param ownerId The unique identifier for the administrator of the group, which cannot be null
     */
    public EGroup(@NotNull String name, @NotNull UUID ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }

    /**
     * Retrieves the unique identifier for the group.
     *
     * @return The UUID representing the unique identifier of the group
     */
    public @NotNull UUID getGroupId() {
        return groupId;
    }

    /**
     * Retrieves the name of the group.
     *
     * @return The name of the group.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the name of the group.
     *
     * @param name The name to be assigned to the group.
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Retrieves the unique identifier for the administrator of the group.
     *
     * @return The UUID representing the unique identifier of the group's administrator.
     */
    public @NotNull UUID getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the unique identifier for the administrator of the group.
     *
     * @param ownerId The unique identifier for the administrator of the group.
     */
    public void setOwnerId(@NotNull UUID ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Compares this EGroup instance with another object to determine equality.
     *
     * @param o The object to be compared for equality with this EGroup.
     * @return True if the specified object is equal to this EGroup; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EGroup that = (EGroup) o;
        return groupId.equals(that.groupId) && name.equals(that.name) && ownerId.equals(that.ownerId);
    }

    /**
     * Computes a hash code for this EGroup instance based on its groupId, name, and ownerId properties.
     *
     * @return An integer representing the computed hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(groupId, name, ownerId);
    }

    /**
     * Returns a string representation of the EGroup object.
     *
     * @return A string representing the instance in a structured format.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM Entity 'Group':\n Id: %s\n Name: %s\n Owner: %s //\n", groupId, name, ownerId);
    }
}