package nbcc.assignment2.dao;

public class LoginRepoFactory {
    public static LoginRepo getRepo(){
        return new LoginDAO();
    }
}
