package majde.marek.bankscrapers.model;


public class UserCredentials {

    String username;
    String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
