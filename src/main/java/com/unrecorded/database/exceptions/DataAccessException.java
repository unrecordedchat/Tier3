package com.unrecorded.database.exceptions;

import jakarta.persistence.*;
import jdk.jfr.Description;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.util.UUID;

/**
 * This class represents an exception that occurs when there is a failure
 * connecting to the database. It extends the {@link RuntimeException} class
 * and provides additional context around the time of the exception and the
 * cause of the failure.
 * <p>Every thrown exception will be logged.
 */
@Description("Java")
@Entity
@Table(name = "DAE", schema = "unrecorded")
public class DataAccessException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DataAccessException.class);
    private static final String msg = "Failure connecting to the database:\n";
    private static final String blankMsg = "Reason must not be blank!";
    private static final String typeMsg = "Specified 'Type' does not exist.";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "daeId", unique = true, nullable = false, updatable = false)
    private UUID daeId;

    @Column(name = "errorMessage", nullable = false)
    private String errorMessage;

    @Column(name = "errorTimestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT current_timestamp")
    private ZonedDateTime errorTimestamp;

    @Column(name = "operation")
    private String operation;

    @Column(name = "relatedEntity")
    private String relatedEntity;

    @Column(name = "stackTrace")
    private String stackTrace;

    @Column(name = "additionalInfo")
    private String additionalInfo;

    @Transient
    private static final ZoneId zoneId = ZoneId.of("CET");

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeOfDAE type;

    private final ZonedDateTime throwDateTime = ZonedDateTime.now(zoneId);
    private final Instant throwInstant = throwDateTime.toInstant();

    /**
     * Protected no-arg constructor required by Hibernate.
     */
    protected DataAccessException() {
        super();
        this.type = null;
    }

    public DataAccessException(@Nullable TypeOfDAE type, @NotNull String reason) throws IllegalArgumentException {
        this((Object) type, reason, null, false, true);
    }

    public DataAccessException(@Nullable String type, @NotNull String reason) throws IllegalArgumentException {
        this((Object) type, reason, null, false, true);
    }

    public DataAccessException(@Nullable TypeOfDAE type, @NotNull String reason, @Nullable Throwable cause) throws IllegalArgumentException {
        this((Object) type, reason, cause, false, true);
    }

    public DataAccessException(@Nullable String type, @NotNull String reason, @Nullable Throwable cause) throws IllegalArgumentException {
        this((Object) type, reason, cause, false, true);
    }

    public DataAccessException(@Nullable TypeOfDAE type, @NotNull Throwable cause) {
        this((Object) type, null, cause, false, true);
    }

    public DataAccessException(@Nullable String type, @NotNull Throwable cause) {
        this((Object) type, null, cause, false, true);
    }

    public DataAccessException(@Nullable TypeOfDAE type, @NotNull String reason, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this((Object) type, reason, cause, enableSuppression, writableStackTrace);
    }

    public DataAccessException(@Nullable String type, @NotNull String reason, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this((Object) type, reason, cause, enableSuppression, writableStackTrace);
    }

    private DataAccessException(@Nullable Object type, @Nullable String reason, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) throws IllegalArgumentException {
        super(msg + (reason != null ? reason : ""), cause, enableSuppression, writableStackTrace);
        if (reason != null) checkReason(reason);
        switch (type) {
            case null -> this.type = TypeOfDAE.GNL;
            case TypeOfDAE typeOfDAE -> this.type = typeOfDAE;
            case String s -> this.type = readType(s);
            default -> throw new IllegalArgumentException(typeMsg);
        }
        this.errorMessage = reason;
        this.errorTimestamp = ZonedDateTime.now();
        this.stackTrace = cause != null ? cause.getMessage() : null;
        this.operation = enableSuppression ? "Suppressed" : "Not Suppressed";
        this.relatedEntity = this.getClass().getName();
        this.additionalInfo = writableStackTrace ? "Stack Trace Writable" : "Stack Trace Not Writable";
        logger.error("DataAccessException occurred: {}", reason, cause);
        registerException(this.type, reason, cause, enableSuppression, writableStackTrace);
    }

    public ZonedDateTime getThrowDateTime() {
        return throwDateTime;
    }

    public Instant getThrowInstant() {
        return throwInstant;
    }

    public TypeOfDAE getType() {
        return type;
    }

    @Contract(pure = true)
    private static @NotNull String msg(@Nullable String reason) {
        return msg + (reason != null ? reason : "");
    }

    private @NotNull String getStackTrace(@NotNull Throwable cause) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : cause.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private void registerException(@NotNull TypeOfDAE type, @Nullable String reason, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        //TODO: implement this method
    }

    private void checkReason(@NotNull String reason) throws IllegalArgumentException {
        if (reason.isBlank()) throw new IllegalArgumentException(blankMsg);
    }

    private @NotNull TypeOfDAE readType(@NotNull String type) throws IllegalArgumentException {
        for (TypeOfDAE t : TypeOfDAE.values()) if (t.toString().equalsIgnoreCase(type)) return t;
        throw new IllegalArgumentException(typeMsg);
    }
}