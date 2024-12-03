package com.unrecorded.database.exceptions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Represents different types of Data Access Exceptions (DAE).
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since PREVIEW
 */
public enum TypeOfDAE implements Serializable {
    /**
     * General data access exception type when no specific type is specified.
     */
    GNL,
    /**
     * Represents a data access exception type for insertion operations.
     */
    INS,
    /**
     * Represents a data access exception type for update operations.
     */
    UPD,
    /**
     * Represents a data access exception type for delete operations.
     */
    DEL,
    /**
     * Represents a data access exception type for find operations.
     */
    FND;

    /**
     * Returns the full name representation of the current enum instance.
     *
     * @return A string representing the full name of the enum instance.
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
     * Returns the short name representation of the current enum instance.
     *
     * @return A string representing the short name of the enum instance.
     */
    @Contract(pure = true)
    public @NotNull String getShortName() {
        return this.toString();
    }

    /**
     * Provides a detailed description for the current type of Data Access Error (DAE).
     *
     * @return A string describing the specific data access error for the enum instance.
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