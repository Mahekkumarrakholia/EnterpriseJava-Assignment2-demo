package nbcc.assignment2.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nbcc.assignment2.dao.BugReportRepo;
import nbcc.assignment2.dao.BugReportRepoFactory;
import nbcc.assignment2.entities.BugReport;
import nbcc.assignment2.services.UserInfoSessionService;
import nbcc.assignment2.viewmodels.BugListViewModel;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "bugsServlet", value = {"/bugs"})
public class BugListServlet extends HttpServlet {
    private String message;

    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        BugReportRepo bugReportRepo = BugReportRepoFactory.getRepo();

        List<BugReport> bugReportList = bugReportRepo.getBugReportList();
        BugListViewModel viewModel = new BugListViewModel(bugReportList);

        UserInfoSessionService userInfoSessionService = new UserInfoSessionService(request);
        viewModel.setUserInfo(userInfoSessionService.getLoggedInUser());

        request.setAttribute("viewModel", viewModel);

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    public void destroy() {
    }
}