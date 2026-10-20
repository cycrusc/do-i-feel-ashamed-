import java.util.ArrayList;
import java.util.List;

public abstract class User implements ViewDetails {
    protected final String userID;
    protected final String username;
    private String password;
    protected final String accessLevel;
    protected final StoreContext ctx;
    private boolean loggedIn = false;

    protected User(String userID, String username, String password, String accessLevel, StoreContext ctx) {
        if (userID == null || userID.isBlank() || username == null || username.isBlank()
                || password == null || password.isBlank())
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

    public boolean isLoggedIn()           { return loggedIn; }
    public String getUserID()             { return userID; }
    public String getUsername()           { return username; }
    public String getAccessLevel()        { return accessLevel; }

    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank())
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
    protected void printProducts(String title, List<Product> products) {
        if (products.isEmpty()) {
            ConsoleUI.info("No products found.");
            return;
        }
        List<String[]> rows = new ArrayList<>();
        for (Product p : products) {
            rows.add(new String[] { p.getProductID(), p.getProductName(), String.valueOf(p.getQuantity()),
                    String.format("%.2f", p.getPrice()), String.valueOf(p.getReorderLevel()),
                    p.isLowStock() ? "LOW" : "OK" });
        }
        ConsoleUI.printTable(title,
                new String[] { "ID", "Name", "Qty", "Price", "Reorder", "Status" },
                new int[] { 6, 20, 5, 9, 7, 6 },
                new boolean[] { false, false, true, true, true, false }, rows);
    }

    protected void printPendingOrders() {
        CustomerOrder[] pending = ctx.getOrderQueue().getPendingOrders();
        if (pending.length == 0) {
            ConsoleUI.info("No pending customer orders.");
            return;
        }
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < pending.length; i++) {
            CustomerOrder o = pending[i];
            rows.add(new String[] { String.valueOf(i + 1), o.getOrderID(), o.getCustomerName(),
                    o.getProductID(), String.valueOf(o.getQuantity()), o.getStatus() });
        }
        ConsoleUI.printTable("PENDING CUSTOMER ORDERS  (front of queue first)",
                new String[] { "#", "Order ID", "Customer", "Product", "Qty", "Status" },
                new int[] { 3, 8, 18, 8, 5, 9 },
                new boolean[] { true, false, false, false, true, false }, rows);
    }

    protected void printRestockOrders(String title, List<Order> orders) {
        List<String[]> rows = new ArrayList<>();
        for (Order o : orders) {
            rows.add(new String[] { o.getOrderID(), o.getProduct().getProductName(),
                    String.valueOf(o.getQuantityOrdered()), o.getSupplier().getSupplierName(), o.getStatus() });
        }
        ConsoleUI.printTable(title,
                new String[] { "Order ID", "Product", "Qty", "Supplier", "Status" },
                new int[] { 8, 18, 5, 22, 9 },
                new boolean[] { false, false, true, false, false }, rows);
    }
}