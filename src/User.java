public abstract class User {
    protected String userID;
    protected String userName;
    private String password;
    protected String accessLevel;

    public boolean login(String username, String password) {
        return false;
    }

    public void logout() {}
    public abstract void displayMenu();
}
