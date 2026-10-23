public class Product implements ViewDetails{
    private String productID;
    private String productName;
    private int quantity;
    private double price;
    private int reoderLevel;

    @Override
    public void viewDetails() {
        System.out.println(getDetails());
    }

    public String getDetails() {
        return "ID: " + productID + "\nName: " + productName + "\nQuantity: " + quantity + "\nPrice: " + price + "\nReoder Level: " + reoderLevel;
    }

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
        return this.quantity <= this.reoderLevel;
    }
}
