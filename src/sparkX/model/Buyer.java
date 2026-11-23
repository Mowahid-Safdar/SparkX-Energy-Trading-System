
package sparkX.model;

public class Buyer extends User {

    private String consumerType;

    private double requiredUnits;
    private double maxPricePerUnit;
    private double totalSpent;

    public Buyer(String userId, String fullName, String email,
                 String consumerType,
                 double requiredUnits,
                 double maxPricePerUnit) {

        super(userId, fullName, email);

        if (consumerType == null || consumerType.trim().isEmpty()
                || requiredUnits <= 0
                || maxPricePerUnit <= 0) {

            throw new IllegalArgumentException(
                "Invalid buyer information."
            );
        }

        this.consumerType = consumerType;
        this.requiredUnits = requiredUnits;
        this.maxPricePerUnit = maxPricePerUnit;
        this.totalSpent = 0;
    }

    @Override
    public String getRole() {
        return "Buyer";
    }

    public boolean recordPurchase(double units, double pricePerUnit) {

        if (units <= 0 || units > requiredUnits
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

        if (maxPricePerUnit <= 0) {
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
