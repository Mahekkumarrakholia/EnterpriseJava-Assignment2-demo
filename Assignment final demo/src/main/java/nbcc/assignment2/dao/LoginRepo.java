package nbcc.assignment2.dao;

import nbcc.assignment2.entities.UserInfo;

public interface LoginRepo {

    void create(String username, String password);
    void create(UserInfo userInfo);
    UserInfo get(String username, String password);
    UserInfo get(long id);
    boolean exists(String username);

}
