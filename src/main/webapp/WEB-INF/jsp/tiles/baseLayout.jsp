<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title><tiles:getAsString name="title"/> — <bean:message key="app.title"/></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<div id="header"><tiles:insert attribute="header"/></div>
<div id="menu"><tiles:insert attribute="menu"/></div>
<div id="body"><tiles:insert attribute="body"/></div>
<div id="footer"><tiles:insert attribute="footer"/></div>
</body>
</html>
