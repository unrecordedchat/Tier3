package com.unrecorded.database.entities;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * HibernateORM entity representing a user in the system.
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @see com.unrecorded.database.repositories.UserPSQL UserPSQL
 * @since PREVIEW
 */
@Entity
@Table(name = "Users")
public class EUser {

    /**
     * Represents the unique identifier for a user.
     * <p>This field cannot be updated once set.
     */
    @Id
    @Column(name = "userId", updatable = false, nullable = false)
    @Nullable
    private UUID userId = null;

    /**
     * Represents the username for a user in the application.
     */
    @Column(name = "username", nullable = false, unique = true)
    @NotNull
    private String username;

    /**
     * Stores the hashed representation of a user's password.
     *
     * <p>This ensures that the password information is securely stored without exposing the plain text password,
     * providing a layer of security by storing only a hashed version of the password.
     */
    @Column(name = "passwordHash", nullable = false)
    @NotNull
    private String passHash;

    /**
     * Stores the cryptographic salt used in password hashing to enhance security.
     *
     * <p>Salt is a random byte array that is combined with the password before hashing
     * to ensure that identical passwords hash to different values. This mitigates
     * the risk of dictionary and rainbow table attacks by making precomputed hashes
     * ineffective.
     *
     * <p>The salt is stored as a Large Object (LOB) within the database, which is marked as non-nullable,
     * indicating that a salt must be provided for each user password entry.
     */
    @Lob
    @Column(name = "passwordSalt", nullable = false)
    private byte[] salt;

    /**
     * Represents the email address associated with the user.
     */
    @Column(name = "emailAddress", nullable = false, unique = true)
    @NotNull
    private String email;

    /**
     * Represents the public key associated with the user for cryptographic operations.
     */
    @Column(name = "publicKey", nullable = false, unique = true)
    @NotNull
    private String publicKey;

    /**
     * Represents an encrypted private key associated with the user.
     *
     * <p>The private key is used for cryptographic operations such as encryption, signing or
     * authentication. In this application, it is stored in an encrypted format
     * to ensure confidentiality and security.
     *
     * <p>Note: The encryption and decryption of the private key must be handled
     * appropriately to prevent unauthorized access.
     */
    @Column(name = "privateKeyEncrypted", nullable = false)
    @NotNull
    private String privateKey;

    /**
     * Default constructor required by JPA.
     */
    public EUser() {
    }

    /**
     * Constructs a new EUser instance with the specified details.
     *
     * @param username   The username of the user.
     * @param passHash   The password hash of the user.
     * @param salt       The salt used for password hashing
     * @param email      The email address of the user.
     * @param publicKey  The public key of the user.
     * @param privateKey The encrypted private key of the user.
     */
    public EUser(@NotNull String username, @NotNull String passHash, byte[] salt, @NotNull String email, @NotNull String publicKey, @NotNull String privateKey) {
        this.username = username;
        this.passHash = passHash;
        this.salt = salt;
        this.email = email;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Retrieves the unique identifier (UUID) of the user.
     *
     * @return The UUID representing the user's unique identifier.
     */
    public @Nullable UUID getUserId() {
        return userId;
    }

    /**
     * Retrieves the username associated with the user.
     *
     * @return The username of the user.
     */
    public @NotNull String getUsername() {
        return username;
    }

    /**
     * Sets the username for the user.
     *
     * @param username The new username to be set.
     */
    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    /**
     * Retrieves the password hash of the user.
     *
     * @return The password hash.
     */
    public @NotNull String getPassHash() {
        return passHash;
    }

    /**
     * Sets the password hash for the user.
     *
     * @param passHash The hashed password to be set for the user.
     */
    public void setPassHash(@NotNull String passHash) {
        this.passHash = passHash;
    }

    /**
     * Retrieves the salt used for the user's password hashing process.
     *
     * @return A byte array representing the salt.
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Sets the salt value for the user's password hashing process.
     *
     * @param salt The new salt as a byte array.
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    /**
     * Retrieves the email address associated with the user.
     *
     * @return The email address of the user.
     */
    public @NotNull String getEmail() {
        return email;
    }

    /**
     * Sets the email address for the user.
     *
     * @param email The new email address to be set.
     */
    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    /**
     * Retrieves the public key associated with the user.
     *
     * @return The public key of the user.
     */
    public @NotNull String getPublicKey() {
        return publicKey;
    }

    /**
     * Sets the public key for the user.
     *
     * @param publicKey The new public key to be set.
     */
    public void setPublicKey(@NotNull String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Retrieves the encrypted private key of the user.
     *
     * @return The encrypted private key.
     */
    public @NotNull String getPrivateKey() {
        return privateKey;
    }

    /**
     * Sets the encrypted private key for the user.
     *
     * @param privateKey The new encrypted private key to be set.
     */
    public void setPrivateKey(@NotNull String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Compares this EUser object to the specified object to determine if they are equal.
     *
     * @param o The object to compare with this EUser instance.
     * @return True if the specified object is equal to this EUser, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EUser that = (EUser) o;
        return Objects.equals(userId, that.userId) && username.equals(that.username) && passHash.equals(that.passHash) && email.equals(that.email) && publicKey.equals(that.publicKey) && privateKey.equals(that.privateKey);
    }

    /**
     * Computes the hash code for this object using its significant attributes.
     *
     * @return An integer representing the hash code of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, username, passHash, email, publicKey, privateKey);
    }

    /**
     * Returns a string representation of the User entity.
     *
     * @return A formatted string containing the username and email of the User.
     */
    @Override
    public String toString() {
        return String.format("// HibernateORM Entity 'User':\n Id: %s\n Username: %s\n  Email: %s\n * Rest is kept hidden. //\n", userId, username, email);
    }
}