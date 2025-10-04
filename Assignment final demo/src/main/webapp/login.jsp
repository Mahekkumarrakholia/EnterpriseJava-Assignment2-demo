<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="message" scope="request" class="java.lang.String"/>
<jsp:useBean id="username" scope="request" class="java.lang.String"/>
<html>
<head>
    <title>Title</title>
</head>
<body>
<p>${message}</p>
<form action="login" method="post">
    <label>Username: <input type="text" name="username" value=<c:out value="${username}" />></label>
    <label>Password: <input type="password" name="password"></label>
    <input type="submit" value="Log In">
</form>
</body>
</html>
