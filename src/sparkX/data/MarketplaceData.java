
package sparkX.data;

import sparkX.model.User;
import sparkX.service.TradingService;

public class MarketplaceData {

    private User[] users;
    private int userCount;

    private TradingService tradingService;

    public MarketplaceData() {

        users = new User[100];
        userCount = 0;

        tradingService = new TradingService();
    }

    // Register a new user.
    public void registerUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                "User cannot be null."
            );
        }

        if (userCount >= users.length) {
            throw new IllegalStateException(
                "User registration capacity is full."
            );
        }

        // Check for duplicate user ID and email.
        for (int i = 0; i < userCount; i++) {

            if (users[i].getUserId().equalsIgnoreCase(
                    user.getUserId())) {

                throw new IllegalArgumentException(
                    "User ID already exists."
                );
            }

            if (users[i].getEmail().equalsIgnoreCase(
                    user.getEmail())) {

                throw new IllegalArgumentException(
                    "Email is already registered."
                );
            }
        }

        users[userCount] = user;
        userCount++;
    }

    // Find a user using their email.
    public User findUserByEmail(String email) {

        if (email == null) {
            return null;
        }

        for (int i = 0; i < userCount; i++) {

            if (users[i].getEmail().equalsIgnoreCase(email)) {
                return users[i];
            }
        }

        return null;
    }

    // Find a user using their ID.
    public User findUserById(String userId) {

        if (userId == null) {
            return null;
        }

        for (int i = 0; i < userCount; i++) {

            if (users[i].getUserId().equalsIgnoreCase(userId)) {
                return users[i];
            }
        }

        return null;
    }

    // Return a user using their array position.
    public User getUser(int index) {

        if (index < 0 || index >= userCount) {
            throw new IndexOutOfBoundsException(
                "Invalid user index."
            );
        }

        return users[index];
    }

    // Return the number of registered users.
    public int getUserCount() {
        return userCount;
    }

    // Provide access to the marketplace service.
    public TradingService getTradingService() {
        return tradingService;
    }
}
