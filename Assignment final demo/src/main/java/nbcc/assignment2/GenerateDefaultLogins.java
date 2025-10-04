package nbcc.assignment2;

import nbcc.assignment2.dao.LoginRepo;
import nbcc.assignment2.dao.LoginRepoFactory;
import nbcc.assignment2.entities.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class GenerateDefaultLogins {

    public static void main(String[] args) {
        List<UserInfo> defaultUsers = new ArrayList<UserInfo>();

        defaultUsers.add(new UserInfo("andre", "nbcc"));
        defaultUsers.add(new UserInfo("admin", "nbcc"));

        LoginRepo loginRepo = LoginRepoFactory.getRepo();

        for (UserInfo user : defaultUsers) {
            if (!loginRepo.exists(user.getUsername())) {
                loginRepo.create(user);
                System.out.println("User inserted: " + user.getUsername());
            } else {
                System.out.println("User already exists: " + user.getUsername());
            }
        }
    }
}

