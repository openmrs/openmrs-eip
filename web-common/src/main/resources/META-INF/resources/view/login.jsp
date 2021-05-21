<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title>Log In</title>
    <link type="text/css" rel="stylesheet" href="styles.css">
    <link type="text/css" rel="stylesheet" href="css/login.css">
</head>
<body>
<div class="container">
    <c:url value="/login" var="loginProcessingUrl"/>
    <form class="form-signin" action="${loginProcessingUrl}" method="post">
        <!-- FormLoginConfigurer#failureUrl is /login?error -->
        <c:if test="${param.error != null}">
            <div class="alert alert-danger" role="alert">
                <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
            </div>
        </c:if>
        <!-- the configured LogoutConfigurer#logoutSuccessUrl is /login?logout -->
        <c:if test="${param.logout != null}">
            <div class="alert alert-success" role="alert">You have been logged out</div>
        </c:if>
        <div>
            <label for="username">Username</label>
            <input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus />
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" class="form-control" placeholder="Password" required />
        </div>
        <sec:csrfInput />
        <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block">Log in</button>
        </div>
    </form>
</div>
</body>
</html>
