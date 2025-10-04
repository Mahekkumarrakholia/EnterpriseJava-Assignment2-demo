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
import nbcc.assignment2.viewmodels.BugListViewModel;
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

public class BugListServletTest {

    private long nextBugId = 1L;
    private long nextUserId = 1L;
    private List<BugReport> inMemoryBugReports = new ArrayList<>();
    private List<UserInfo> inMemoryUsers = new ArrayList<>();

    @Test
    public void testDoGet_ForwardsToIndexJsp() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RequestDispatcher mockDispatcher = mock(RequestDispatcher.class);

        when(mockRequest.getRequestDispatcher("index.jsp")).thenReturn(mockDispatcher);

        // Use helper method for anonymous user setup
        MockSetup mockSetup = setupMocksForAnonymousUser(mockRequest);

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {

            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            BugListServlet servlet = new BugListServlet();
            servlet.doGet(mockRequest, mockResponse);

            // Assert
            verify(mockDispatcher).forward(mockRequest, mockResponse);
            verify(mockRequest).setAttribute(eq("viewModel"), any(BugListViewModel.class));
        }
    }

    @Test
    public void testDoGet_WithNoUserSession_ViewModelHasNullUser() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RequestDispatcher mockDispatcher = mock(RequestDispatcher.class);

        when(mockRequest.getRequestDispatcher("index.jsp")).thenReturn(mockDispatcher);

        // Setup with anonymous user (no session user ID)
        MockSetup mockSetup = setupMocksForAnonymousUser(mockRequest);

        // Add some bug reports to test
        addSampleBugReports();

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {

            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            BugListServlet servlet = new BugListServlet();
            servlet.doGet(mockRequest, mockResponse);

            // Assert
            ArgumentCaptor<BugListViewModel> viewModelCaptor = ArgumentCaptor.forClass(BugListViewModel.class);
            verify(mockRequest).setAttribute(eq("viewModel"), viewModelCaptor.capture());

            BugListViewModel capturedViewModel = viewModelCaptor.getValue();
            assertNull(capturedViewModel.getUserInfo(), "User should be null when no session user ID");
            assertFalse(capturedViewModel.isLoggedIn(), "Should not be logged in");
            assertFalse(capturedViewModel.showCreateLink(), "Should not show create link");
            assertFalse(capturedViewModel.showEditLink(), "Should not show edit link");
            assertNotNull(capturedViewModel.getBugReports(), "Bug reports should not be null");
            assertEquals(2, capturedViewModel.getBugReports().size(), "Should have sample bug reports");
        }
    }

    @Test
    public void testDoGet_WithUserInSession_ViewModelHasCorrectUser() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RequestDispatcher mockDispatcher = mock(RequestDispatcher.class);

        when(mockRequest.getRequestDispatcher("index.jsp")).thenReturn(mockDispatcher);

        // Setup with logged-in user
        MockSetup mockSetup = setupMocksForLoggedInUser(mockRequest, 5L);

        // Add some bug reports to test
        addSampleBugReports();

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {

            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            BugListServlet servlet = new BugListServlet();
            servlet.doGet(mockRequest, mockResponse);

            // Assert
            ArgumentCaptor<BugListViewModel> viewModelCaptor = ArgumentCaptor.forClass(BugListViewModel.class);
            verify(mockRequest).setAttribute(eq("viewModel"), viewModelCaptor.capture());

            BugListViewModel capturedViewModel = viewModelCaptor.getValue();
            assertNotNull(capturedViewModel.getUserInfo(), "User should not be null when logged in");
            assertEquals(5L, capturedViewModel.getUserInfo().getId(), "Should have correct user ID");
            assertEquals("testuser", capturedViewModel.getUserInfo().getUsername(), "Should have correct username");
            assertTrue(capturedViewModel.isLoggedIn(), "Should be logged in");
            assertTrue(capturedViewModel.showCreateLink(), "Should show create link when logged in");
            assertTrue(capturedViewModel.showEditLink(), "Should show edit link when logged in");
            assertNotNull(capturedViewModel.getBugReports(), "Bug reports should not be null");
            assertEquals(2, capturedViewModel.getBugReports().size(), "Should have sample bug reports");
        }
    }

    @Test
    public void testDoGet_ViewModelContainsExactBugReportsFromRepo() throws ServletException, IOException {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RequestDispatcher mockDispatcher = mock(RequestDispatcher.class);

        when(mockRequest.getRequestDispatcher("index.jsp")).thenReturn(mockDispatcher);

        // Setup with anonymous user (focus is on bug reports, not user)
        MockSetup mockSetup = setupMocksForAnonymousUser(mockRequest);

        // Create specific bug reports that the repo should return
        UserInfo user1 = new UserInfo("alice", "password123");
        user1.setId(10L);

        UserInfo user2 = new UserInfo("bob", "password456");
        user2.setId(20L);

        BugReport expectedBug1 = new BugReport("Critical Bug", "System crashes on startup", user1);
        expectedBug1.setId(100L);
        expectedBug1.setCostToFix(new BigDecimal("500.75"));

        BugReport expectedBug2 = new BugReport("UI Issue", "Button is misaligned", user2);
        expectedBug2.setId(200L);
        expectedBug2.setCostToFix(new BigDecimal("25.50"));

        BugReport expectedBug3 = new BugReport("Performance Bug", "Page loads slowly", user1);
        expectedBug3.setId(300L);
        expectedBug3.setCostToFix(new BigDecimal("150.00"));

        // Clear any existing data and add our specific test data
        inMemoryBugReports.clear();
        inMemoryUsers.clear();
        inMemoryBugReports.add(expectedBug1);
        inMemoryBugReports.add(expectedBug2);
        inMemoryBugReports.add(expectedBug3);
        inMemoryUsers.add(user1);
        inMemoryUsers.add(user2);

        try (MockedStatic<BugReportRepoFactory> mockedBugFactory = mockStatic(BugReportRepoFactory.class);
             MockedStatic<LoginRepoFactory> mockedLoginFactory = mockStatic(LoginRepoFactory.class)) {

            configureMockFactories(mockedBugFactory, mockedLoginFactory, mockSetup);

            // Act
            BugListServlet servlet = new BugListServlet();
            servlet.doGet(mockRequest, mockResponse);

            // Assert - Capture the viewModel
            ArgumentCaptor<BugListViewModel> viewModelCaptor = ArgumentCaptor.forClass(BugListViewModel.class);
            verify(mockRequest).setAttribute(eq("viewModel"), viewModelCaptor.capture());

            BugListViewModel capturedViewModel = viewModelCaptor.getValue();
            List<BugReport> actualBugReports = capturedViewModel.getBugReports();

            // Verify the exact same bug reports are in the viewModel
            assertNotNull(actualBugReports, "Bug reports should not be null");
            assertEquals(3, actualBugReports.size(), "Should have exactly 3 bug reports");

            // Verify first bug report
            BugReport actualBug1 = actualBugReports.get(0);
            assertEquals(100L, actualBug1.getId(), "First bug ID should match");
            assertEquals("Critical Bug", actualBug1.getSummary(), "First bug summary should match");
            assertEquals("System crashes on startup", actualBug1.getDescription(), "First bug description should match");
            assertEquals(new BigDecimal("500.75"), actualBug1.getCostToFix(), "First bug cost should match");
            assertEquals(10L, actualBug1.getUserInfo().getId(), "First bug user ID should match");
            assertEquals("alice", actualBug1.getUserInfo().getUsername(), "First bug username should match");

            // Verify second bug report
            BugReport actualBug2 = actualBugReports.get(1);
            assertEquals(200L, actualBug2.getId(), "Second bug ID should match");
            assertEquals("UI Issue", actualBug2.getSummary(), "Second bug summary should match");
            assertEquals("Button is misaligned", actualBug2.getDescription(), "Second bug description should match");
            assertEquals(new BigDecimal("25.50"), actualBug2.getCostToFix(), "Second bug cost should match");
            assertEquals(20L, actualBug2.getUserInfo().getId(), "Second bug user ID should match");
            assertEquals("bob", actualBug2.getUserInfo().getUsername(), "Second bug username should match");

            // Verify third bug report
            BugReport actualBug3 = actualBugReports.get(2);
            assertEquals(300L, actualBug3.getId(), "Third bug ID should match");
            assertEquals("Performance Bug", actualBug3.getSummary(), "Third bug summary should match");
            assertEquals("Page loads slowly", actualBug3.getDescription(), "Third bug description should match");
            assertEquals(new BigDecimal("150.00"), actualBug3.getCostToFix(), "Third bug cost should match");
            assertEquals(10L, actualBug3.getUserInfo().getId(), "Third bug user ID should match");
            assertEquals("alice", actualBug3.getUserInfo().getUsername(), "Third bug username should match");

            // Verify that these are the exact same objects (reference equality)
            assertSame(expectedBug1, actualBug1, "First bug should be the exact same object from repo");
            assertSame(expectedBug2, actualBug2, "Second bug should be the exact same object from repo");
            assertSame(expectedBug3, actualBug3, "Third bug should be the exact same object from repo");
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

    // Helper method to add sample bug reports for testing
    private void addSampleBugReports() {
        UserInfo user1 = new UserInfo("john", "password");
        user1.setId(1L);

        UserInfo user2 = new UserInfo("jane", "password");
        user2.setId(2L);

        BugReport bug1 = new BugReport("Bug 1", "Description 1", user1);
        bug1.setId(1L);
        bug1.setCostToFix(new BigDecimal("100.00"));

        BugReport bug2 = new BugReport("Bug 2", "Description 2", user2);
        bug2.setId(2L);
        bug2.setCostToFix(new BigDecimal("200.00"));

        inMemoryBugReports.add(bug1);
        inMemoryBugReports.add(bug2);
        inMemoryUsers.add(user1);
        inMemoryUsers.add(user2);
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