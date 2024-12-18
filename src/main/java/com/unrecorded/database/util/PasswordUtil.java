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

package com.unrecorded.database.util;

import de.mkammerer.argon2.Argon2Factory;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;


/**
 * The `PasswordUtil` class provides cryptographic utilities for password management.
 *
 * <p><b>Purpose:</b> This class handles secure generation of cryptographic salts, hashing of passwords
 * using the Argon2 algorithm, and verification of password hashes.
 * It ensures that stored passwords are cryptographically secure and resistant to common attacks.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Generates cryptographically secure salts for password hashing.</li>
 *   <li>Hashes passwords using the Argon2id algorithm with custom salting.</li>
 *   <li>Verifies hashed passwords against their plaintext counterparts.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>This class is stateless and thread-safe, as it does not maintain any internal state
 * between method calls.
 * All operations use local variables and thread-safe components like
 * {@link SecureRandom}.</p>
 *
 * @author Sergiu Chirap
 * @version 2.0
 * @see <a href="https://github.com/P-H-C/phc-winner-argon2">Argon2 Documentation</a>
 * @since 0.1
 */
public class PasswordUtil {

    /**
     * Generates a random salt value using a secure random number generator.
     *
     * <p>This method creates a 32-byte cryptographic salt value, suitable for use in password
     * hashing algorithms like Argon2.
     * The salt ensures that identical passwords will not produce identical hashes.</p>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * byte[] salt = PasswordUtil.generateSalt();
     * System.out.println("Generated Salt: " + Arrays.toString(salt));
     * }</pre>
     *
     * @return A newly generated 32-byte salt as a byte array.
     */
    public static byte @NotNull [] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a given password using the Argon2id algorithm.
     *
     * <p>This method combines the given password with a salt, then hashes the result using
     * the Argon2id algorithm.
     * Argon2id is the recommended variant of Argon2 for general-purpose password hashing, 
     * offering a balance between resistance to GPU-based attacks and side-channel protection.</p>
     *
     * <h3>Steps:</h3>
     * <ol>
     *   <li>Concatenate the given password with the provided salt.</li>
     *   <li>Hash the resulting value using 3 iterations, 64 MB of memory, and 4 threads.</li>
     *   <li>Return the resulting hash in an encoded Argon2id string format.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * String password = "mySecurePassword";
     * byte[] salt = PasswordUtil.generateSalt();
     * String hashedPassword = PasswordUtil.hashPassword(password, salt);
     * System.out.println("Hashed password: " + hashedPassword);
     * }</pre>
     *
     * @param password The plaintext password to hash. Must not be {@code null} or empty.
     * @param salt     A unique cryptographic salt to use in hashing. Must not be {@code null} or empty.
     * @return An encoded Argon2id hash string of the given password and salt.
     * @throws IllegalArgumentException If the password or salt is {@code null} or empty.
     */
    public static String hashPassword(String password, byte[] salt) throws IllegalArgumentException {
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("Password cannot be null or empty.");
        if (salt == null || salt.length == 0) throw new IllegalArgumentException("Salt cannot be null or empty.");
        String passwordWithSalt = password + new String(salt, StandardCharsets.UTF_8);
        int iterations = 3; 
        int memory = 65536;
        int parallelism = 4;
        return Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id).hash(iterations, memory, parallelism, passwordWithSalt.toCharArray());
    }

    /**
     * Verifies if a given plaintext password matches its corresponding hashed password.
     *
     * <p>This method reconstructs the hash using the provided plaintext password and salt,
     * then matches it with the stored hashed password to validate its correctness.</p>
     *
     * <h3>How it works:</h3>
     * <ol>
     *   <li>Combine the plaintext password with the provided salt.</li>
     *   <li>Verify the resulting hash against the stored hashed password.</li>
     * </ol>
     *
     * <h3>Example:</h3>
     * <pre>{@code
     * boolean isVerified = PasswordUtil.verifyPassword(storedHash, "mySecurePassword", storedSalt);
     * if (isVerified) {
     *     System.out.println("Password is valid!");
     * } else {
     *     System.out.println("Invalid password.");
     * }
     * }</pre>
     *
     * @param hashedPassword The previously hashed password to match. Must not be {@code null}.
     * @param rawPassword    The plaintext password provided for verification. Must not be {@code null} or empty.
     * @param salt           The salt originally used during the hashing process. Must not be {@code null} or empty.
     * @return {@code true} if the provided password is valid and matches the hashed password; {@code false} otherwise.
     * @throws IllegalArgumentException If any provided argument is invalid (e.g., {@code null} or empty).
     */
    public static boolean verifyPassword(String hashedPassword, String rawPassword, byte[] salt) {
        if (hashedPassword == null || rawPassword == null || salt == null) throw new IllegalArgumentException("Hashed password, raw password, and salt cannot be null.");
        String passwordWithSalt = rawPassword + new String(salt, StandardCharsets.UTF_8);
        return Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id).verify(hashedPassword, passwordWithSalt.toCharArray());
    }
}