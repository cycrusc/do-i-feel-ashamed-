public class SystemAdmin extends User implements Report {

    public void displayMenu(){}
    public String report(String report){
        return report;
    }
    public void createUserAccount(User user){}
    public void removeUserAccount(String userID){}
    public void resetPassword(String UserID){}
}
