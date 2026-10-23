public class Main {

    public static void main(String[] args) {
        StoreContext ctx = new StoreContext(10);   // order queue holds up to 10 waiting orders
        seedData(ctx);

        ConsoleUI.printBanner();
        while (true) {
            ConsoleUI.printBox("LOGIN", "Enter your username and password.\nType 'exit' as the username to quit.");
            String username = ConsoleInput.readString("  Username: ");
            if (username.equalsIgnoreCase("exit")) break;
            String password = ConsoleInput.readString("  Password: ");

            User user = ctx.getUserManager().authenticate(username, password);
            if (user == null) {
                ConsoleUI.error("Invalid username or password.");
                continue;
            }
            ConsoleUI.success("Welcome, " + user.getUsername() + "!");
            user.displayMenu();   // returns when the user logs out
        }
        ConsoleUI.info("Goodbye.");
    }

    /** Starter data so you can test immediately. Change or remove the default accounts before submitting. */
    private static void seedData(StoreContext ctx) {
        ctx.getInventory().addProduct(new Product("P001", "Notebook", 50, 35.00, 10));
        ctx.getInventory().addProduct(new Product("P002", "Ballpen", 8, 12.50, 15));   // starts low on stock
        ctx.getInventory().addProduct(new Product("P003", "Stapler", 20, 89.00, 5));

        ctx.getSuppliers().add(new Supplier("S001", "Metro Office Supply", "metro@example.com"));
        ctx.getSuppliers().add(new Supplier("S002", "Luzon Paper Co.", "luzonpaper@example.com"));

        ctx.getUserManager().addUser(new SystemAdmin("U001", "admin", "admin123", ctx));
        ctx.getUserManager().addUser(new InventoryClerk("U002", "clerk1", "clerk123", ctx));
        ctx.getUserManager().addUser(new StoreManager("U003", "manager1", "manager123", ctx));
    }
}