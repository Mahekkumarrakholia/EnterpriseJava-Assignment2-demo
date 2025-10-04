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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class EditBugServletTest {

    private long nextBugId = 1L;
    private long nextUserId = 1L;
    private List<BugReport> inMemoryBugReports = new ArrayList<>();
    private List<UserInfo> inMemoryUsers = new ArrayList<>();

    @Test
    public void testDoGet_ForwardsToEditBugReportJsp() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RequestDispatcher mockDispatcher = mock(RequestDispatcher.class);

        when(mockRequest.getRequestDispatcher("editBugReport.jsp")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("id")).thenReturn("123");

        // Act
        EditBugServlet servlet = new EditBugServlet();
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
            EditBugServlet servlet = new EditBugServlet();
            servlet.doPost(mockRequest, mockResponse);

            // Assert
            verify(mockResponse).sendRedirect("bugs");
            verify(mockSetup.bugRepo).editBugReport(any(BugReport.class));
        }
    }

    @Test
    public void testDoPost_CallsEditBugReportWithCorrectValues() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // Set up request parameters (simulating form data)
        when(mockRequest.getParameter("id")).thenReturn("456");
        when(mockRequest.getParameter("summary")).thenReturn("Updated Bug Summary");
        when(mockRequest.getParameter("description")).thenReturn("Updated Bug Description");
        when(mockRequest.getParameter("costToFix")).thenReturn("299.50");

        // Use helper methods to set up mocks
        MockSetup mockSetup = setupMocksForLoggedInUser(mockRequest, 7L);

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {

            // Configure factories
            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            EditBugServlet servlet = new EditBugServlet();
            servlet.doPost(mockRequest, mockResponse);

            // Assert - Verify editBugReport was called
            ArgumentCaptor<BugReport> bugReportCaptor = ArgumentCaptor.forClass(BugReport.class);
            verify(mockSetup.bugRepo).editBugReport(bugReportCaptor.capture());

            // Verify the captured BugReport has the correct values
            BugReport capturedBugReport = bugReportCaptor.getValue();
            assertEquals(456L, capturedBugReport.getId(), "Bug ID should match request parameter");
            assertEquals("Updated Bug Summary", capturedBugReport.getSummary());
            assertEquals("Updated Bug Description", capturedBugReport.getDescription());
            assertEquals(new BigDecimal("299.50"), capturedBugReport.getCostToFix());
            assertNotNull(capturedBugReport.getUserInfo(), "UserInfo should not be null");
            assertEquals(7L, capturedBugReport.getUserInfo().getId());
            assertEquals("testuser", capturedBugReport.getUserInfo().getUsername());

            // Verify redirect still happens
            verify(mockResponse).sendRedirect("bugs");
        }
    }

    @Test
    public void testDoPost_EnsuresUserInfoIsSetOnBugReport() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // Set up request parameters for editing
        when(mockRequest.getParameter("id")).thenReturn("789");
        when(mockRequest.getParameter("summary")).thenReturn("Security Bug");
        when(mockRequest.getParameter("description")).thenReturn("SQL injection vulnerability");
        when(mockRequest.getParameter("costToFix")).thenReturn("1500.00");

        // Setup for a specific logged-in user
        long loggedInUserId = 42L;
        MockSetup mockSetup = setupMocksForLoggedInUser(mockRequest, loggedInUserId);

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {

            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            EditBugServlet servlet = new EditBugServlet();
            servlet.doPost(mockRequest, mockResponse);

            // Assert - Capture and verify the UserInfo is properly set
            ArgumentCaptor<BugReport> bugReportCaptor = ArgumentCaptor.forClass(BugReport.class);
            verify(mockSetup.bugRepo).editBugReport(bugReportCaptor.capture());

            BugReport capturedBugReport = bugReportCaptor.getValue();

            // Verify UserInfo is set and has correct data
            assertNotNull(capturedBugReport.getUserInfo(),
                    "UserInfo must be set on BugReport when editing (user must be logged in)");
            assertEquals(loggedInUserId, capturedBugReport.getUserInfo().getId(),
                    "UserInfo should have the ID of the logged-in user");
            assertEquals("testuser", capturedBugReport.getUserInfo().getUsername(),
                    "UserInfo should have the correct username from session");

            // Verify the UserInfo object comes from the login service (not null/empty)
            assertNotNull(capturedBugReport.getUserInfo().getUsername(),
                    "Username should not be null");
            assertTrue(capturedBugReport.getUserInfo().getUsername().length() > 0,
                    "Username should not be empty");

            // Verify this is the actual user object from our mock (to ensure proper lookup happened)
            UserInfo expectedUser = mockGetUserInfo(loggedInUserId);
            assertEquals(expectedUser.getId(), capturedBugReport.getUserInfo().getId(),
                    "Should use the user from UserInfoSessionService lookup");
            assertEquals(expectedUser.getUsername(), capturedBugReport.getUserInfo().getUsername(),
                    "Should use the user from UserInfoSessionService lookup");
        }
    }

    @Test
    public void testDoPost_WithDifferentLoggedInUser_SetsCorrectUserInfo() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // Set up request parameters
        when(mockRequest.getParameter("id")).thenReturn("999");
        when(mockRequest.getParameter("summary")).thenReturn("Performance Issue");
        when(mockRequest.getParameter("description")).thenReturn("Slow database queries");
        when(mockRequest.getParameter("costToFix")).thenReturn("750.25");

        // Setup for a different logged-in user
        long differentUserId = 88L;
        MockSetup mockSetup = setupMocksForLoggedInUser(mockRequest, differentUserId);

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {

            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            EditBugServlet servlet = new EditBugServlet();
            servlet.doPost(mockRequest, mockResponse);

            // Assert
            ArgumentCaptor<BugReport> bugReportCaptor = ArgumentCaptor.forClass(BugReport.class);
            verify(mockSetup.bugRepo).editBugReport(bugReportCaptor.capture());

            BugReport capturedBugReport = bugReportCaptor.getValue();

            // Verify the UserInfo matches the different logged-in user
            assertNotNull(capturedBugReport.getUserInfo());
            assertEquals(differentUserId, capturedBugReport.getUserInfo().getId(),
                    "Should set UserInfo to the currently logged-in user (ID: " + differentUserId + ")");
            assertEquals("testuser", capturedBugReport.getUserInfo().getUsername(),
                    "Should set UserInfo to the currently logged-in user");
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

    // Configure bug repository mock behavior
    private void setupBugRepoMocks(BugReportRepo mockBugRepo) {
        when(mockBugRepo.getBugReportList()).thenAnswer(invocation -> mockGetBugReportList());
        doAnswer(invocation -> {
            BugReport bug = invocation.getArgument(0);
            mockEditBugReport(bug);
            return null;
        }).when(mockBugRepo).editBugReport(any(BugReport.class));
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