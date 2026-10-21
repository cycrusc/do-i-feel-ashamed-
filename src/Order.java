import java.util.Date;

/** A restock (purchase) order sent to a supplier. Flow: CREATED -> PENDING -> APPROVED -> COMPLETED (or REJECTED). */
public class Order implements ViewDetails {
    private static int counter = 1;

    private String orderID;
    private Product product;
    private Supplier supplier;
    private int quantityOrdered;
    private String orderDate;
    private String status = OrderStatus.CREATED;

    public Order(Product product, int quantityOrdered, Supplier supplier) {
        if (product == null) throw new IllegalArgumentException("Product is required.");
        if (supplier == null) throw new IllegalArgumentException("Supplier is required.");
        if (quantityOrdered <= 0) throw new IllegalArgumentException("Quantity must be at least 1.");
        this.orderID = String.format("PO-%03d", counter++);
        this.product = product;
        this.supplier = supplier;
        this.quantityOrdered = quantityOrdered;
    }

    public String getOrderID()       { return orderID; }
    public Product getProduct()      { return product; }
    public Supplier getSupplier()    { return supplier; }
    public int getQuantityOrdered()  { return quantityOrdered; }
    public String getOrderDate()   { return orderDate; }
    public String getStatus()        { return status; }

    public void placeOrder() {
        requireStatus(OrderStatus.CREATED, "placed");
        orderDate = new Date().toString();
        status = OrderStatus.PENDING;
    }

    public void approve() {
        requireStatus(OrderStatus.PENDING, "approved");
        status = OrderStatus.APPROVED;
    }

    public void reject() {
        requireStatus(OrderStatus.PENDING, "rejected");
        status = OrderStatus.REJECTED;
    }

    /** Stock arrives: adds the ordered quantity to the product. */
    public void completeOrder() {
        requireStatus(OrderStatus.APPROVED, "completed");
        product.addStock(quantityOrdered);
        status = OrderStatus.COMPLETED;
    }

    private void requireStatus(String expected, String action) {
        if (!status.equals(expected))
            throw new IllegalStateException("Order " + orderID + " cannot be " + action + " while " + status + ".");
    }

    public String getSummary() {
        return orderID + " | " + product.getProductName() + " x " + quantityOrdered
                + " | " + supplier.getSupplierName() + " | " + status;
    }

    @Override
    public void viewDetails() {
        System.out.println("Order ID: " + orderID + "\nProduct: " + product.getProductName()
                + "\nQuantity: " + quantityOrdered + "\nSupplier: " + supplier.getSupplierName()
                + "\nDate: " + orderDate + "\nStatus: " + status);
    }
}