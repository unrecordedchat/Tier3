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

package com.unrecorded.database.exceptions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Represents categorized types of Data Access Exceptions (DAE) encountered during
 * database operations.
 *
 * <p><b>Purpose:</b> The `TypeOfDAE` enum centralizes error categorization for database-related
 * exceptions, ensuring consistent identification and handling of issues specific to CRUD
 * (Create, Read, Update, Delete) operations across the application.</p>
 *
 * <h2>Enum Categories:</h2>
 * <ul>
 *   <li>{@link #GNL}: General data access exception for unspecified failures.</li>
 *   <li>{@link #INS}: Data access exception specific to insert operations.</li>
 *   <li>{@link #UPD}: Data access exception specific to update operations.</li>
 *   <li>{@link #DEL}: Data access exception specific to delete operations.</li>
 *   <li>{@link #FND}: Data access exception specific to find/retrieve operations.</li>
 * </ul>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Provides string-based full name and short name representations of each error type.</li>
 *   <li>Generates detailed descriptions for the type of error based on context.</li>
 *   <li>Ensures consistency by associating exceptions with predefined categories.</li>
 * </ul>
 *
 * <p>This class is especially useful when debugging or handling exceptions within
 * the {@link DataAccessException} class, as it enables easy mapping of failure types to
 * their associated database operations.</p>
 *
 * <h2>Thread Safety:</h2>
 * <p>Since enums in Java are inherently thread-safe, this class is safe for concurrent
 * use across multiple threads.</p>
 *
 * <p><b>Note:</b> This enum should be closely tied to error-handling mechanisms,
 * particularly in classes like {@link DataAccessException} and repository implementations
 * (e.g., {@link com.unrecorded.database.repositories.UserPSQL}).</p>
 *
 * @author Sergiu
 * @version 2.0
 * @see DataAccessException
 * @since PREVIEW
 */
public enum TypeOfDAE implements Serializable {

    /**
     * General data access exception type when no specific type is specified,
     * indicating an unspecified or miscellaneous database failure.
     */
    GNL,

    /**
     * Data access exception type indicating a failure during the insertion
     * of data into the database.
     */
    INS,

    /**
     * Data access exception type indicating a failure during the update
     * of existing database records.
     */
    UPD,

    /**
     * Data access exception type indicating a failure during the deletion
     * of records from the database.
     */
    DEL,

    /**
     * Data access exception type indicating a failure during the retrieval
     * (find) of specific data from the database.
     */
    FND;

    /**
     * Provides the user-friendly, descriptive name for the current type of
     * Data Access Exception (DAE).
     *
     * <p>This method maps internal enum values to their human-readable equivalents,
     * offering clarity when logging or displaying error messages to developers or administrators.</p>
     *
     * <h3>Mapped Values:</h3>
     * <ul>
     *     <li>{@link #GNL} → "General"</li>
     *     <li>{@link #INS} → "Insertion"</li>
     *     <li>{@link #UPD} → "Update"</li>
     *     <li>{@link #DEL} → "Deletion"</li>
     *     <li>{@link #FND} → "Finding"</li>
     * </ul>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     * TypeOfDAE errorType = TypeOfDAE.INS;
     * System.out.println("Error type description: " + errorType.getFullName());
     * // Output: Error type description: Insertion
     * }</pre>
     *
     * @return A string representing the full, human-readable name of the current enum instance.
     */
    @Contract(pure = true)
    public @NotNull String getFullName() {
        return switch (this) {
            case GNL -> "General";
            case INS -> "Insertion";
            case UPD -> "Update";
            case DEL -> "Deletion";
            case FND -> "Finding";
        };
    }

    /**
     * Returns the short (abbreviated) name representation of the current enum type.
     *
     * <p>The short name typically corresponds to the string representation of the
     * enum value, such as "INS" for {@link #INS}, "UPD" for {@link #UPD}, etc.</p>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     * TypeOfDAE errorType = TypeOfDAE.DEL;
     * System.out.println("Short name: " + errorType.getShortName());
     * // Output: Short name: DEL
     * }</pre>
     *
     * @return A string representing the short name of the current enum instance.
     */
    @Contract(pure = true)
    public @NotNull String getShortName() {
        return this.toString();
    }

    /**
     * Provides a detailed description of the current type of Data Access Exception (DAE),
     * including potential causes or the contextual meaning of the error.
     *
     * <p>The descriptions are designed for debugging purposes and error reporting, providing
     * verbose contextual details for logging and user feedback mechanisms.</p>
     *
     * <h3>Descriptions Map:</h3>
     * <ul>
     *   <li>{@link #GNL}: "General Exception: No data has been specified."</li>
     *   <li>{@link #INS}: "Insertion Exception: An error occurred while inserting data."</li>
     *   <li>{@link #UPD}: "Update Exception: An error occurred while updating data."</li>
     *   <li>{@link #DEL}: "Deletion Exception: An error occurred while deleting data."</li>
     *   <li>{@link #FND}: "Finding Exception: An error occurred while finding data."</li>
     * </ul>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     * TypeOfDAE errorType = TypeOfDAE.UPD;
     * System.out.println("Error description: " + errorType.getDescription());
     * // Output: Error description: Update Exception: An error occurred while updating data.
     * }</pre>
     *
     * @return A verbose string describing the specific type of Data Access Exception.
     */
    @Contract(pure = true)
    public @NotNull String getDescription() {
        return switch (this) {
            case GNL -> String.format("%s Exception: No data has been specified.", getShortName());
            case INS -> String.format("%s Exception: An error occurred while inserting data.", getShortName());
            case UPD -> String.format("%s Exception: An error occurred while updating data.", getShortName());
            case DEL -> String.format("%s Exception: An error occurred while deleting data.", getShortName());
            case FND -> String.format("%s Exception: An error occurred while finding data.", getShortName());
        };
    }
}