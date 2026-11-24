
package sparkX.model;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;

import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class User {

    private String userId;
    private String fullName;
    private String email;

    private String password;

    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    // Existing constructor
    public User(String userId, String fullName, String email) {
        this(userId, fullName, email, null);
    }

    // Constructor with password
    public User(String userId, String fullName, String email,
                String password) {

        this(userId, fullName, email, password, false);
    }

    // Constructor used when restoring an existing account
    protected User(String userId, String fullName, String email,
                   String storedPassword, boolean alreadyHashed) {

        if (userId == null || userId.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "User information cannot be empty."
            );
        }

        if (storedPassword != null && storedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Password cannot be empty."
            );
        }

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;

        if (storedPassword == null) {
            this.password = null;
        } else if (alreadyHashed) {
            this.password = storedPassword;
        } else {
            this.password = hashPassword(storedPassword);
        }
    }

    // Generates a secure password hash
    private String hashPassword(String plainPassword) {

        try {

            byte[] salt = new byte[SALT_LENGTH];

            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(
                plainPassword.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
            );

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            spec.clearPassword();

            return "PBKDF2$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);

        } catch (GeneralSecurityException e) {

            throw new IllegalStateException(
                "Password hashing failed.", e
            );
        }
    }

    // Verifies a login password
    public boolean verifyPassword(String enteredPassword) {

        if (password == null || enteredPassword == null) {
            return false;
        }

        try {

            String[] parts = password.split("\\$");

            if (parts.length != 4
                    || !parts[0].equals("PBKDF2")) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);

            if (iterations < 100000 || iterations > 1000000) {
                return false;
            }

            byte[] salt =
                Base64.getDecoder().decode(parts[2]);

            byte[] originalHash =
                Base64.getDecoder().decode(parts[3]);

            if (salt.length != SALT_LENGTH
                    || originalHash.length != KEY_LENGTH / 8) {
                return false;
            }

            PBEKeySpec spec = new PBEKeySpec(
                enteredPassword.toCharArray(),
                salt,
                iterations,
                KEY_LENGTH
            );

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] enteredHash =
                factory.generateSecret(spec).getEncoded();

            spec.clearPassword();

            return MessageDigest.isEqual(
                originalHash,
                enteredHash
            );

        } catch (GeneralSecurityException | IllegalArgumentException e) {

            return false;
        }
    }

    // Provides the password hash for local storage
    public String getStoredPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public abstract String getRole();
}
