package com.unrecorded.database.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    /**
     * Generates a random salt value using a secure random number generator.
     *
     * @return A newly generated 32-byte salt as a byte array.
     */
    public static byte @NotNull [] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return salt;
    }

    @Contract("_, _ -> new")
    public static @NotNull String hashPassword(@NotNull String password, byte @NotNull [] salt) {
        // Use a robust hashing algorithm like PBKDF2, bcrypt, or Argon2
        // This is just a placeholder
        return new String(Base64.getEncoder().encode((password + new String(salt)).getBytes()));
    }
}