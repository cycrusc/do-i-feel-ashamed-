import java.util.ArrayList;
import java.util.List;

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
        String[] options = { "Create user account", "Remove user account", "Reset a user's password", "List users" };
        while (isLoggedIn()) {
            ConsoleUI.printMenu("SYSTEM ADMIN  -  " + username, options);
            int choice = ConsoleInput.readInt("  Select an option: ", 0);

            try {
                switch (choice) {
                    case 1: createUserFlow(); break;
                    case 2:
                        removeUserAccount(ConsoleInput.readString("  User ID to remove: "));
                        ConsoleUI.success("Account removed.");
                        break;
                    case 3:
                        String temp = resetPassword(ConsoleInput.readString("  User ID: "));
                        ConsoleUI.success("Password reset. Temporary password: " + temp);
                        break;
                    case 4: listUsers(); break;
                    case 0: logout(); ConsoleUI.info("Logged out."); break;
                    default: ConsoleUI.error("Invalid option.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                ConsoleUI.error(e.getMessage());
            }
        }
    }

    private void listUsers() {
        List<String[]> rows = new ArrayList<>();
        for (User u : ctx.getUserManager().getAllUsers()) {
            rows.add(new String[] { u.getUserID(), u.getUsername(), u.getAccessLevel() });
        }
        ConsoleUI.printTable("USER ACCOUNTS", new String[] { "User ID", "Username", "Role" },
                new int[] { 8, 18, 10 }, null, rows);
    }

    private void createUserFlow() {
        ConsoleUI.printBox("SELECT ROLE", "[1] Inventory Clerk\n[2] Store Manager\n[3] System Admin");
        int role = ConsoleInput.readInt("  Choose role: ", 1);
        String id = ConsoleInput.readString("  User ID: ");
        String name = ConsoleInput.readString("  Username: ");
        String pass = ConsoleInput.readString("  Password: ");

        User u;
        switch (role) {
            case 1: u = new InventoryClerk(id, name, pass, ctx); break;
            case 2: u = new StoreManager(id, name, pass, ctx); break;
            case 3: u = new SystemAdmin(id, name, pass, ctx); break;
            default: ConsoleUI.error("Invalid role."); return;
        }
        createUserAccount(u);
        ConsoleUI.success("Account created.");
    }
}