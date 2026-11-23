
package sparkX.model;

public abstract class User {

    private String userId;
    private String fullName;
    private String email;
    private String password;

    // Existing constructor
    public User(String userId, String fullName, String email) {

        this(userId, fullName, email, null);
    }

    // Constructor with password
    public User(String userId, String fullName, String email,
                String password) {

        if (userId == null || userId.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "User information cannot be empty."
            );
        }

        if (password != null && password.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "Password cannot be empty."
            );
        }

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
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

    // Verify a login password
    public boolean verifyPassword(String enteredPassword) {

        if (password == null || enteredPassword == null) {
            return false;
        }

        return password.equals(enteredPassword);
    }

    public abstract String getRole();
}
