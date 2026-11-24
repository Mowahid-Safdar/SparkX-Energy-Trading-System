
package sparkX.model;

public class Buyer extends User {

    private String consumerType;
    private double requiredUnits;
    private double maxPricePerUnit;
    private double totalSpent;

    // Original account-only constructor
    public Buyer(String userId, String fullName, String email) {
        this(userId, fullName, email, null);
    }

    // Account-only constructor with password
    public Buyer(String userId, String fullName, String email,
                 String password) {

        super(userId, fullName, email, password);

        consumerType = "Not configured";
        requiredUnits = 0;
        maxPricePerUnit = 0;
        totalSpent = 0;
    }

    // Full constructor for buyer profile configuration
    public Buyer(String userId, String fullName, String email,
                 String consumerType,
                 double requiredUnits,
                 double maxPricePerUnit) {

        super(userId, fullName, email);

        this.consumerType = "Not configured";
        this.requiredUnits = 0;
        this.maxPricePerUnit = 0;
        this.totalSpent = 0;

        configureProfile(
            consumerType,
            requiredUnits,
            maxPricePerUnit
        );
    }

    // Constructor used when restoring a saved buyer account
    public Buyer(String userId, String fullName, String email,
                 String storedPassword, boolean alreadyHashed,
                 String consumerType,
                 double requiredUnits,
                 double maxPricePerUnit,
                 double totalSpent) {

        super(userId, fullName, email, storedPassword, alreadyHashed);

        if (consumerType == null
                || consumerType.trim().isEmpty()
                || !Double.isFinite(requiredUnits)
                || requiredUnits < 0
                || !Double.isFinite(maxPricePerUnit)
                || maxPricePerUnit < 0
                || !Double.isFinite(totalSpent)
                || totalSpent < 0) {

            throw new IllegalArgumentException(
                "Invalid saved buyer information."
            );
        }

        this.consumerType = consumerType;
        this.requiredUnits = requiredUnits;
        this.maxPricePerUnit = maxPricePerUnit;
        this.totalSpent = totalSpent;
    }

    @Override
    public String getRole() {
        return "Buyer";
    }

    // Configures buyer marketplace requirements
    public void configureProfile(
            String consumerType,
            double requiredUnits,
            double maxPricePerUnit) {

        if (consumerType == null
                || consumerType.trim().isEmpty()
                || requiredUnits <= 0
                || !Double.isFinite(requiredUnits)
                || maxPricePerUnit <= 0
                || !Double.isFinite(maxPricePerUnit)) {

            throw new IllegalArgumentException(
                "Invalid buyer profile information."
            );
        }

        this.consumerType = consumerType;
        this.requiredUnits = requiredUnits;
        this.maxPricePerUnit = maxPricePerUnit;
    }

    // Checks whether buyer profile has been configured
    public boolean isProfileConfigured() {
        return !consumerType.equals("Not configured")
            && maxPricePerUnit > 0;
    }

    // Records a successful purchase
    public boolean recordPurchase(
            double units,
            double pricePerUnit) {

        if (!Double.isFinite(units)
                || !Double.isFinite(pricePerUnit)
                || units <= 0
                || units > requiredUnits
                || pricePerUnit <= 0
                || pricePerUnit > maxPricePerUnit) {

            return false;
        }

        requiredUnits -= units;
        totalSpent += units * pricePerUnit;

        return true;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public double getRequiredUnits() {
        return requiredUnits;
    }

    public double getMaxPricePerUnit() {
        return maxPricePerUnit;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setMaxPricePerUnit(double maxPricePerUnit) {

        if (maxPricePerUnit <= 0
                || !Double.isFinite(maxPricePerUnit)) {

            throw new IllegalArgumentException(
                "Maximum price must be greater than zero."
            );
        }

        this.maxPricePerUnit = maxPricePerUnit;
    }

    public boolean isDemandSatisfied() {
        return requiredUnits == 0;
    }
}
