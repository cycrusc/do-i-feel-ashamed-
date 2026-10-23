import java.util.Date;

/** An order a customer places for a product. These are the items held in the OrderQueue. */
public class CustomerOrder implements ViewDetails {
    private static int counter = 1;

    private String orderID;
    private String customerName;
    private String productID;
    private int quantity;
    private String orderDate;
    private String status;

    public CustomerOrder(String customerName, String productID, int quantity) {
        if (customerName == null || customerName.trim().isEmpty())
            throw new IllegalArgumentException("Customer name is required.");
        if (productID == null || productID.trim().isEmpty())
            throw new IllegalArgumentException("Product ID is required.");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be at least 1.");
        this.orderID = String.format("CO-%03d", counter++);
        this.customerName = customerName.trim();
        this.productID = productID.trim();
        this.quantity = quantity;
        this.orderDate = new Date().toString();
        this.status = OrderStatus.PENDING;
    }

    public String getOrderID()      { return orderID; }
    public String getCustomerName() { return customerName; }
    public String getProductID()    { return productID; }
    public int getQuantity()        { return quantity; }
    public String getOrderDate()   { return orderDate; }
    public String getStatus()       { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getSummary() {
        return orderID + " | " + customerName + " | " + productID + " x " + quantity + " | " + status;
    }

    @Override
    public void viewDetails() {
        System.out.println("Order ID: " + orderID + "\nCustomer: " + customerName
                + "\nProduct ID: " + productID + "\nQuantity: " + quantity
                + "\nDate: " + orderDate + "\nStatus: " + status);
    }
}