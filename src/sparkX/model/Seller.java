
package sparkX.model;

public class Seller extends User {

    private String sellerType;
    private String energySource;

    private double generationCapacityKw;
    private double operatingHours;
    private double performanceRatio;
    private double ownConsumptionUnits;

    private double generatedUnits;
    private double availableUnits;
    private double reservedUnits;

    private double pricePerUnit;
    private double totalEarnings;

    public Seller(String userId, String fullName, String email,
                  String sellerType, String energySource,
                  double generationCapacityKw,
                  double operatingHours,
                  double performanceRatio,
                  double ownConsumptionUnits,
                  double pricePerUnit) {

        super(userId, fullName, email);

        if (sellerType == null || sellerType.trim().isEmpty()
                || energySource == null || energySource.trim().isEmpty()
                || generationCapacityKw <= 0
                || operatingHours <= 0 || operatingHours > 24
                || performanceRatio <= 0 || performanceRatio > 1
                || ownConsumptionUnits < 0
                || pricePerUnit <= 0) {

            throw new IllegalArgumentException(
                "Invalid seller information."
            );
        }

        this.sellerType = sellerType;
        this.energySource = energySource;
        this.generationCapacityKw = generationCapacityKw;
        this.operatingHours = operatingHours;
        this.performanceRatio = performanceRatio;
        this.ownConsumptionUnits = ownConsumptionUnits;

        this.pricePerUnit = pricePerUnit;

        this.generatedUnits = 0;
        this.availableUnits = 0;
        this.reservedUnits = 0;
        this.totalEarnings = 0;
    }

    @Override
    public String getRole() {
        return "Seller";
    }

    public double generateDailyEnergy() {

        generatedUnits = generationCapacityKw
                * operatingHours
                * performanceRatio;

        availableUnits = Math.max(
            0, generatedUnits - ownConsumptionUnits
        );

        reservedUnits = 0;

        return generatedUnits;
    }

    public boolean reserveUnits(double units) {

        if (units <= 0 || units > availableUnits) {
            return false;
        }

        availableUnits -= units;
        reservedUnits += units;

        return true;
    }

    public boolean completeSale(double units, double salePrice) {

        if (units <= 0 || units > reservedUnits || salePrice <= 0) {
            return false;
        }

        reservedUnits -= units;
        totalEarnings += units * salePrice;

        return true;
    }

    public void releaseReservedUnits(double units) {

        if (units <= 0 || units > reservedUnits) {
            throw new IllegalArgumentException(
                "Invalid reservation quantity."
            );
        }

        reservedUnits -= units;
        availableUnits += units;
    }

    public double getGeneratedUnits() {
        return generatedUnits;
    }

    public double getAvailableUnits() {
        return availableUnits;
    }

    public double getReservedUnits() {
        return reservedUnits;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public String getSellerType() {
        return sellerType;
    }

    public String getEnergySource() {
        return energySource;
    }

    public double getGenerationCapacityKw() {
        return generationCapacityKw;
    }

    public void setPricePerUnit(double pricePerUnit) {

        if (pricePerUnit <= 0) {
            throw new IllegalArgumentException(
                "Price must be greater than zero."
            );
        }

        this.pricePerUnit = pricePerUnit;
    }
}
