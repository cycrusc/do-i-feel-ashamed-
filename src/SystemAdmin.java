public class SystemAdmin extends User {
    private static final String DEFAULT_RESET_PASSWORD = "Reset123!";

    public SystemAdmin(String userID, String username, String password, StoreContext ctx) {
        super(userID, username, password, AccessLevel.ADMIN, ctx);
    }

    // =============== core logic ===============

    public void createUserAccount(User u) {
        if (!ctx.getUserManager().addUser(u))
            throw new IllegalArgumentException("User ID or username already exists.");
    }

    public void removeUserAccount(String userID) {
        if (userID.trim().equalsIgnoreCase(this.userID))
            throw new IllegalArgumentException("You cannot remove your own account.");
        if (!ctx.getUserManager().removeUser(userID))
            throw new IllegalArgumentException("User not found: " + userID);
    }

    /** Sets the account's password to a default; the user should change it afterwards. Returns that default. */
    public String resetPassword(String userID) {
        User u = ctx.getUserManager().findByID(userID);
        if (u == null) throw new IllegalArgumentException("User not found: " + userID);
        u.setPassword(DEFAULT_RESET_PASSWORD);
        return DEFAULT_RESET_PASSWORD;
    }

    // =============== menu ===============

    @Override
    public void displayMenu() {
        while (isLoggedIn()) {
            System.out.println("\n=== System Admin Menu (" + username + ") ===");
            System.out.println("1. Create user account");
            System.out.println("2. Remove user account");
            System.out.println("3. Reset a user's password");
            System.out.println("4. List users");
            System.out.println("0. Logout");
            int choice = ConsoleInput.readInt("Choose: ", 0);

            try {
                switch (choice) {
                    case 1: createUserFlow(); break;
                    case 2:
                        removeUserAccount(ConsoleInput.readString("User ID to remove: "));
                        System.out.println("Account removed.");
                        break;
                    case 3:
                        String temp = resetPassword(ConsoleInput.readString("User ID: "));
                        System.out.println("Password reset. Temporary password: " + temp);
                        break;
                    case 4:
                        for (User u : ctx.getUserManager().getAllUsers()) u.viewDetails();
                        break;
                    case 0: logout(); System.out.println("Logged out."); break;
                    default: System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void createUserFlow() {
        System.out.println("Role: 1. Inventory Clerk  2. Store Manager  3. System Admin");
        int role = ConsoleInput.readInt("Choose role: ", 1);
        String id = ConsoleInput.readString("User ID: ");
        String name = ConsoleInput.readString("Username: ");
        String pass = ConsoleInput.readString("Password: ");

        User u;
        switch (role) {
            case 1: u = new InventoryClerk(id, name, pass, ctx); break;
            case 2: u = new StoreManager(id, name, pass, ctx); break;
            case 3: u = new SystemAdmin(id, name, pass, ctx); break;
            default: System.out.println("Invalid role."); return;
        }
        createUserAccount(u);
        System.out.println("Account created.");
    }
}