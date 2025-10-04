package nbcc.assignment2.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nbcc.assignment2.dao.BugReportRepo;
import nbcc.assignment2.dao.BugReportRepoFactory;
import nbcc.assignment2.dao.LoginRepo;
import nbcc.assignment2.dao.LoginRepoFactory;
import nbcc.assignment2.entities.BugReport;
import nbcc.assignment2.entities.UserInfo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateBugServletTest {

    private long nextBugId = 1L;
    private long nextUserId = 1L;
    private List<BugReport> inMemoryBugReports = new ArrayList<>();
    private List<UserInfo> inMemoryUsers = new ArrayList<>();

    @Test
    public void testDoGet_ForwardsToCreateBugReportJsp() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RequestDispatcher mockDispatcher = mock(RequestDispatcher.class);

        when(mockRequest.getRequestDispatcher("createBugReport.jsp")).thenReturn(mockDispatcher);

        // Act
        CreateBugServlet servlet = new CreateBugServlet();
        servlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    public void testDoPost_RedirectsToBugs() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        
        // Use helper methods to set up mocks
        MockSetup mockSetup = setupMocksForLoggedInUser(mockRequest, 1L);

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {
            
            // Configure factories
            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            CreateBugServlet servlet = new CreateBugServlet();
            servlet.doPost(mockRequest, mockResponse);

            // Assert
            verify(mockResponse).sendRedirect("bugs");
            verify(mockSetup.bugRepo).addBugReport(any(BugReport.class));
        }
    }

    @Test
    public void testDoPost_CallsAddBugReportWithCorrectValues() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // Set up request parameters (simulating form data)
        when(mockRequest.getParameter("summary")).thenReturn("Test Bug Summary");
        when(mockRequest.getParameter("description")).thenReturn("Test Bug Description");
        when(mockRequest.getParameter("costToFix")).thenReturn("199.99");

        // Use helper methods to set up mocks
        MockSetup mockSetup = setupMocksForLoggedInUser(mockRequest, 1L);

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {

            // Configure factories
            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            CreateBugServlet servlet = new CreateBugServlet();
            servlet.doPost(mockRequest, mockResponse);

            // Assert - Verify addBugReport was called
            ArgumentCaptor<BugReport> bugReportCaptor = ArgumentCaptor.forClass(BugReport.class);
            verify(mockSetup.bugRepo).addBugReport(bugReportCaptor.capture());

            // Verify the captured BugReport has the correct values
            BugReport capturedBugReport = bugReportCaptor.getValue();
            assertEquals("Test Bug Summary", capturedBugReport.getSummary());
            assertEquals("Test Bug Description", capturedBugReport.getDescription());
            assertEquals(new BigDecimal("199.99"), capturedBugReport.getCostToFix());
            assertNotNull(capturedBugReport.getUserInfo());
            assertEquals(1L, capturedBugReport.getUserInfo().getId());
            assertEquals("testuser", capturedBugReport.getUserInfo().getUsername());

            // Verify redirect still happens
            verify(mockResponse).sendRedirect("bugs");
        }
    }

    // Helper class to hold mock objects
    private static class MockSetup {
        HttpSession session;
        BugReportRepo bugRepo;
        LoginRepo loginRepo;
    }

    // Reusable method to set up mocks for a logged-in user
    private MockSetup setupMocksForLoggedInUser(HttpServletRequest mockRequest, long userId) {
        MockSetup setup = new MockSetup();
        
        // Set up session
        setup.session = mock(HttpSession.class);
        when(mockRequest.getSession()).thenReturn(setup.session);
        when(setup.session.getAttribute("loggedInUserId")).thenReturn(userId);
        
        // Set up repositories
        setup.bugRepo = mock(BugReportRepo.class);
        setup.loginRepo = mock(LoginRepo.class);
        
        // Configure repo behaviors
        setupBugRepoMocks(setup.bugRepo);
        setupLoginRepoMocks(setup.loginRepo);
        
        return setup;
    }

    // Reusable method to set up mocks for anonymous user (no session)
    private MockSetup setupMocksForAnonymousUser(HttpServletRequest mockRequest) {
        MockSetup setup = new MockSetup();
        
        // Set up session with no logged-in user
        setup.session = mock(HttpSession.class);
        when(mockRequest.getSession()).thenReturn(setup.session);
        when(setup.session.getAttribute("loggedInUserId")).thenReturn(null);
        
        // Set up repositories
        setup.bugRepo = mock(BugReportRepo.class);
        setup.loginRepo = mock(LoginRepo.class);
        
        // Configure repo behaviors
        setupBugRepoMocks(setup.bugRepo);
        setupLoginRepoMocks(setup.loginRepo);
        
        return setup;
    }

    // Configure bug repository mock behavior
    private void setupBugRepoMocks(BugReportRepo mockBugRepo) {
        when(mockBugRepo.getBugReportList()).thenAnswer(invocation -> mockGetBugReportList());
        doAnswer(invocation -> {
            BugReport bug = invocation.getArgument(0);
            mockAddBugReport(bug);
            return null;
        }).when(mockBugRepo).addBugReport(any(BugReport.class));
    }

    // Configure login repository mock behavior
    private void setupLoginRepoMocks(LoginRepo mockLoginRepo) {
        when(mockLoginRepo.get(anyLong())).thenAnswer(invocation -> {
            long userId = invocation.getArgument(0);
            return mockGetUserInfo(userId);
        });
    }

    // Configure mock factories
    private void configureMockFactories(MockedStatic<BugReportRepoFactory> mockedBugFactory,
                                      MockedStatic<LoginRepoFactory> mockedLoginFactory,
                                      MockSetup mockSetup) {
        mockedBugFactory.when(BugReportRepoFactory::getRepo).thenReturn(mockSetup.bugRepo);
        mockedLoginFactory.when(LoginRepoFactory::getRepo).thenReturn(mockSetup.loginRepo);
    }

    // Mock methods that simulate database operations
    private List<BugReport> mockGetBugReportList() {
        return new ArrayList<>(inMemoryBugReports);
    }

    private void mockAddBugReport(BugReport bugReport) {
        bugReport.setId(nextBugId++);
        inMemoryBugReports.add(bugReport);
        System.out.println("Mock: Added bug report with ID: " + bugReport.getId());
    }

    private void mockEditBugReport(BugReport bugReport) {
        for (int i = 0; i < inMemoryBugReports.size(); i++) {
            if (inMemoryBugReports.get(i).getId() == bugReport.getId()) {
                inMemoryBugReports.set(i, bugReport);
                System.out.println("Mock: Updated bug report with ID: " + bugReport.getId());
                return;
            }
        }
    }

    private UserInfo mockGetUserInfo(long userId) {
        for (UserInfo user : inMemoryUsers) {
            if (user.getId() == userId) {
                return user;
            }
        }
        UserInfo mockUser = new UserInfo("testuser", "password");
        mockUser.setId(userId);
        inMemoryUsers.add(mockUser);
        return mockUser;
    }
}
