<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title><tiles:getAsString name="title"/></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<div id="header"><tiles:insert attribute="header"/></div>
<tiles:insert attribute="body"/>
</body>
</html>
