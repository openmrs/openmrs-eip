<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<div>
    <nav class="navbar navbar-expand-lg bg-gradient-dark text-white justify-content-between">
        <span class="navbar-brand">DB SYNC</span>
        <sec:authorize access="isFullyAuthenticated()">
            <form class="form-inline" action="/logout" method="post">
                <button type="submit" class="btn btn-outline-light">Log Out</button>
                <sec:csrfInput />
            </form>
        </sec:authorize>
    </nav>
</div>
<div class="ui-bar-header"></div>
<div class="ui-bar-spacer bg-silver"></div>
