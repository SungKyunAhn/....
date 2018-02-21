<%-- SiteMesh has a bug where error pages aren't decorated - hence the full HTML --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
        
<%@ include file="/taglibs.jsp"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Page Not Found</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <c:set var="ctx" value="${pageContext.request.contextPath}"/>
    <link rel="shortcut icon" href="${ctx}/images/favicon.ico" type="image/x-icon"/>
    <!-- <link rel="stylesheet" type="text/css" href="${ctx}/styles/deliciouslyblue/theme.css" title="default" /> -->
    <!-- <link rel="alternate stylesheet" type="text/css" href="${ctx}/styles/deliciouslygreen/theme.css" title="green" />-->
    <script type="text/javascript" src="${ctx}/scripts/prototype.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/scriptaculous.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/stylesheetswitcher.js"></script>
    <script type="text/javascript" src="${ctx}/scripts/global.js"></script>
</head>
<body>
Page Not Found
</body>
</html>
