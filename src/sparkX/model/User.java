
package sparkX.model;

public abstract class User {

    private String userId;
    private String fullName;
    private String email;

    public User(String userId, String fullName, String email) {

        if (userId == null || userId.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "User information cannot be empty."
            );
        }

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
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
