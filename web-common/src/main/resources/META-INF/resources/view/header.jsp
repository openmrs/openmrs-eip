<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<div id="header-main">
    <nav class="navbar navbar-expand-lg bg-gradient-dark text-white justify-content-between">
        <span class="navbar-brand">DB SYNC</span>
        <sec:authorize access="isFullyAuthenticated()">
            <form class="form-inline" action="/logout" method="post">
                <i class="bi bi-person ui-icon-sm"></i>
                &nbsp;
                <sec:authentication property="principal.username" />
                &nbsp;
                <button type="submit" class="btn btn-outline-light"><spring:message code="logout.label" /></button>
                <sec:csrfInput />
            </form>
        </sec:authorize>
    </nav>
</div>
<div id="ui-bar-header"></div>
<div class="ui-bar-spacer bg-silver"></div>
