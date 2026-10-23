import java.util.ArrayList;
import java.util.List;

/** Stores the accounts and handles authentication. */
public class UserManager {
    private List<User> users = new ArrayList<>();

    /** Returns false if the user ID or username is already taken. */
    public boolean addUser(User u) {
        if (u == null) return false;
        for (User existing : users) {
            if (existing.getUserID().equalsIgnoreCase(u.getUserID())
                    || existing.getUsername().equalsIgnoreCase(u.getUsername())) {
                return false;
            }
        }
        users.add(u);
        return true;
    }

    public boolean removeUser(String userID) {
        User u = findByID(userID);
        return u != null && users.remove(u);
    }

    public User findByID(String userID) {
        if (userID == null) return null;
        for (User u : users) {
            if (u.getUserID().equalsIgnoreCase(userID.trim())) return u;
        }
        return null;
    }

    /** Returns the logged-in user, or null if the credentials are wrong. */
    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.login(username, password)) return u;
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}