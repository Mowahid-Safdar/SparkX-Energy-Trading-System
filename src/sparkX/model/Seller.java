
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

    // Account-only constructor
    public Seller(String userId, String fullName, String email) {
        this(userId, fullName, email, null);
    }

    // Account-only constructor with password
    public Seller(String userId, String fullName, String email,
                  String password) {

        super(userId, fullName, email, password);

        sellerType = "Not configured";
        energySource = "Not configured";

        generationCapacityKw = 0;
        operatingHours = 0;
        performanceRatio = 0;
        ownConsumptionUnits = 0;

        generatedUnits = 0;
        availableUnits = 0;
        reservedUnits = 0;

        pricePerUnit = 0;
        totalEarnings = 0;
    }

    // Full constructor for an already configured seller
    public Seller(String userId, String fullName, String email,
                  String sellerType, String energySource,
                  double generationCapacityKw,
                  double operatingHours,
                  double performanceRatio,
                  double ownConsumptionUnits,
                  double pricePerUnit) {

        super(userId, fullName, email);

        this.sellerType = "Not configured";
        this.energySource = "Not configured";

        configureProfile(
            sellerType,
            energySource,
            generationCapacityKw,
            operatingHours,
            performanceRatio,
            ownConsumptionUnits,
            pricePerUnit
        );
    }

    // Constructor used when restoring a saved seller account
    public Seller(String userId, String fullName, String email,
                  String storedPassword, boolean alreadyHashed,
                  String sellerType, String energySource,
                  double generationCapacityKw,
                  double operatingHours,
                  double performanceRatio,
                  double ownConsumptionUnits,
                  double generatedUnits,
                  double availableUnits,
                  double reservedUnits,
                  double pricePerUnit,
                  double totalEarnings) {

        super(userId, fullName, email, storedPassword, alreadyHashed);

        if (sellerType == null || sellerType.trim().isEmpty()
                || energySource == null || energySource.trim().isEmpty()
                || !Double.isFinite(generationCapacityKw)
                || !Double.isFinite(operatingHours)
                || !Double.isFinite(performanceRatio)
                || !Double.isFinite(ownConsumptionUnits)
                || !Double.isFinite(generatedUnits)
                || !Double.isFinite(availableUnits)
                || !Double.isFinite(reservedUnits)
                || !Double.isFinite(pricePerUnit)
                || !Double.isFinite(totalEarnings)
                || generationCapacityKw < 0
                || operatingHours < 0
                || operatingHours > 24
                || performanceRatio < 0
                || performanceRatio > 1
                || ownConsumptionUnits < 0
                || generatedUnits < 0
                || availableUnits < 0
                || reservedUnits < 0
                || pricePerUnit < 0
                || totalEarnings < 0) {

            throw new IllegalArgumentException(
                "Invalid saved seller information."
            );
        }

        boolean configured = !sellerType.equals("Not configured")
                && !energySource.equals("Not configured");

        if (configured
                && (generationCapacityKw <= 0
                || operatingHours <= 0
                || performanceRatio <= 0
                || pricePerUnit <= 0)) {

            throw new IllegalArgumentException(
                "Invalid saved seller profile."
            );
        }

        this.sellerType = sellerType;
        this.energySource = energySource;

        this.generationCapacityKw = generationCapacityKw;
        this.operatingHours = operatingHours;
        this.performanceRatio = performanceRatio;
        this.ownConsumptionUnits = ownConsumptionUnits;

        this.generatedUnits = generatedUnits;
        this.availableUnits = availableUnits;
        this.reservedUnits = reservedUnits;

        this.pricePerUnit = pricePerUnit;
        this.totalEarnings = totalEarnings;
    }

    @Override
    public String getRole() {
        return "Seller";
    }

    // Saves or updates seller profile information
    public void configureProfile(
            String sellerType,
            String energySource,
            double generationCapacityKw,
            double operatingHours,
            double performanceRatio,
            double ownConsumptionUnits,
            double pricePerUnit) {

        if (sellerType == null || sellerType.trim().isEmpty()
                || energySource == null || energySource.trim().isEmpty()
                || generationCapacityKw <= 0
                || operatingHours <= 0 || operatingHours > 24
                || performanceRatio <= 0 || performanceRatio > 1
                || ownConsumptionUnits < 0
                || pricePerUnit <= 0
                || !Double.isFinite(generationCapacityKw)
                || !Double.isFinite(operatingHours)
                || !Double.isFinite(performanceRatio)
                || !Double.isFinite(ownConsumptionUnits)
                || !Double.isFinite(pricePerUnit)) {

            throw new IllegalArgumentException(
                "Invalid seller profile information."
            );
        }

        this.sellerType = sellerType;
        this.energySource = energySource;

        this.generationCapacityKw = generationCapacityKw;
        this.operatingHours = operatingHours;
        this.performanceRatio = performanceRatio;
        this.ownConsumptionUnits = ownConsumptionUnits;

        this.pricePerUnit = pricePerUnit;
    }

    // Generates estimated daily energy
    public double generateDailyEnergy() {

        // Prevent overwriting energy already available or reserved.
        if (availableUnits > 0 || reservedUnits > 0) {

            throw new IllegalStateException(
                "Existing energy must be traded or released "
                + "before generating again."
            );
        }

        generatedUnits = generationCapacityKw
                * operatingHours
                * performanceRatio;

        availableUnits = Math.max(
            0, generatedUnits - ownConsumptionUnits
        );

        return generatedUnits;
    }

    public boolean reserveUnits(double units) {

        if (!Double.isFinite(units)
                || units <= 0
                || units > availableUnits) {

            return false;
        }

        availableUnits -= units;
        reservedUnits += units;

        return true;
    }

    public boolean completeSale(double units, double salePrice) {

        if (!Double.isFinite(units)
                || !Double.isFinite(salePrice)
                || units <= 0
                || units > reservedUnits
                || salePrice <= 0) {

            return false;
        }

        reservedUnits -= units;
        totalEarnings += units * salePrice;

        return true;
    }

    public void releaseReservedUnits(double units) {

        if (!Double.isFinite(units)
                || units <= 0
                || units > reservedUnits) {

            throw new IllegalArgumentException(
                "Invalid reservation quantity."
            );
        }

        reservedUnits -= units;
        availableUnits += units;
    }

    // Checks whether the seller has configured their profile
    public boolean isProfileConfigured() {

        return !sellerType.equals("Not configured")
                && !energySource.equals("Not configured");
    }

    // Getters
    public String getSellerType() {
        return sellerType;
    }

    public String getEnergySource() {
        return energySource;
    }

    public double getGenerationCapacityKw() {
        return generationCapacityKw;
    }

    public double getOperatingHours() {
        return operatingHours;
    }

    public double getPerformanceRatio() {
        return performanceRatio;
    }

    public double getOwnConsumptionUnits() {
        return ownConsumptionUnits;
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

    // Updates the default selling price
    public void setPricePerUnit(double pricePerUnit) {

        if (pricePerUnit <= 0
                || !Double.isFinite(pricePerUnit)) {

            throw new IllegalArgumentException(
                "Price must be greater than zero."
            );
        }

        this.pricePerUnit = pricePerUnit;
    }
}
