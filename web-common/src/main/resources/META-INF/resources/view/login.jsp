<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!DOCTYPE html>
<html lang="${projectLocale}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title>Log In</title>
    <link rel="icon" type="image/x-icon" href="${localeDir}favicon.${projectBuildNumber}.ico">
    <link type="text/css" rel="stylesheet" href="${localeDir}styles.${projectBuildNumber}.css">
    <link type="text/css" rel="stylesheet" href="css/login.${projectBuildNumber}.css">
</head>
<body>
<%@ include file="header.jsp" %>
<div id="loginContainer" class="container">
    <div class="alert alert-warning text-center text-dark font-weight-bold confidential-message" role="alert" >
        <div>
            <spring:message code="login.confidential.message" />
        </div>
    </div>
    <c:url value="/login" var="loginProcessingUrl"/>
    <form class="form-signin" action="${loginProcessingUrl}" method="post">
        <!-- FormLoginConfigurer#failureUrl is /login?error -->
        <c:if test="${param.error != null}">
            <div class="alert alert-danger" role="alert">
                <spring:message code="login.fail" />
            </div>
        </c:if>
        <!-- the configured LogoutConfigurer#logoutSuccessUrl is /login?logout -->
        <c:if test="${param.logout != null}">
            <div class="alert alert-success" role="alert"><spring:message code="login.logged.out" /></div>
        </c:if>
        <div>
            <label for="username" class="sr-only"><spring:message code="login.username" /></label>
            <input type="text" id="username" name="username" class="form-control" placeholder="<spring:message code="login.username" />" required autofocus />
        </div>
        <div class="form-group mt-4">
            <label for="password" class="sr-only"><spring:message code="login.password" /></label>
            <input type="password" id="password" name="password" class="form-control" placeholder="<spring:message code="login.password" />" required />
        </div>
        <sec:csrfInput />
        <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block"><spring:message code="login.label" /></button>
        </div>
    </form>
</div>
</body>
</html>
