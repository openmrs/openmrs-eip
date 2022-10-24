<%@ page import="org.openmrs.eip.app.AppUtils" %>

<div class="bg-silver text-left w-100 h-100 pl-2">
    <span id="app-version"><spring:message code="app.version" />: <%= AppUtils.getVersion() %></span>
    &nbsp;&nbsp;
    <span id="app-build-number"><spring:message code="app.build.number" />: <%= AppUtils.getBuildNumber() %></span>
</div>
