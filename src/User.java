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
    protected void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("(no products)");
            return;
        }
        System.out.printf("%-8s %-22s %6s %10s %8s%n", "ID", "Name", "Qty", "Price", "Reorder");
        for (Product p : products) {
            System.out.printf("%-8s %-22s %6d %10.2f %8d %s%n", p.getProductID(), p.getProductName(),
                    p.getQuantity(), p.getPrice(), p.getReorderLevel(), p.isLowStock() ? "<- LOW" : "");
        }
    }

    protected void printPendingOrders() {
        CustomerOrder[] pending = ctx.getOrderQueue().getPendingOrders();
        if (pending.length == 0) {
            System.out.println("No pending customer orders.");
            return;
        }
        System.out.println("Pending customer orders (front of queue first):");
        for (int i = 0; i < pending.length; i++) {
            System.out.println((i + 1) + ". " + pending[i].getSummary());
        }
    }
}