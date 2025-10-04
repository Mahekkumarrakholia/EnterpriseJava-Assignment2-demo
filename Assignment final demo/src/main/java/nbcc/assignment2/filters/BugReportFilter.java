package nbcc.assignment2.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nbcc.assignment2.entities.UserInfo;
import nbcc.assignment2.services.UserInfoSessionService;

import java.io.IOException;

//Todo turn this into a proper filter for creates, edits and deletes

@WebFilter({"/createBug", "/editBug"})
public class BugReportFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest request){
            if (servletResponse instanceof HttpServletResponse response){
                UserInfoSessionService userInfoSessionService = new UserInfoSessionService(request);
                var userInfo = userInfoSessionService.getLoggedInUser();

                if (userInfo == null){
                    var contextPath = request.getContextPath();
                    var loginUrl = contextPath + "/login";
                    response.sendRedirect(loginUrl);
                    return;
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse); //continue with the request, don't stop it.
    }
}
