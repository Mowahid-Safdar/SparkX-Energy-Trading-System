
package sparkX.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import sparkX.model.EnergyOffer;
import sparkX.model.User;
import sparkX.model.Buyer;
import sparkX.model.Seller;
import sparkX.model.Transaction;
import sparkX.service.TradingService;

public class MarketplaceData {

    private User[] users;
    private int userCount;
    private TradingService tradingService;

    private static final String DATA_DIRECTORY = "sparkx-data";
    private static final String DATA_FILE = "users.dat";

    private static final int FILE_MAGIC = 0x53504B58;
    private static final int FILE_VERSION = 3;

    public MarketplaceData() {

        users = new User[100];
        userCount = 0;
        tradingService = new TradingService();

        loadUsers();
    }

    // Registers a new user
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

        try {
            saveUsers();
        } catch (RuntimeException e) {

            userCount--;
            users[userCount] = null;

            throw e;
        }
    }

    // Finds a user using their email
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

    // Finds a user using their ID
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

    public User getUser(int index) {

        if (index < 0 || index >= userCount) {
            throw new IndexOutOfBoundsException(
                "Invalid user index."
            );
        }

        return users[index];
    }

    public int getUserCount() {
        return userCount;
    }

    public TradingService getTradingService() {
        return tradingService;
    }

    // Saves accounts, offers, and transactions
    public void saveUsers() {

        File directory = new File(DATA_DIRECTORY);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException(
                "Could not create data directory."
            );
        }

        File file = new File(directory, DATA_FILE);

        try (DataOutputStream output = new DataOutputStream(
                new FileOutputStream(file))) {

            output.writeInt(FILE_MAGIC);
            output.writeInt(FILE_VERSION);
            output.writeInt(userCount);

            // Save accounts and their profile information
            for (int i = 0; i < userCount; i++) {

                User user = users[i];

                output.writeUTF(user.getRole());
                output.writeUTF(user.getUserId());
                output.writeUTF(user.getFullName());
                output.writeUTF(user.getEmail());
                output.writeUTF(user.getStoredPassword());

                if (user instanceof Seller) {

                    Seller seller = (Seller) user;

                    output.writeUTF(seller.getSellerType());
                    output.writeUTF(seller.getEnergySource());
                    output.writeDouble(seller.getGenerationCapacityKw());
                    output.writeDouble(seller.getOperatingHours());
                    output.writeDouble(seller.getPerformanceRatio());
                    output.writeDouble(seller.getOwnConsumptionUnits());
                    output.writeDouble(seller.getGeneratedUnits());
                    output.writeDouble(seller.getAvailableUnits());
                    output.writeDouble(seller.getReservedUnits());
                    output.writeDouble(seller.getPricePerUnit());
                    output.writeDouble(seller.getTotalEarnings());

                } else if (user instanceof Buyer) {

                    Buyer buyer = (Buyer) user;

                    output.writeUTF(buyer.getConsumerType());
                    output.writeDouble(buyer.getRequiredUnits());
                    output.writeDouble(buyer.getMaxPricePerUnit());
                    output.writeDouble(buyer.getTotalSpent());
                }
            }

            // Save marketplace offers
            output.writeInt(tradingService.getOfferCount());

            for (int i = 0; i < tradingService.getOfferCount(); i++) {

                EnergyOffer offer = tradingService.getOffer(i);

                output.writeUTF(offer.getOfferId());
                output.writeUTF(offer.getSeller().getUserId());
                output.writeDouble(offer.getOfferedUnits());
                output.writeDouble(offer.getRemainingUnits());
                output.writeDouble(offer.getPricePerUnit());
                output.writeUTF(offer.getCreatedAt().toString());
                output.writeBoolean(offer.isActive());
            }

            // Save completed transactions
            output.writeInt(tradingService.getTransactionCount());

            for (int i = 0;
                    i < tradingService.getTransactionCount();
                    i++) {

                Transaction transaction =
                    tradingService.getTransaction(i);

                output.writeUTF(transaction.getTransactionId());
                output.writeUTF(transaction.getBuyer().getUserId());
                output.writeUTF(transaction.getSeller().getUserId());
                output.writeUTF(transaction.getOfferId());
                output.writeDouble(transaction.getPurchasedUnits());
                output.writeDouble(transaction.getPricePerUnit());
                output.writeUTF(transaction.getTransactionDate().toString());
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                "Could not save accounts, offers, and transactions.",
                e
            );
        }
    }

    // Loads accounts, offers, and transactions
    private void loadUsers() {

        File file = new File(DATA_DIRECTORY, DATA_FILE);

        if (!file.exists()) {
            return;
        }

        try (DataInputStream input = new DataInputStream(
                new FileInputStream(file))) {

            int magic = input.readInt();
            int version = input.readInt();

            if (magic != FILE_MAGIC
                    || (version != 1
                    && version != 2
                    && version != 3)) {

                throw new IllegalStateException(
                    "Unsupported or invalid account file."
                );
            }

            int savedUserCount = input.readInt();

            if (savedUserCount < 0
                    || savedUserCount > users.length) {

                throw new IllegalStateException(
                    "Invalid saved account count."
                );
            }

            // Restore accounts first
            for (int i = 0; i < savedUserCount; i++) {

                String role = input.readUTF();
                String userId = input.readUTF();
                String fullName = input.readUTF();
                String email = input.readUTF();
                String storedPassword = input.readUTF();

                User user;

                if (role.equals("Seller")) {

                    user = new Seller(
                        userId,
                        fullName,
                        email,
                        storedPassword,
                        true,
                        input.readUTF(),
                        input.readUTF(),
                        input.readDouble(),
                        input.readDouble(),
                        input.readDouble(),
                        input.readDouble(),
                        input.readDouble(),
                        input.readDouble(),
                        input.readDouble(),
                        input.readDouble(),
                        input.readDouble()
                    );

                } else if (role.equals("Buyer")) {

                    user = new Buyer(
                        userId,
                        fullName,
                        email,
                        storedPassword,
                        true,
                        input.readUTF(),
                        input.readDouble(),
                        input.readDouble(),
                        input.readDouble()
                    );

                } else {

                    throw new IllegalStateException(
                        "Unknown saved account role."
                    );
                }

                users[userCount] = user;
                userCount++;
            }

            // Version 1 contains accounts only.
            // Versions 2 and 3 also contain offers.
            if (version >= 2) {

                int savedOfferCount = input.readInt();

                if (savedOfferCount < 0
                        || savedOfferCount > 100) {

                    throw new IllegalStateException(
                        "Invalid saved offer count."
                    );
                }

                // Restore offers
                for (int i = 0; i < savedOfferCount; i++) {

                    String offerId = input.readUTF();
                    String sellerId = input.readUTF();

                    double offeredUnits = input.readDouble();
                    double remainingUnits = input.readDouble();
                    double pricePerUnit = input.readDouble();

                    LocalDateTime createdAt =
                        LocalDateTime.parse(input.readUTF());

                    boolean active = input.readBoolean();

                    User savedSeller = findUserById(sellerId);

                    if (!(savedSeller instanceof Seller)) {
                        throw new IllegalStateException(
                            "Saved offer refers to an unknown seller."
                        );
                    }

                    EnergyOffer offer = new EnergyOffer(
                        offerId,
                        (Seller) savedSeller,
                        offeredUnits,
                        remainingUnits,
                        pricePerUnit,
                        createdAt,
                        active,
                        true
                    );

                    tradingService.restoreOffer(offer);
                }
            }

            // Version 3 also contains completed transactions.
            if (version >= 3) {

                int savedTransactionCount = input.readInt();

                if (savedTransactionCount < 0
                        || savedTransactionCount > 500) {

                    throw new IllegalStateException(
                        "Invalid saved transaction count."
                    );
                }

                // Restore transactions after accounts and offers
                for (int i = 0; i < savedTransactionCount; i++) {

                    String transactionId = input.readUTF();
                    String buyerId = input.readUTF();
                    String sellerId = input.readUTF();
                    String offerId = input.readUTF();

                    double purchasedUnits = input.readDouble();
                    double pricePerUnit = input.readDouble();

                    LocalDateTime transactionDate =
                        LocalDateTime.parse(input.readUTF());

                    User savedBuyer = findUserById(buyerId);
                    User savedSeller = findUserById(sellerId);

                    if (!(savedBuyer instanceof Buyer)) {
                        throw new IllegalStateException(
                            "Saved transaction refers to an unknown buyer."
                        );
                    }

                    if (!(savedSeller instanceof Seller)) {
                        throw new IllegalStateException(
                            "Saved transaction refers to an unknown seller."
                        );
                    }

                    Transaction transaction = new Transaction(
                        transactionId,
                        (Buyer) savedBuyer,
                        (Seller) savedSeller,
                        offerId,
                        purchasedUnits,
                        pricePerUnit,
                        transactionDate
                    );

                    tradingService.restoreTransaction(transaction);
                }
            }

            if (input.available() > 0) {
                throw new IllegalStateException(
                    "Unexpected data found in account file."
                );
            }

        } catch (IOException | IllegalArgumentException e) {

            throw new IllegalStateException(
                "Could not load saved accounts, offers, and transactions. "
                + "The account file may be damaged.",
                e
            );
        }
    }
}
