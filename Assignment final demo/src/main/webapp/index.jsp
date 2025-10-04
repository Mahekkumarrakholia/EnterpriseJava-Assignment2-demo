<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="viewModel" scope="request" class="nbcc.assignment2.viewmodels.BugListViewModel"/>

<!DOCTYPE html>
<html>
<head>
    <title>JSP - Bug Reports!</title>
</head>
<body>
<h1>Bug Reports</h1>
<br/>

<c:choose>
    <c:when test="${viewModel.isLoggedIn()}">
        Hello ${viewModel.userInfo.username}
        <form action="logout" method="post">
            <input type="submit" value="Logout">
        </form>
    </c:when>
    <c:otherwise>
        <a href="login">Login</a>
    </c:otherwise>
</c:choose>

<br/>

<c:if test="${viewModel.showCreateLink()}">
    <a href="createBug">Create Bug Report</a>
    <br/>
</c:if>

<h2>All Bug Reports</h2>

<c:choose>
    <c:when test="${empty viewModel.bugReports}">
        <p>No Bug Reports found in the database.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr>
                <th>Id</th>
                <th>Summary</th>
                <th>Description</th>
                <th>Cost to Fix</th>
                <th>Created By</th>
                <c:if test="${viewModel.showEditLink()}">
                    <th>Manage</th>
                </c:if>
            </tr>

            <c:forEach var="bug" items="${viewModel.bugReports}">
                <tr>
                    <td>${bug.id}</td>
                    <td><c:out value="${bug.summary}"/> </td>
                    <td><c:out value="${bug.description}"/> </td>
                    <td><fmt:formatNumber value="${bug.costToFix}" type="currency"/> </td>
                    <td><c:out value="${bug.userInfo.username}"/> </td>
                    <c:if test="${viewModel.showEditLink()}">
                        <td>
                            <%--Bonus Question--%>
                            <c:if test="${viewModel.userEditBugReport(bug)}">
                                <a href="editBug?id=${bug.id}">Edit</a>
                            </c:if>
                        </td>
                    </c:if>

                </tr>
            </c:forEach>
        </table>

        <p>Total Cost: <fmt:formatNumber value="${viewModel.totalCost}" type="currency" /></p>
    </c:otherwise>
</c:choose>


</body>
</html>
