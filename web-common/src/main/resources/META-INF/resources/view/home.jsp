<!doctype html>
<html lang="${projectLocale}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>DBsync</title>
    <base href="/">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <link rel="icon" type="image/x-icon" href="${localeDir}favicon.${projectBuildNumber}.ico">
    <link rel="stylesheet" href="${localeDir}styles.${projectBuildNumber}.css">
</head>
<body class="bg-light-silver">
    <div id="body-header" class="position-absolute w-100">
        <%@ include file="header.jsp" %>
    </div>
    <div id="body-main" class="position-absolute w-100">
        <app-root></app-root>
    </div>
    <div id="body-footer" class="position-absolute w-100">
        <%@ include file="footer.jsp" %>
    </div>
<script src="${localeDir}runtime.${projectBuildNumber}.js" defer></script>
<script src="${localeDir}polyfills.${projectBuildNumber}.js" defer></script>
<script src="${localeDir}scripts.${projectBuildNumber}.js" defer></script>
<script src="${localeDir}main.${projectBuildNumber}.js" defer></script>
</body>
</html>
