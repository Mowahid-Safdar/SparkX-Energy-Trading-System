
package sparkX.model;

import java.time.LocalDateTime;

public class EnergyOffer {

    private String offerId;
    private Seller seller;
    private double offeredUnits;
    private double remainingUnits;
    private double pricePerUnit;
    private LocalDateTime createdAt;
    private boolean active;

    // Normal constructor: creates a new marketplace offer
    public EnergyOffer(String offerId, Seller seller,
                       double offeredUnits, double pricePerUnit) {

        if (offerId == null || offerId.trim().isEmpty()
                || seller == null
                || !Double.isFinite(offeredUnits)
                || !Double.isFinite(pricePerUnit)
                || offeredUnits <= 0
                || pricePerUnit <= 0) {

            throw new IllegalArgumentException(
                "Invalid energy offer."
            );
        }

        if (!seller.reserveUnits(offeredUnits)) {
            throw new IllegalArgumentException(
                "Seller does not have enough available units."
            );
        }

        this.offerId = offerId;
        this.seller = seller;
        this.offeredUnits = offeredUnits;
        this.remainingUnits = offeredUnits;
        this.pricePerUnit = pricePerUnit;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Restoration constructor: restores an existing offer
    public EnergyOffer(String offerId, Seller seller,
                       double offeredUnits, double remainingUnits,
                       double pricePerUnit, LocalDateTime createdAt,
                       boolean active, boolean restoring) {

        if (!restoring
                || offerId == null
                || offerId.trim().isEmpty()
                || seller == null
                || !Double.isFinite(offeredUnits)
                || !Double.isFinite(remainingUnits)
                || !Double.isFinite(pricePerUnit)
                || offeredUnits <= 0
                || remainingUnits < 0
                || remainingUnits > offeredUnits
                || pricePerUnit <= 0
                || createdAt == null
                || (active && remainingUnits <= 0)) {

            throw new IllegalArgumentException(
                "Invalid saved energy offer."
            );
        }

        this.offerId = offerId;
        this.seller = seller;
        this.offeredUnits = offeredUnits;
        this.remainingUnits = remainingUnits;
        this.pricePerUnit = pricePerUnit;
        this.createdAt = createdAt;
        this.active = active;
    }

    // Records a sale
    public boolean recordSale(double units) {

        if (!active || units <= 0 || units > remainingUnits) {
            return false;
        }

        if (!seller.completeSale(units, pricePerUnit)) {
            return false;
        }

        remainingUnits -= units;

        if (remainingUnits == 0) {
            active = false;
        }

        return true;
    }

    // Cancels an offer
    public void cancelOffer() {

        if (!active) {
            return;
        }

        seller.releaseReservedUnits(remainingUnits);

        remainingUnits = 0;
        active = false;
    }

    public String getOfferId() {
        return offerId;
    }

    public Seller getSeller() {
        return seller;
    }

    public double getOfferedUnits() {
        return offeredUnits;
    }

    public double getRemainingUnits() {
        return remainingUnits;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public double getOfferValue() {
        return remainingUnits * pricePerUnit;
    }
}
