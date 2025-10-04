package nbcc.assignment2.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nbcc.assignment2.dao.LoginRepo;
import nbcc.assignment2.dao.LoginRepoFactory;
import nbcc.assignment2.entities.UserInfo;
import nbcc.assignment2.services.UserInfoSessionService;

import java.io.IOException;

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    private static final String MESSAGE_ATTRIBUTE_NAME = "message";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       request.setAttribute(MESSAGE_ATTRIBUTE_NAME, "Please login");
       request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = getUsername(request);
        String password = getPassword(request);

        LoginRepo loginRepo = LoginRepoFactory.getRepo();

        UserInfo userInfo = loginRepo.get(username, password);

        //Invalid Login
        if(userInfo == null) {
            request.setAttribute(MESSAGE_ATTRIBUTE_NAME, "Invalid Login");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        UserInfoSessionService userInfoSessionService = new UserInfoSessionService(request);
        userInfoSessionService.setLoggedInUser(userInfo);

        response.sendRedirect("bugs");
    }

    private String getUsername(HttpServletRequest request) {
        return request.getParameter("username");
    }

    private String getPassword(HttpServletRequest request) {
        return request.getParameter("password");
    }
}
