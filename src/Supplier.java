public class Supplier implements ViewDetails{
    private String supplierID;
    private String supplierName;
    private String contactInfo;

    public void viewDetails() { // idk bout this
        System.out.println("supplierID: " + supplierID + "\nsupplierName: " + supplierName + "\ncontactInfo: " + contactInfo);
    }

    public void fullFillOrders(Order order){}

}
