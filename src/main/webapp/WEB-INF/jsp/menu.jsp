<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title><bean:message key="app.title"/></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<div id="header">
    <span class="title"><bean:message key="app.title"/></span>
    <span class="user-info">
        ログインユーザー: <%= HtmlUtil.escape((String) session.getAttribute("loginUser")) %> &nbsp;
        <a href="<%=request.getContextPath()%>/logout.do">ログアウト</a>
    </span>
</div>
<div id="menu"><jsp:include page="/WEB-INF/jsp/common/menu.jsp"/></div>
<div id="body">
    <h1>メインメニュー</h1>
    <p>ようこそ、<%= HtmlUtil.escape((String) session.getAttribute("loginUser")) %> さん。</p>
</div>
<div id="footer"><jsp:include page="/WEB-INF/jsp/common/footer.jsp"/></div>
</body>
</html>
