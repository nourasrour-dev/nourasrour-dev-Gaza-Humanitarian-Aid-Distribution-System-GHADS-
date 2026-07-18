/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gaza.aid.tracker;

import java.time.LocalDate;

/**
 * Represents a single distribution operation linking an organization, family, quantity, and status.
 */
public class AidDistribution implements Comparable<AidDistribution> {

    // Constants: Define standard distribution status strings
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_WAITING = "On Waiting List (Less than 30 days)";

    // Data Fields: Store distribution identification, associated entities, quantity, and status
    private String distribution_ID;
    private Organization distribution_Org;
    private Family receiving_Family;
    private int distributed_Quantity;
    private String receipt_status;

    // Constructor: Creates a new distribution from UI, calculates eligibility, and updates family records
    public AidDistribution(String distribution_ID, Organization distribution_Org,
            Family receiving_Family, int distributed_Quantity) {
        initialize(distribution_ID, distribution_Org, receiving_Family, distributed_Quantity);

        if (receiving_Family.isEligibleForAid()) {
            receipt_status = STATUS_DELIVERED;
            receiving_Family.setLast_Aid_Date(LocalDate.now().toString());
        } else {
            receipt_status = STATUS_WAITING;
        }
    }

    // Constructor: Loads an existing record from file, preserving saved state without re-calculation
    public AidDistribution(String distribution_ID, Organization distribution_Org,
            Family receiving_Family, int distributed_Quantity, String savedStatus) {
        initialize(distribution_ID, distribution_Org, receiving_Family, distributed_Quantity);
        receipt_status = (savedStatus == null || savedStatus.trim().isEmpty())
                ? STATUS_WAITING : savedStatus.trim();
    }

    // Private Helper Method: Centralizes validation and attribute assignment logic
    private void initialize(String distribution_ID, Organization distribution_Org,
            Family receiving_Family, int distributed_Quantity) {
        if (distribution_ID == null || distribution_ID.trim().isEmpty()) {
            throw new IllegalArgumentException("Distribution ID is required.");
        }
        if (distribution_Org == null || receiving_Family == null) {
            throw new IllegalArgumentException("Organization and family are required.");
        }
        if (distributed_Quantity <= 0) {
            throw new IllegalArgumentException("Distributed quantity must be greater than zero.");
        }

        this.distribution_ID = distribution_ID.trim();
        this.distribution_Org = distribution_Org;
        this.receiving_Family = receiving_Family;
        this.distributed_Quantity = distributed_Quantity;
    }

    // Getter Method: Returns the distribution ID
    public String getDistribution_ID() {
        return distribution_ID;
    }

    // Setter Method: Updates the distribution ID
    public void setDistribution_ID(String distribution_ID) {
        this.distribution_ID = distribution_ID;
    }

    // Getter Method: Returns the organization
    public Organization getDistribution_Org() {
        return distribution_Org;
    }

    // Setter Method: Updates the organization
    public void setDistribution_Org(Organization distribution_Org) {
        this.distribution_Org = distribution_Org;
    }

    // Getter Method: Returns the receiving family
    public Family getReceiving_Family() {
        return receiving_Family;
    }

    // Setter Method: Updates the receiving family
    public void setReceiving_Family(Family receiving_Family) {
        this.receiving_Family = receiving_Family;
    }

    // Getter Method: Returns the distributed quantity
    public int getDistributed_Quantity() {
        return distributed_Quantity;
    }

    // Setter Method: Updates the distributed quantity with validation
    public void setDistributed_Quantity(int distributed_Quantity) {
        if (distributed_Quantity <= 0) {
            throw new IllegalArgumentException("Distributed quantity must be greater than zero.");
        }
        this.distributed_Quantity = distributed_Quantity;
    }

    // Getter Method: Returns the receipt status
    public String getReceipt_status() {
        return receipt_status;
    }

    // Setter Method: Updates the receipt status
    public void setReceipt_status(String receipt_status) {
        if (receipt_status == null || receipt_status.trim().isEmpty()) {
            throw new IllegalArgumentException("Receipt status is required.");
        }
        this.receipt_status = receipt_status.trim();
    }

    // Logic Method: Checks if the distribution status is delivered
    public boolean isDelivered() {
        return STATUS_DELIVERED.equalsIgnoreCase(receipt_status);
    }

    // Logic Method: Compares distributions for sorting (prioritizes displaced families, then member count)
    @Override
    public int compareTo(AidDistribution other) {
        if (this.receiving_Family.isIs_Displaced()
                && !other.receiving_Family.isIs_Displaced()) {
            return -1;
        }
        if (!this.receiving_Family.isIs_Displaced()
                && other.receiving_Family.isIs_Displaced()) {
            return 1;
        }

        return Integer.compare(
                other.receiving_Family.getMember_Count(),
                this.receiving_Family.getMember_Count()
        );
    }

    // Method: Returns a string representation of the distribution record
    @Override
    public String toString() {
        return "Receipt No: " + distribution_ID
                + " | Qty: " + distributed_Quantity + " units\n"
                + "Family Data -> " + receiving_Family.toString() + "\n"
                + "Status: [" + receipt_status + "]\n";
    }
}