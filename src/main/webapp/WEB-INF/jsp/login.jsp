<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>ログイン — <bean:message key="app.title"/></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<div style="width:400px;margin:100px auto;border:1px solid #ccc;padding:24px;">
<h2 style="text-align:center;">電力設備巡視点検管理システム</h2>
<%
    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg != null) {
%>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;">
        <%= errMsg %>
    </div>
<%
    }
%>
<html:form action="/login" method="post">
<table class="form-table" style="width:100%;">
    <tr><th>ログインID</th><td><html:text property="loginId"/></td></tr>
    <tr><th>パスワード</th><td><html:password property="password"/></td></tr>
</table>
<div style="text-align:center;margin-top:16px;">
    <html:submit value="ログイン"/>
</div>
</html:form>
</div>
</body>
</html>
