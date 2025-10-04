package nbcc.assignment2.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nbcc.assignment2.dao.BugReportDAO;
import nbcc.assignment2.dao.BugReportRepo;
import nbcc.assignment2.dao.BugReportRepoFactory;
import nbcc.assignment2.entities.BugReport;
import nbcc.assignment2.entities.UserInfo;
import nbcc.assignment2.services.BugReportRequestService;
import nbcc.assignment2.services.UserInfoSessionService;

import java.io.IOException;

@WebServlet(name = "createBugsServlet", value = "/createBug")
public class CreateBugServlet extends HttpServlet {
    private String message;

    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("createBugReport.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        BugReportRequestService bugReportRequestService = new BugReportRequestService(request);
        BugReport bugReport = bugReportRequestService.getBugReport();

        UserInfoSessionService userInfoSessionService = new UserInfoSessionService(request);
        UserInfo user = userInfoSessionService.getLoggedInUser();
        bugReport.setUserInfo(user);

        BugReportRepo bugReportRepo = BugReportRepoFactory.getRepo();
        bugReportRepo.addBugReport(bugReport);

        response.sendRedirect("bugs");
    }

    public void destroy() {
    }
}