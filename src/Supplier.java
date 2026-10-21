public class Supplier implements ViewDetails {
    private String supplierID;
    private String supplierName;
    private String contactInfo;

    public Supplier(String supplierID, String supplierName, String contactInfo) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.contactInfo = contactInfo;
    }

    public String getSupplierID()   { return supplierID; }
    public String getSupplierName() { return supplierName; }
    public String getContactInfo()  { return contactInfo; }

    /** Delivers an approved restock order addressed to this supplier. Returns false if it can't be fulfilled. */
    public boolean fulfillOrder(Order o) {
        if (o == null) return false;
        if (!o.getSupplier().getSupplierID().equals(this.supplierID)) return false;   // must be our order
        if (!OrderStatus.APPROVED.equals(o.getStatus())) return false;                // must be approved
        o.completeOrder();
        return true;
    }

    @Override
    public void viewDetails() {
        System.out.println("Supplier ID: " + supplierID + "\nName: " + supplierName + "\nContact: " + contactInfo);
    }
}