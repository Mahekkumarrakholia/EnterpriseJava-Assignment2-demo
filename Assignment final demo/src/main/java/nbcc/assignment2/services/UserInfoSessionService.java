package nbcc.assignment2.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nbcc.assignment2.dao.LoginRepo;
import nbcc.assignment2.dao.LoginRepoFactory;
import nbcc.assignment2.entities.UserInfo;

public class UserInfoSessionService {

    private static final String USER_ID_SESSION_KEY = "loggedInUserId";
    private static final String LOGGED_IN_USER_KEY = "loggedIn";

    private final HttpSession session;

    public UserInfoSessionService(HttpServletRequest request) {
        this(request.getSession());
    }

    public UserInfoSessionService(HttpSession session) {
        this.session = session;
    }


    public Long getLoggedInUserId(){
        return (Long) session.getAttribute(USER_ID_SESSION_KEY);
    }

    public UserInfo getLoggedInUser() {

        Long userId = getLoggedInUserId();

        if (userId == null) {
            return null;
        }

        LoginRepo loginRepo = LoginRepoFactory.getRepo();

        return loginRepo.get(userId);
    }

    public void setLoggedInUser(UserInfo userInfo){
        setLoggedInUserId(userInfo.getId());
    }

    public void setLoggedInUserId(long id){
        session.setAttribute(USER_ID_SESSION_KEY, id);
        session.setAttribute(LOGGED_IN_USER_KEY, "true");
    }

    public void removeLoggedInUserId(){
        session.removeAttribute(USER_ID_SESSION_KEY);
        session.removeAttribute(LOGGED_IN_USER_KEY);
    }
}
