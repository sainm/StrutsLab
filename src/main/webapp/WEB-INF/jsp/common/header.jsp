<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<span class="title"><bean:message key="app.title"/></span>
<%
    String user = (String) session.getAttribute("loginUser");
    if (user != null) {
%>
    <span class="user-info">
        ログインユーザー: <%= user %> &nbsp;
        <a href="<%=request.getContextPath()%>/logout.do">ログアウト</a>
    </span>
<%
    }
%>
