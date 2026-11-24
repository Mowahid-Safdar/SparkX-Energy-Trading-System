
package sparkX.model;

import java.time.LocalDateTime;

public class Transaction {

    private final String transactionId;
    private final Buyer buyer;
    private final Seller seller;
    private final String offerId;
    private final double purchasedUnits;
    private final double pricePerUnit;
    private final double totalAmount;
    private final LocalDateTime transactionDate;

    // Normal transaction constructor
    public Transaction(
            String transactionId,
            Buyer buyer,
            Seller seller,
            String offerId,
            double purchasedUnits,
            double pricePerUnit) {

        this(
            transactionId,
            buyer,
            seller,
            offerId,
            purchasedUnits,
            pricePerUnit,
            LocalDateTime.now()
        );
    }

    // Constructor used when restoring saved transactions
    public Transaction(
            String transactionId,
            Buyer buyer,
            Seller seller,
            String offerId,
            double purchasedUnits,
            double pricePerUnit,
            LocalDateTime transactionDate) {

        if (transactionId == null
                || transactionId.trim().isEmpty()
                || buyer == null
                || seller == null
                || offerId == null
                || offerId.trim().isEmpty()
                || !Double.isFinite(purchasedUnits)
                || !Double.isFinite(pricePerUnit)
                || purchasedUnits <= 0
                || pricePerUnit <= 0
                || transactionDate == null) {

            throw new IllegalArgumentException(
                "Invalid transaction information."
            );
        }

        this.transactionId = transactionId;
        this.buyer = buyer;
        this.seller = seller;
        this.offerId = offerId;
        this.purchasedUnits = purchasedUnits;
        this.pricePerUnit = pricePerUnit;
        this.totalAmount = purchasedUnits * pricePerUnit;
        this.transactionDate = transactionDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public Seller getSeller() {
        return seller;
    }

    public String getOfferId() {
        return offerId;
    }

    public double getPurchasedUnits() {
        return purchasedUnits;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}
