public class Product implements ViewDetails {
    private final String productID;
    private String productName;
    private int quantity;
    private double price;
    private int reorderLevel;

    public Product(String productID, String productName, int quantity, double price, int reorderLevel) {
        if (productID == null || productID.isBlank())
            throw new IllegalArgumentException("Product ID is required.");
        if (productName == null || productName.isBlank())
            throw new IllegalArgumentException("Product name is required.");
        if (quantity < 0 || price < 0 || reorderLevel < 0)
            throw new IllegalArgumentException("Quantity, price and reorder level cannot be negative.");
        this.productID = productID.trim();
        this.productName = productName.trim();
        this.quantity = quantity;
        this.price = price;
        this.reorderLevel = reorderLevel;
    }

    // ----- getters / setters (the ID never changes) -----
    public String getProductID()   { return productID; }
    public String getProductName() { return productName; }
    public int getQuantity()       { return quantity; }
    public double getPrice()       { return price; }
    public int getReorderLevel()   { return reorderLevel; }

    public void setProductName(String productName) {
        if (productName == null || productName.isBlank())
            throw new IllegalArgumentException("Product name is required.");
        this.productName = productName.trim();
    }

    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }

    public void setReorderLevel(int reorderLevel) {
        if (reorderLevel < 0) throw new IllegalArgumentException("Reorder level cannot be negative.");
        this.reorderLevel = reorderLevel;
    }

    // ----- stock logic -----
    public void addStock(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Quantity to add must be positive.");
        this.quantity += qty;
    }

    /** Returns true if stock was reduced, false if qty is invalid or there is not enough stock. */
    public boolean reduceStock(int qty) {
        if (qty > 0 && this.quantity >= qty) {
            this.quantity -= qty;
            return true;
        }
        return false;
    }

    public boolean isLowStock() {
        return this.quantity <= this.reorderLevel;
    }

    public String getDetails() {
        return "ID: " + productID + "\nName: " + productName + "\nQuantity: " + quantity
                + "\nPrice: " + String.format("%.2f", price) + "\nReorder Level: " + reorderLevel;
    }

    @Override
    public void viewDetails() {
        System.out.println(getDetails());
    }
}