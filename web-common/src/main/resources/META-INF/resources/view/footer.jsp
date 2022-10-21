<%@ page import="org.openmrs.eip.app.AppUtils" %>

<div class="bg-silver text-center w-100 h-100">
    <span id="appVersion"><spring:message code="app.version" />: <%= AppUtils.getVersion() %></span>
</div>
