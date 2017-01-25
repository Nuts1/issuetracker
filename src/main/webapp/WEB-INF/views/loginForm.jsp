<%--
  Created by IntelliJ IDEA.
  User: Nuts
  Date: 1/14/2017
  Time: 2:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page session="true"%>

<c:url value="/login" var="loginUrl"/>
<html lang="en">
<head>
    <title>Issue tracker</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<style>
    .login-container {
        position: relative;
        top: 50%;
        width: 100%;
    }
</style>
<body>
<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar" id="myNavbar">
            <ul class="nav navbar-nav navbar-right">
                <li><a href="#"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="login-container" style="width: 500px">
    <form action="${loginUrl}" method="post">
        <c:if test="${param.error != null}">
            <p>
                Invalid username and password.
            </p>
        </c:if>
        <c:if test="${param.logout != null}">
            <p>
                You have been logged out.
            </p>
        </c:if>
        <div class="input-group">
        <label for="email">Username</label>
            <input type="text" id="email" name="email"/>
        </div>
        <div class="input-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password"/>
        </div>
        <input type="hidden"
               name="${_csrf.parameterName}"
               value="${_csrf.token}"/>
        <button type="submit" class="btn">Log in</button>
    </form>
</div>

</body>
</html>
