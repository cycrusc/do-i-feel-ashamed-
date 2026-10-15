public class Supplier implements ViewDetails {
    private final String supplierID;
    private final String supplierName;
    private final String contactInfo;

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
        if (o == null || o.getSupplier() != this || !OrderStatus.APPROVED.equals(o.getStatus())) return false;
        o.completeOrder();
        return true;
    }

    @Override
    public void viewDetails() {
        System.out.println("Supplier ID: " + supplierID + "\nName: " + supplierName + "\nContact: " + contactInfo);
    }
}