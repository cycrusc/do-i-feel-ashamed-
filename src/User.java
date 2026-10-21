import java.util.List;

public abstract class User implements ViewDetails {
    protected String userID;
    protected String username;
    private String password;
    protected String accessLevel;
    protected StoreContext ctx;
    private boolean loggedIn = false;

    protected User(String userID, String username, String password, String accessLevel, StoreContext ctx) {
        if (userID == null || userID.trim().isEmpty() || username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("User ID, username and password are required.");
        this.userID = userID.trim();
        this.username = username.trim();
        this.password = password;
        this.accessLevel = accessLevel;
        this.ctx = ctx;
    }

    public boolean login(String username, String password) {
        if (this.username.equalsIgnoreCase(username) && this.password.equals(password)) {
            loggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedIn = false;
    }

    public boolean isLoggedIn()        { return loggedIn; }
    public String getUserID()          { return userID; }
    public String getUsername()        { return username; }
    public String getAccessLevel()     { return accessLevel; }

    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty())
            throw new IllegalArgumentException("Password cannot be empty.");
        this.password = newPassword;
    }

    /** Runs this role's menu loop until the user logs out. */
    public abstract void displayMenu();

    @Override
    public void viewDetails() {   // never prints the password
        System.out.println("User ID: " + userID + " | Username: " + username + " | Role: " + accessLevel);
    }

    // ----- shared console helpers for the role menus -----
    // Every table row below is exactly 60 characters wide inside the border, to match the menus.

    protected void printProducts(String title, List<Product> products) {
        if (products.isEmpty()) {
            ConsoleUI.info("No products found.");
            return;
        }
        ConsoleUI.printTop(title);
        System.out.printf(ConsoleUI.V + " %-6s %-20s %5s %9s %7s %-6s " + ConsoleUI.V + "%n",
                "ID", "Name", "Qty", "Price", "Reorder", "Status");
        ConsoleUI.printDivider();
        for (Product p : products) {
            String status = "OK";
            if (p.isLowStock()) {
                status = "LOW";
            }
            System.out.printf(ConsoleUI.V + " %-6.6s %-20.20s %5d %9.2f %7d %-6s " + ConsoleUI.V + "%n",
                    p.getProductID(), p.getProductName(), p.getQuantity(), p.getPrice(),
                    p.getReorderLevel(), status);
        }
        ConsoleUI.printBottom();
    }

    protected void printPendingOrders() {
        CustomerOrder[] pending = ctx.getOrderQueue().getPendingOrders();
        if (pending.length == 0) {
            ConsoleUI.info("No pending customer orders.");
            return;
        }
        ConsoleUI.printTop("PENDING CUSTOMER ORDERS (FRONT FIRST)");
        System.out.printf(ConsoleUI.V + " %3s %-8s %-20s %-8s %5s %-9s " + ConsoleUI.V + "%n",
                "#", "Order ID", "Customer", "Product", "Qty", "Status");
        ConsoleUI.printDivider();
        for (int i = 0; i < pending.length; i++) {
            CustomerOrder o = pending[i];
            System.out.printf(ConsoleUI.V + " %3d %-8.8s %-20.20s %-8.8s %5d %-9.9s " + ConsoleUI.V + "%n",
                    i + 1, o.getOrderID(), o.getCustomerName(), o.getProductID(), o.getQuantity(), o.getStatus());
        }
        ConsoleUI.printBottom();
    }

    protected void printRestockOrders(String title, List<Order> orders) {
        ConsoleUI.printTop(title);
        System.out.printf(ConsoleUI.V + " %-8s %-12s %5s %-20s %-9s " + ConsoleUI.V + "%n",
                "Order ID", "Product", "Qty", "Supplier", "Status");
        ConsoleUI.printDivider();
        for (Order o : orders) {
            System.out.printf(ConsoleUI.V + " %-8.8s %-12.12s %5d %-20.20s %-9.9s " + ConsoleUI.V + "%n",
                    o.getOrderID(), o.getProduct().getProductName(), o.getQuantityOrdered(),
                    o.getSupplier().getSupplierName(), o.getStatus());
        }
        ConsoleUI.printBottom();
    }
}