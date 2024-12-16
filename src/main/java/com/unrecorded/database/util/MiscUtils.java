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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * A utility class designed to house complex, generic methods and features
 * that are not associated with a specific class or domain but are reusable
 * across different parts of the application.
 *
 * <p>The {@code MiscUtils} class is intended to provide a centralized repository
 * for various utility methods, tools, and features that do not neatly fit into
 * a particular category, domain, or entity.
 * This includes but is not limited to methods for processing, hashing, and converting data.</p>
 *
 * <h2>Purpose:</h2>
 * <ul>
 *   <li>Provides generic utility methods that operate on arbitrary data or perform general transformations.</li>
 *   <li>Enables code reuse by implementing methods that can be shared across entities or domains.</li>
 *   <li>Holds features commonly needed in multiple, unrelated parts of the application.</li>
 *   <li>Facilitates the addition of future utility functions with minimal coupling to existing domains.</li>
 * </ul>
 *
 * <h2>Current Features:</h2>
 * <ul>
 *   <li><b>Hashing:</b> Implements a hardened hashing mechanism that converts input objects to a strong SHA-256 hash,
 *   reduced to a 32-bit {@code int}, suitable for overriding Java's {@code hashCode()} method.</li>
 *   <li>Functionality is designed to handle null values and support a wide variety of object inputs.</li>
 * </ul>
 *
 * <h2>Future Features:</h2>
 * <p>This class may be expanded to include additional general utility functions
 * for data manipulation, string processing, or entity-related operations that
 * are not domain-specific or require grouping elsewhere.</p>
 *
 * <h2>Design Considerations:</h2>
 * <ul>
 *   <li>This class is stateless, containing only static utility methods.</li>
 *   <li>Methods are designed for convenience, reducing boilerplate and enabling safe reuse.</li>
 *   <li>Utility methods must be implemented with efficiency and security considerations in mind.</li>
 * </ul>
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * // Using the hash method to generate a secure hash for multiple inputs:
 * int hash = MiscUtils.hash("field1", "field2", "field3");
 * System.out.println("Generated Hash: " + hash);
 * }</pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>As a stateless utility class, all methods in {@code MiscUtils} are
 * inherently thread-safe.
 * There are no mutable fields or state-related operations, making it safe for use in concurrent environments.</p>
 *
 * <h2>Known Limitations:</h2>
 * <ul>
 *   <li>While the SHA-256-based integer hash is highly secure and robust,
 *       the reduction to 32 bits increases the likelihood of hash collisions
 *       compared to the full hash (256-bit).
 *       It is unsuitable for cryptographic purposes.</li>
 *   <li>The class holds arbitrary utilities that may span multiple concerns;
 *       developers should avoid introducing cohesion-violating features.</li>
 * </ul>
 *
 * @author Sergiu Chirap
 * @version 1.0
 * @since PREVIEW
 */
public class MiscUtils {

    /**
     * Generates a hardened integer hash using SHA-256, reduced to fit into a 32-bit {@code int}.
     *
     * <p>This method takes one or more objects, converts them to their {@link String} representation,
     * concatenates the results, and applies SHA-256 hashing.
     * The resulting 256-bit output is compressed into a 32-bit integer using bitwise operations,
     * while preserving a high degree of randomness
     * and uniformity.</p>
     *
     * <h2>Behavior:</h2>
     * <ul>
     *   <li>Handles {@code null} values gracefully, converting them to the string literal "null".</li>
     *   <li>Ensures UTF-8 encoding is applied to handle internationalized input correctly.</li>
     *   <li>Reduces the 256-bit SHA-256 hash to a 32-bit {@code int}, making it usable for
     *       overriding the {@link Object#hashCode()} method.</li>
     * </ul>
     *
     * <h2>Example Usage:</h2>
     * <pre>{@code
     * int hash = MiscUtils.hash("username", "email@example.com", "password");
     * System.out.println("Generated Integer Hash: " + hash);
     * }</pre>
     *
     * <h2>Security:</h2>
     * <ul>
     *   <li>Uses SHA-256 to provide a strong cryptographic hash.</li>
     *   <li>The reduction to 32 bits maintains randomness but is not suitable for cryptographic operations.
     *       Only use this for non-cryptographic purposes such as overriding {@code hashCode()}.</li>
     * </ul>
     *
     * @param objects The objects to hash. Supports varargs to hash multiple inputs.
     * @return A 32-bit integer hash derived from the SHA-256 hash of the concatenated input.
     * @throws RuntimeException If the SHA-256 algorithm is unavailable (highly unlikely).
     */
    public static int hash(Object... objects) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder inputBuilder = new StringBuilder();
            Arrays.stream(objects).map(Objects::toString).forEach(inputBuilder::append);
            byte[] hashBytes = digest.digest(inputBuilder.toString().getBytes(StandardCharsets.UTF_8));
            int hash = 0;
            for (int i = 0; i < hashBytes.length; i += 4) {
                int chunk = ((hashBytes[i] & 0xff) << 24) | ((hashBytes[i + 1] & 0xff) << 16) | ((hashBytes[i + 2] & 0xff) << 8) | (hashBytes[i + 3] & 0xff);
                hash ^= chunk;
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}