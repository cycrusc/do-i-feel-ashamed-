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

        // PERFUME PRODUCTS - IN THE MOOD (₱220)
        ctx.getInventory().addProduct(
                new Product("P001", "In The Mood - Lilac Fantasy", 20, 220.00, 5));
        ctx.getInventory().addProduct(
                new Product("P002", "In The Mood - Diamond Blush", 20, 220.00, 5));
        ctx.getInventory().addProduct(
                new Product("P003", "In The Mood - Arctic Aqua", 20, 220.00, 5));

        // PERFUME PRODUCTS - SOF & MMMM (₱285)
        ctx.getInventory().addProduct(
                new Product("P004", "Sof & Mmmm - Eternity", 20, 285.00, 5));
        ctx.getInventory().addProduct(
                new Product("P005", "Sof & Mmmm - Evermore", 20, 285.00, 5));
        ctx.getInventory().addProduct(
                new Product("P006", "Sof & Mmmm - All Time Fresh", 20, 285.00, 5));
        ctx.getInventory().addProduct(
                new Product("P007", "Sof & Mmmm - All Day Radiance", 20, 285.00, 5));
        ctx.getInventory().addProduct(
                new Product("P008", "Sof & Mmmm - Ever After", 20, 285.00, 5));
        ctx.getInventory().addProduct(
                new Product("P009", "Sof & Mmmm - Serenity", 20, 285.00, 5));
        ctx.getInventory().addProduct(
                new Product("P010", "Sof & Mmmm - Naturals", 20, 285.00, 5));

        // SUPPLIERS
        ctx.getSuppliers().add(
                new Supplier("S001", "3M Compound", "3mcompound@example.com"));
        ctx.getSuppliers().add(
                new Supplier("S002", "2B Compound", "2bcompound@example.com"));

        // USER ACCOUNTS
        ctx.getUserManager().addUser(
                new SystemAdmin("U001", "admin", "admin123", ctx));
        ctx.getUserManager().addUser(
                new InventoryClerk("U002", "clerk1", "clerk123", ctx));
        ctx.getUserManager().addUser(
                new StoreManager("U003", "manager1", "manager123", ctx));
    }

}