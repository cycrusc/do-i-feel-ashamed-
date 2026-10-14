public class Product implements ViewDetails{
    private String productID;
    private String productName;
    private int quantity;
    private double price;
    private int reorderLevel;

    @Override
    public void viewDetails() {
        System.out.println("ID: " + productID + "\nName: " + productName + "\nQuantity: " + quantity + "\nPrice: " + price + "\nReorder Level: " + reorderLevel);
    }

//    public String getDetails() {
//        return "ID: " + productID + "\nName: " + productName + "\nQuantity: " + quantity + "\nPrice: " + price + "\nReorder Level: " + reorderLevel;
//    }

    public void addStock(int quantity) {
        this.quantity += quantity;
    }

    public void reduceStock(int quantity) {
        if (quantity > 0 && this.quantity >= quantity) {
            this.quantity -= quantity;
        }
        else{
            System.out.println("---Generic message---");
        }
    }

    public boolean isLowStock() {
        return this.quantity <= this.reorderLevel;
    }
}
