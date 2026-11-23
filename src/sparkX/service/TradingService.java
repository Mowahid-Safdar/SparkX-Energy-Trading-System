
package sparkX.service;

import sparkX.model.Buyer;
import sparkX.model.EnergyOffer;
import sparkX.model.Seller;
import sparkX.model.Transaction;

public class TradingService {

    private EnergyOffer[] offers;
    private Transaction[] transactions;

    private int offerCount;
    private int transactionCount;

    private int nextOfferId;
    private int nextTransactionId;

    public TradingService() {

        offers = new EnergyOffer[100];
        transactions = new Transaction[500];

        offerCount = 0;
        transactionCount = 0;

        nextOfferId = 1;
        nextTransactionId = 1;
    }

    // Publish an offer using the seller's default price.
    public EnergyOffer publishOffer(Seller seller, double units) {

        if (seller == null) {
            throw new IllegalArgumentException(
                "Seller cannot be null."
            );
        }

        return publishOffer(
            seller, units, seller.getPricePerUnit()
        );
    }

    // Publish an offer using a specified price.
    public EnergyOffer publishOffer(
            Seller seller, double units, double pricePerUnit) {

        if (seller == null) {
            throw new IllegalArgumentException(
                "Seller cannot be null."
            );
        }

        if (offerCount >= offers.length) {
            throw new IllegalStateException(
                "Marketplace offer capacity is full."
            );
        }

        String offerId = "OFF-" + nextOfferId;

        EnergyOffer offer = new EnergyOffer(
            offerId, seller, units, pricePerUnit
        );

        offers[offerCount] = offer;

        offerCount++;
        nextOfferId++;

        return offer;
    }

    // Purchase energy from an existing offer.
    public Transaction buyUnits(
            Buyer buyer, EnergyOffer offer, double units) {

        if (buyer == null || offer == null) {
            throw new IllegalArgumentException(
                "Buyer and energy offer are required."
            );
        }

        boolean offerExists = false;

        for (int i = 0; i < offerCount; i++) {

            if (offers[i] == offer) {
                offerExists = true;
                break;
            }
        }

        if (!offerExists) {
            throw new IllegalArgumentException(
                "This offer does not belong to this marketplace."
            );
        }

        if (!offer.isActive()) {
            throw new IllegalArgumentException(
                "This offer is no longer active."
            );
        }

        if (units <= 0 || units > offer.getRemainingUnits()) {
            throw new IllegalArgumentException(
                "Invalid purchase quantity."
            );
        }

        if (units > buyer.getRequiredUnits()) {
            throw new IllegalArgumentException(
                "Purchase exceeds buyer's remaining demand."
            );
        }

        if (offer.getPricePerUnit() > buyer.getMaxPricePerUnit()) {
            throw new IllegalArgumentException(
                "Offer price exceeds buyer's maximum price."
            );
        }

        if (transactionCount >= transactions.length) {
            throw new IllegalStateException(
                "Transaction history is full."
            );
        }

        // Record the sale and update seller's earnings.
        if (!offer.recordSale(units)) {
            throw new IllegalStateException(
                "Sale could not be completed."
            );
        }

        // Update buyer's demand and spending.
        if (!buyer.recordPurchase(units, offer.getPricePerUnit())) {
            throw new IllegalStateException(
                "Buyer purchase could not be recorded."
            );
        }

        // Create and store transaction.
        String transactionId = "TXN-" + nextTransactionId;

        Transaction transaction = new Transaction(
            transactionId,
            buyer,
            offer.getSeller(),
            offer.getOfferId(),
            units,
            offer.getPricePerUnit()
        );

        transactions[transactionCount] = transaction;

        transactionCount++;
        nextTransactionId++;

        return transaction;
    }

    // Cancel an existing offer.
    public void cancelOffer(EnergyOffer offer) {

        if (offer == null) {
            throw new IllegalArgumentException(
                "Offer cannot be null."
            );
        }

        boolean offerExists = false;

        for (int i = 0; i < offerCount; i++) {

            if (offers[i] == offer) {
                offerExists = true;
                break;
            }
        }

        if (!offerExists) {
            throw new IllegalArgumentException(
                "Offer does not exist in this marketplace."
            );
        }

        if (!offer.isActive()) {
            throw new IllegalArgumentException(
                "This offer is already inactive."
            );
        }

        offer.cancelOffer();
    }

    // Display all marketplace offers.
    public void displayAllOffers() {

        for (int i = 0; i < offerCount; i++) {

            System.out.println(
                "Offer ID: " + offers[i].getOfferId()
            );

            System.out.println(
                "Seller: " + offers[i].getSeller().getFullName()
            );

            System.out.println(
                "Remaining Units: " + offers[i].getRemainingUnits()
            );

            System.out.println(
                "Price Per Unit: Rs. " + offers[i].getPricePerUnit()
            );

            System.out.println("-------------------------");
        }
    }

    // Display only active offers.
    public void displayActiveOffers() {

        for (int i = 0; i < offerCount; i++) {

            if (offers[i].isActive()) {

                System.out.println(
                    "Offer ID: " + offers[i].getOfferId()
                );

                System.out.println(
                    "Seller: " + offers[i].getSeller().getFullName()
                );

                System.out.println(
                    "Available Units: " + offers[i].getRemainingUnits()
                );

                System.out.println(
                    "Price Per Unit: Rs. " + offers[i].getPricePerUnit()
                );

                System.out.println("-------------------------");
            }
        }
    }

    // Display all completed transactions.
    public void displayTransactionHistory() {

        for (int i = 0; i < transactionCount; i++) {

            System.out.println(
                "Transaction ID: "
                + transactions[i].getTransactionId()
            );

            System.out.println(
                "Buyer: "
                + transactions[i].getBuyer().getFullName()
            );

            System.out.println(
                "Seller: "
                + transactions[i].getSeller().getFullName()
            );

            System.out.println(
                "Purchased Units: "
                + transactions[i].getPurchasedUnits()
            );

            System.out.println(
                "Total Amount: Rs. "
                + transactions[i].getTotalAmount()
            );

            System.out.println("-------------------------");
        }
    }

    // Return the number of published offers.
    public int getOfferCount() {
        return offerCount;
    }

    // Return the number of completed transactions.
    public int getTransactionCount() {
        return transactionCount;
    }

    // Return an offer using its array position.
    public EnergyOffer getOffer(int index) {

        if (index < 0 || index >= offerCount) {
            throw new IndexOutOfBoundsException(
                "Invalid offer index."
            );
        }

        return offers[index];
    }

    // Return a transaction using its array position.
    public Transaction getTransaction(int index) {

        if (index < 0 || index >= transactionCount) {
            throw new IndexOutOfBoundsException(
                "Invalid transaction index."
            );
        }

        return transactions[index];
    }
}
