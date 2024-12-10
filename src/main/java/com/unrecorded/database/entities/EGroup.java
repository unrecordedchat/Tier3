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

package com.unrecorded.database.entities;

import com.unrecorded.database.util.MultiTools;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * HibernateORM entity representing a group within the system.
 *
 * <p>This entity models the fundamental aspects of a group, such as group name and ownership,
 * allowing for the organization of users into cohesive units. 
 * A UUID uniquely identifies each group, with one user serving as the group's administrator.</p>
 *
 * <h2>Entity Relationships:</h2>
 * <ul>
 *   <li>Managed by a single administrator, referenced by {@code ownerId}, facilitating group management operations.</li>
 *   <li>Has potential relationships with {@code EUser} and {@code EGroupMembership} entities
 *   to manage users within the group.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @see com.unrecorded.database.repositories.GroupPSQL GroupPSQL
 * @version 1.1
 * @since PREVIEW
 */
@Entity
@Table(name = "groups", schema = "unrecorded")
public class EGroup {

    /**
     * Represents the unique identifier for a group.
     * <p>This value is automatically generated and immutable once set.
     * It is crucial in distinguishing each group within the system.</p>
     */
    @Id
    @Column(name = "group_id", nullable = false, updatable = false)
    @Nullable
    private UUID id;

    /**
     * Represents the name of the group within the system.
     */
    @Column(name = "group_name", nullable = false)
    @NotNull
    private String name;

    /**
     * Represents the unique identifier for the group administrator.
     * <p>This ID links to the user's unique identifier who holds administrative privileges over the group.</p>
     */
    @Column(name = "admin_id", nullable = false)
    @NotNull
    private UUID ownerId;

    /**
     * Default constructor required by JPA.
     */
    public EGroup() {
    }

    /**
     * Constructs a new EGroup instance with a specified name and owner ID.
     *
     * @param name The name of the group.
     * @param ownerId The UUID of the group's administrator.
     */
    public EGroup(@NotNull String name, @NotNull UUID ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }

    /**
     * Retrieves the unique identifier for the group.
     *
     * @return The UUID representing the group's unique identifier. It may be null if not yet persisted.
     */
    public @Nullable UUID getId() {
        return id;
    }

    /**
     * Retrieves the name of the group.
     *
     * @return A string representing the group's name.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the name of the group.
     *
     * @param name The name to assign to the group.
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Retrieves the unique identifier for the group's administrator.
     *
     * @return The UUID representing the unique identifier of the administrator.
     */
    public @NotNull UUID getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the unique identifier for the group's administrator.
     *
     * @param ownerId The UUID representing the administrator's unique identifier.
     */
    public void setOwnerId(@NotNull UUID ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Compares this EGroup instance with another object for equality.
     *
     * @param o The object to be compared for equality with this EGroup.
     * @return True if the specified object is equivalent to this EGroup; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EGroup that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(ownerId, that.ownerId);
    }

    /**
     * Computes a hash code for this EGroup instance based on its fields.
     *
     * @return An integer representing the computed hash code.
     */
    @Override
    public int hashCode() {
        return MultiTools.hash(id, name, ownerId);
    }

    /**
     * Returns a string representation of the EGroup entity, ideal for debugging and logging.
     *
     * @return A string representing this instance in a structured, readable format.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM Entity 'Group':\n ID: %s\n Name: %s\n Owner: %s //", id, name, ownerId);
    }
}