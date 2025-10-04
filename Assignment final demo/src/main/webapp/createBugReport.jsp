<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="bug" scope="request" class="nbcc.assignment2.entities.BugReport"/>

<!DOCTYPE html>
<html>
<head>
    <title>Create Bug Report</title>
</head>
<body>
<h1>Create Bug Report
</h1>
<br/>

<h2>Bug Report</h2>
<form action="createBug" method="post">
    <table>
        <tr>
            <td><label for="summary">Summary</label></td>
            <td><input type="text" id="summary" name="summary"/></td>
        </tr>
        <tr>
            <td><label for="description">Description:</label></td>
            <td>
                <textarea id="description" name="description"
                          rows="5" cols="50"></textarea>
            </td>
        </tr>
        <tr>
            <td><label for="costToFix">Cost To Fix</label></td>
            <td><input type="text" id="costToFix" name="costToFix"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" value="Submit"></td>
        </tr>
    </table>
</form>
<a href="bugs">Go Back</a>
</body>
</html>